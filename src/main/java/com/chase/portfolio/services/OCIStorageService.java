package com.chase.portfolio.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.chase.portfolio.EnvLoader;
import com.chase.portfolio.PortfolioApplication;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequestSummary;
import com.oracle.bmc.objectstorage.model.StorageTier;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.ListPreauthenticatedRequestsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.responses.DeleteObjectResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.responses.ListPreauthenticatedRequestsResponse;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
@Profile("!test")
public class OCIStorageService {
	
	
	
//	static
//	{
//		EnvReader reader = new EnvReader();
//		Bucket_Name = reader.getBucketName();
//		Bucket_Namespace = reader.getBucketNamespace();
//		Bucket_Region = Region.fromRegionId(reader.getBucketRegion());
//	}

//	private static final String OCI_METADATA_URL = "http://169.254.169.254/opc/v2/instance/";

//    private static boolean isRunningOnOracleCloud() {
//        try {
//            URL url = new URL(OCI_METADATA_URL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Authorization", "Bearer Oracle");
//            connection.setConnectTimeout(2000); // 2-second timeout
//            connection.setReadTimeout(2000);
//
//            int responseCode = connection.getResponseCode();
//            return responseCode == 200; // 200 means it's running on OCI
//        } catch (IOException e) {
//            return false; // If request fails, assume it's not on OCI
//        }
//    }
    
    
	
	private static AbstractAuthenticationDetailsProvider authenticateAPI()
	{	
		try
		{
			ConfigFileReader.ConfigFile configFile = null;
			if (!PortfolioApplication.isInProject())
			{
				String currentDirectory = Paths.get("").toAbsolutePath().toString(); // Get the current directory
		        String customConfigFilePath = Paths.get(currentDirectory, "oci-config").toString();
		        configFile = ConfigFileReader.parse(customConfigFilePath);
			}
			else
				configFile = ConfigFileReader.parseDefault();
				
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
	
	
	
	public static int getHoursLeft(Date date) {
	    long diffMillis = Math.abs(date.getTime() - new Date().getTime());
	    return (int) Math.floor((double) diffMillis / (1000 * 60 * 60)); // Convert milliseconds to hours
	}
	
	private static boolean isTestProfile() {
        return "test".equals(System.getProperty("spring.profiles.active"));
    }
	
	
	
	
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private ObjectStorageClient client;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<String, String> preAuthUrls = new HashMap<>();
	
	private String bucket_Name;
	private String bucket_Namespace;
	private Region bucket_Region;
	private Logger logger = LoggerFactory.getLogger(OCIStorageService.class);
	
//	public OCIStorageAPI(AbstractAuthenticationDetailsProvider provider)
//	{
//		this.client = ObjectStorageClient.builder().region(Bucket_Region).build(provider);
//		syncResourcesWithBucket();
//		startScheduler();
//	}
	
	
	@PostConstruct
    public void initializeOracleClient() {
		if (isTestProfile()) {
            return;  // Skip initialization in test environment
        }
		this.bucket_Name = EnvLoader.get().bucket_Name;
		this.bucket_Namespace = EnvLoader.get().bucket_Namespace;
		this.bucket_Region = Region.fromRegionId(EnvLoader.get().bucket_Region);
		this.client = ObjectStorageClient.builder().region(bucket_Region).build(authenticateAPI());
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
            
            createPreAuthURLs(ResourceService.getStaticIndex());
        };

        // Schedule to run every 23 hours
        scheduler.scheduleAtFixedRate(task, 23, 23, TimeUnit.HOURS);
        //scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.MINUTES);
    }
	
	private void updateBucketResources()
	{
		if (!PortfolioApplication.isInProject())
			return;
		
		//first need to get index
		HashMap<String, String> static_index = ResourceService.getStaticIndex();
		//that's why, I need the old hash but read index returns new everything, including the new hash
		//I need to rethink this then
		//I still need the new index. but then I should get the hash from static index, if it doesn't exist, that means it should upload
		//if it exists but it's the wrong hash, then should also upload
		//so that means if it 
		HashMap<String, String> read_index = ResourceService.readIndex();
		for (Map.Entry<String, String> local_file : read_index.entrySet())
		{
			
			File file = ResourceService.getIndexFile(local_file.getKey());
			if (file == null)
				continue;
			if (file.isDirectory())
				continue;
			//System.out.println("File " + local_file.getKey() + ", Index = " + local_file.getValue() + ", Local = " + ResourceService.getFileMd5(file));
			//need to check if it's a file not in the static index
			//System.out.println("Has Static Index = " + static_index.containsKey(local_file.getKey()))
			String md5hash = static_index.get(local_file.getKey());
			if (md5hash != null && md5hash.equalsIgnoreCase(local_file.getValue()))
				continue;
			//should probably only upload it if on project
    		uploadResource(local_file.getKey(), ResourceService.getResourceStream(file), file.length());
    		logger.info("Successfully Uploaded File = " + local_file);
		}
		for (Map.Entry<String, ObjectSummary> index : getCloudResources().entrySet())
		{
			if (!read_index.containsKey(index.getKey()))
				deleteResource(index.getKey());
		}
		ResourceService.updateStaticIndex(false);
		logger.info("Synced Resources To Bucket");
	}
	
	private void syncResourcesWithBucket()
	{
		//the locals will always be more
		//so should
		
		lock.writeLock().lock();
        try {
    		updateBucketResources();
    		clearPreAuthURLs();
    		createPreAuthURLs(ResourceService.getStaticIndex());
        } finally
        {
        	lock.writeLock().unlock();
        }
		

	}
	
	@Nullable
	public String getPreAuthURL(String objectname)
	{
		lock.readLock().lock();
        try {
        	return preAuthUrls.get(objectname);
        } finally {
            lock.readLock().unlock();
        }
	}
	
	private void createPreAuthURLs(HashMap<String, String> objects)
	{
		objects.put("videos/background.webm", "");
		for (Map.Entry<String, String> local_file : objects.entrySet())
		{
			if (preAuthUrls.containsKey(local_file.getKey()))
				continue;
			logger.info("Create File URL = " + local_file.getKey());
			preAuthUrls.put(local_file.getKey(), generatePreAuthUrl(local_file.getKey()));
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
                .namespaceName(bucket_Namespace)
                .bucketName(bucket_Name)
                .createPreauthenticatedRequestDetails(details)
                .build();

        // Execute request
        CreatePreauthenticatedRequestResponse response = client.createPreauthenticatedRequest(request);
        // Construct the URL
        return "https://objectstorage." + bucket_Region.getRegionId() +
                ".oraclecloud.com" + response.getPreauthenticatedRequest().getAccessUri();
    }
	
	private HashMap<String, ObjectSummary> getCloudResources() 
	{
		HashMap<String, ObjectSummary> resources = new HashMap<String, ObjectSummary>();
		ListObjectsResponse response = client.listObjects(
		        ListObjectsRequest.builder()
		                .namespaceName(bucket_Namespace)
		                .bucketName(bucket_Name)
		                .build());
		response.getListObjects().getObjects().forEach(object ->
		{
	        resources.put(object.getName(), object);
		}
		);
		return resources;
	}
	
	private void deleteResource(String objectName)
	{
		DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .namespaceName(bucket_Namespace)
                .bucketName(bucket_Name)
                .objectName(objectName)
                .build();

        DeleteObjectResponse deleteResponse = client.deleteObject(deleteRequest);
        logger.info(deleteResponse.get__httpStatusCode__() + " Deleted outdated object: " + objectName);
	}
	
	private void uploadResource(String objectName, InputStream inputStream, long file_size) {
        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(bucket_Namespace)
                .bucketName(bucket_Name)
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
		                .namespaceName(bucket_Namespace)
		                .bucketName(bucket_Name)
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
		                    .namespaceName(bucket_Namespace)
		                    .bucketName(bucket_Name)
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
