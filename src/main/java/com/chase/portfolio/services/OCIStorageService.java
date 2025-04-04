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
import org.springframework.stereotype.Service;

import com.chase.portfolio.EnvLoader;
import com.chase.portfolio.PortfolioApplication;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequestSummary;
import com.oracle.bmc.objectstorage.model.StorageTier;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.ListPreauthenticatedRequestsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.responses.ListPreauthenticatedRequestsResponse;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class OCIStorageService {
	
	private static final String Bucket_Name = EnvLoader.get().bucket_Name;
	private static final String Bucket_Namespace = EnvLoader.get().bucket_Namespace;
	private static final Region Bucket_Region = Region.fromRegionId(EnvLoader.get().bucket_Region);
	private static final Logger logger = LoggerFactory.getLogger(OCIStorageService.class);
	
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
            
            createPreAuthURLs(ResourceService.getStaticIndex());
        };

        // Schedule to run every 23 hours
        scheduler.scheduleAtFixedRate(task, 23, 23, TimeUnit.HOURS);
        //scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.MINUTES);
    }
	
	private void updateBucketResources(HashMap<String, String> index_resources)
	{
		if (!PortfolioApplication.isInProject())
			return;
		
		//first need to get index
		for (Map.Entry<String, String> local_file : index_resources.entrySet())
		{
			
			File file = ResourceService.getIndexFile(local_file.getKey());
			
			if (file.isDirectory())
				continue;
			System.out.println("Index = " + local_file.getValue() + ", Local = " + ResourceService.getFileMd5(file));
			if (local_file.getValue().equalsIgnoreCase(ResourceService.getFileMd5(file)))
				continue;
			//should probably only upload it if on project
    		uploadResource(local_file.getKey(), ResourceService.getResourceStream(file), file.length());
    		logger.info("Successfully Uploaded File = " + local_file);
		}
		ResourceService.updateStaticIndex(false);
		logger.info("Uploaded Resources To Bucket");
	}
	
	private void syncResourcesWithBucket()
	{
		//the locals will always be more
		//so should
		
		lock.writeLock().lock();
        try {
        	
        	HashMap<String, String> index_files = ResourceService.getStaticIndex();
        	System.out.println(index_files.size());
    		updateBucketResources(index_files);
    		clearPreAuthURLs();
    		createPreAuthURLs(index_files);
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
	
//	private HashMap<String, ObjectSummary> getCloudResources() 
//	{
//		HashMap<String, ObjectSummary> resources = new HashMap<String, ObjectSummary>();
//		ListObjectsResponse response = client.listObjects(
//		        ListObjectsRequest.builder()
//		                .namespaceName(Bucket_Namespace)
//		                .bucketName(Bucket_Name)
//		                .build());
//		response.getListObjects().getObjects().forEach(object ->
//		{
//	        resources.put(object.getName(), object);
//		}
//		);
//		return resources;
//	}
	
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
