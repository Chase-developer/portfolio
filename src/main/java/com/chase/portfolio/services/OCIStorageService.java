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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	
	private static HashSet<String> getLocalResources()
	{
		HashSet<String> resources = new HashSet<String>();
		for (String file : getDirResources("static/images"))
		{
			resources.add("images/" + file);
		}
		for (String file : getDirResources("static/images/badge"))
		{
			resources.add("images/badge/" + file);
		}
		for (String file : getDirResources("static/images/logo"))
		{
			resources.add("images/logo/" + file);
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
	
	public static int getHoursLeft(Date date) {
	    long diffMillis = Math.abs(date.getTime() - new Date().getTime());
	    return (int) Math.floor((double) diffMillis / (1000 * 60 * 60)); // Convert milliseconds to hours
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
	
	private void startScheduler() {
        Runnable task = () -> {
            clearPreAuthURLs();
            
            createPreAuthURLs(getLocalResources());
        };

        // Schedule to run every 23 hours
        scheduler.scheduleAtFixedRate(task, 23, 23, TimeUnit.HOURS);
        //scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.MINUTES);
    }
	
	private void syncResourcesWithBucket()
	{
		//the locals will always be more
		//so should
		
		lock.writeLock().lock();
        try {
        	HashSet<String> files = getCloudResources();
    		for (String local_file : getLocalResources())
    		{
    			logger.info("Local File = " + local_file);
    			if (files.contains(local_file))
    				continue;
    			File file = getResourceFile("static/" + local_file);
    			if (file.isDirectory())
    				continue;
        		uploadResource(local_file, getResourceStream(file), file.length());
        		logger.info("Successfully Uploaded");
    		}
    		logger.info("Uploaded Resources To Bucket");
    		clearPreAuthURLs();
    		createPreAuthURLs(files);
        } finally
        {
        	lock.writeLock().unlock();
        }
		

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
	
//	private void getObjectPreAuth(String objectname, String parId)
//	{
//		GetPreauthenticatedRequestRequest request = GetPreauthenticatedRequestRequest.builder()
//                .namespaceName(Bucket_Namespace)
//                .bucketName(Bucket_Name)
//                .parId(parId)  // The ID of the pre-authenticated request
//                .build();
//
//        GetPreauthenticatedRequestResponse response = client.getPreauthenticatedRequest(request);
//        response.getPreauthenticatedRequest().
//	}
//	
//	//summary.getAccessUri(), summary.getTimeExpires().getMillis()
//	//https://objectstorage.eu-frankfurt-1.oraclecloud.com/p/n8Jx6sJrWkJCi5q4pVrL5adXHpDzUuQAMXdr4qsaI-F1X_zJefk8CKW5VFvo8ws5/n/frcxzo8ihnil/b/portfolio-bucket/o/images/amazon.png
//	private void fetchPreAuthURLs(HashSet<String> objects) {
//	    min_hours_left = 23;
//	    try {
//	        ListPreauthenticatedRequestsRequest request = ListPreauthenticatedRequestsRequest.builder()
//	                .bucketName(Bucket_Name)
//	                .namespaceName(Bucket_Namespace)
//	                .build();
//
//	        ListPreauthenticatedRequestsResponse response = client.listPreauthenticatedRequests(request);
//	        
//	        int i = 0;
//	        for (PreauthenticatedRequestSummary summary : response.getItems()) {
//	        	if (objects.contains(summary.getObjectName()))
//	        	{
//	        		
//	        		String url = String.format("https://objectstorage.%s.oraclecloud.com/p/%s/n/%s/b/%s/o/%s",
//	                        Bucket_Region, summary.getId().split(":")[0], Bucket_Namespace, Bucket_Name, summary.getObjectName());
//	        		preAuthUrls.put(summary.getObjectName(), 
//	        				url);
//	        		min_hours_left = Math.min(min_hours_left, getHoursLeft(summary.getTimeExpires()));
//	        		i++;
//	        		logger.info("Fetched File " + summary.getObjectName() + ", URL " + url);
//	        	}
//	        	else
//	        	{
//	        		client.deletePreauthenticatedRequest(
//	    		            DeletePreauthenticatedRequestRequest.builder()
//	    		                    .namespaceName(Bucket_Namespace)
//	    		                    .bucketName(Bucket_Name)
//	    		                    .parId(summary.getId())
//	    		                    .build()
//	    		    );
//	        		logger.info("Deleted Old File " + summary.getObjectName());
//	        	}
//	        }
//
//	        logger.info("Fetched " + i + " pre-auth URLs from OCI. Will re-sync in " + min_hours_left + " hours");
//	    } catch (Exception e) {
//	        logger.error("Error fetching pre-authenticated requests: ", e);
//	    }
//	}
	/*
	 * two scnearios
	 * 
	 * one when programs starts
	 * it first uploads all the files
	 * then it checks if there are any existing pre auth urls
	 * if there is, add into the hashmap
	 * fetchPreAuthURLs. then gets the earliest expiry
	 * then add the rest createPreAuthURLs
	 * then start scheduler
	 * finished
	 * 
	 * when program scheduler runs
	 * clears all urls
	 * create all urls
	 * 
	 * 
	 */
	
	
	
	private void createPreAuthURLs(HashSet<String> objects)
	{
		for (String local_file : objects)
		{
			if (preAuthUrls.containsKey(local_file))
				continue;
			logger.info("Create File URL = " + local_file);
			preAuthUrls.put(local_file, generatePreAuthUrl(local_file));
		}
		logger.info("Finished Generation of Pre-Auth URLs");
			
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
	
	private HashSet<String> getCloudResources() 
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
	
	private void uploadResource(String objectName, InputStream inputStream, long file_size) {
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
		    //System.out.println("Deleted Pre-Auth Request: " + parId);
		}
		preAuthUrls.clear();
		logger.info("Cleared Existing Pre-Auth URLs");
	}
	
	public void close()
	{
		client.close();
	}

}
