package com.chase.portfolio.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.chase.oracle.EnvReader;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequestSummary;
import com.oracle.bmc.objectstorage.model.StorageTier;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.ListPreauthenticatedRequestsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.responses.ListPreauthenticatedRequestsResponse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class OCIStorageService {
	
	private static final String Bucket_Name;
	private static final String Bucket_Namespace;
	private static final Region Bucket_Region;
	private static final Logger logger = LoggerFactory.getLogger(OCIStorageService.class);
	
	static
	{
		EnvReader reader = new EnvReader();
		Bucket_Name = reader.getBucketName();
		Bucket_Namespace = reader.getBucketNamespace();
		Bucket_Region = Region.fromRegionId(reader.getBucketRegion());
	}

	private static final String OCI_METADATA_URL = "http://169.254.169.254/opc/v2/instance/";

    private static boolean isRunningOnOracleCloud() {
        try {
            URL url = new URL(OCI_METADATA_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer Oracle");
            connection.setConnectTimeout(2000); // 2-second timeout
            connection.setReadTimeout(2000);

            int responseCode = connection.getResponseCode();
            return responseCode == 200; // 200 means it's running on OCI
        } catch (IOException e) {
            return false; // If request fails, assume it's not on OCI
        }
    }
	
	private static AbstractAuthenticationDetailsProvider authenticateAPI()
	{	
		try {
			if (isRunningOnOracleCloud())
			{
				return InstancePrincipalsAuthenticationDetailsProvider.builder().build();
			}
			ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
			return new ConfigFileAuthenticationDetailsProvider(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
	
//	public static OCIStorageAPI setupAPI()
//	{
//		return new OCIStorageAPI(authenticateAPI());
//	}
	
	private static List<String> getLocalResources()
	{
		List<String> resources = new ArrayList<String>();
		for (String file : getDirResources("static/images"))
		{
			resources.add("images/" + file);
		}
		for (String file : getDirResources("static/fonts"))
		{
			resources.add("fonts/" + file);
		}
		return resources;
	}
	
	private static List<String> getDirResources(String resource_dir)
	{
		InputStream resourceStream = OCIStorageService.class.getClassLoader().getResourceAsStream(resource_dir);
        if (resourceStream == null) {
            throw new IllegalArgumentException("Folder not found: " + resource_dir);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	private static File getResourceFile(String filePath)
	{
		URL resourceUrl = OCIStorageService.class.getClassLoader().getResource(filePath);
		if (resourceUrl == null) {
		    throw new IllegalArgumentException("File not found: " + filePath);
		}
		try {
			return new File(resourceUrl.toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream getResourceStream(String filePath)
	{
		return OCIStorageService.class.getClassLoader().getResourceAsStream(filePath);
	}
	
	public static InputStream getResourceStream(File file)
	{
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private ObjectStorageClient client;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<String, String> preAuthUrls = new HashMap<>();
	
//	public OCIStorageAPI(AbstractAuthenticationDetailsProvider provider)
//	{
//		this.client = ObjectStorageClient.builder().region(Bucket_Region).build(provider);
//		syncResourcesWithBucket();
//		startScheduler();
//	}
	
	@PostConstruct
    public void initializeOracleClient() {
		this.client = ObjectStorageClient.builder().region(Bucket_Region).build(authenticateAPI());
		syncResourcesWithBucket();
		startScheduler();
	}
	
	@PreDestroy
    public void closeOracleClient() {
        if (client != null) {
        	client.close(); // Closes the client properly
            logger.info("Oracle Object Storage Client closed.");
        }
    }
	
	public void startScheduler() {
        Runnable task = () -> {
        	logger.info("Fetching Pre-Auth URLs...");
            fetchPreAuthURLs(); // Call your method
        };

        // Schedule to run every 23 hours
        scheduler.scheduleAtFixedRate(task, 0, 23, TimeUnit.HOURS);
        //scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.MINUTES);
    }
	
	private void syncResourcesWithBucket()
	{
		//the locals will always be more
		//so should
		
		Set<String> files = getFiles();
		for (String local_file : getLocalResources())
		{
			if (files.contains(local_file))
				continue;
			File file = getResourceFile("static/" + local_file);
    		uploadFile(local_file, getResourceStream(file), file.length());
		}
		logger.info("Uploaded Resources To Bucket");
		fetchPreAuthURLs();
	}
	
	public String getPreAuthURL(String objectname)
	{
		lock.readLock().lock();
        try {
        	return preAuthUrls.get(objectname);
        } finally {
            lock.readLock().unlock();
        }
	}
	
	private void fetchPreAuthURLs()
	{
		lock.writeLock().lock();
        try {
        	clearPreAuthURLs();
        	logger.info("Cleared Existing Pre-Auth URLs");
        	for (String local_file : getLocalResources())
    			preAuthUrls.put(local_file, generatePreAuthUrl(local_file));
        	logger.info("Finished Generation Pre-Auth URLs");
        } finally {
            lock.writeLock().unlock();
        }
	}
	
	public String generatePreAuthUrl(String objectName) {
        // Set expiration time (e.g., 7 days from now)
        Instant expiration = Instant.now().plus(1, ChronoUnit.DAYS);

        // Define PAR details
        CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .name("PreAuth-" + objectName) // Custom name for tracking
                .objectName(objectName) // Target object
                .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead) // Read-only
                .timeExpires(Date.from(expiration)) // Expiry date
                .build();

        // Create the request
        CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(Bucket_Namespace)
                .bucketName(Bucket_Name)
                .createPreauthenticatedRequestDetails(details)
                .build();

        // Execute request
        CreatePreauthenticatedRequestResponse response = client.createPreauthenticatedRequest(request);
        // Construct the URL
        return "https://objectstorage." + Bucket_Region.getRegionId() +
                ".oraclecloud.com" + response.getPreauthenticatedRequest().getAccessUri();
    }
	
	private HashSet<String> getFiles() 
	{
		HashSet<String> resources = new HashSet<String>();
		ListObjectsResponse response = client.listObjects(
		        ListObjectsRequest.builder()
		                .namespaceName(Bucket_Namespace)
		                .bucketName(Bucket_Name)
		                .build());
		response.getListObjects().getObjects().forEach(object -> 
		        resources.add(object.getName()));
		return resources;
	}
	
	private void uploadFile(String objectName, InputStream inputStream, long file_size) {
        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(Bucket_Namespace)
                .bucketName(Bucket_Name)
                .objectName(objectName)
                .contentLength(file_size)
                .putObjectBody(inputStream)
                .storageTier(StorageTier.Standard)
                .build();

        client.putObject(request);

        try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	private void clearPreAuthURLs() {
		ListPreauthenticatedRequestsResponse response = client.listPreauthenticatedRequests(
		        ListPreauthenticatedRequestsRequest.builder()
		                .namespaceName(Bucket_Namespace)
		                .bucketName(Bucket_Name)
		                .build()
		);
//		if (response.getItems().isEmpty())
//			return;
		List<String> preAuthIdsToDelete = response.getItems().stream()
		        //filter(par -> preAuthUrls.containsKey(par.getObjectName()))
		        .map(PreauthenticatedRequestSummary::getId)
		        .collect(Collectors.toList());
		for (String parId : preAuthIdsToDelete) {
		    client.deletePreauthenticatedRequest(
		            DeletePreauthenticatedRequestRequest.builder()
		                    .namespaceName(Bucket_Namespace)
		                    .bucketName(Bucket_Name)
		                    .parId(parId)
		                    .build()
		    );
		    System.out.println("Deleted Pre-Auth Request: " + parId);
		}
	}
	
	public void close()
	{
		client.close();
	}

}
