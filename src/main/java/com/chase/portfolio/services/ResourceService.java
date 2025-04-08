package com.chase.portfolio.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chase.portfolio.PortfolioApplication;

public class ResourceService {
	
	public static String getFileMd5(File file) {
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");

	        try (InputStream is = new FileInputStream(file)) {
	            byte[] buffer = new byte[4096];
	            while (is.read(buffer) != -1) {
	                md.update(buffer);
	            }
	        }

	        // Convert the MD5 hash to Base64 format
	        byte[] digestBytes = md.digest();
	        return Base64.getEncoder().encodeToString(digestBytes);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
        
    }

	
	private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
	
	public static File getIndexFile(String file)
	{
		File idx_file = new File("index/" + file);
		return idx_file.exists() ? idx_file : null;
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
	
	public static void main(String[] args)
	{
		updateStaticIndex(true);
		for (Map.Entry<String, String> entry : getStaticIndex().entrySet()) {
            // Write the key and value in the format "key=value"
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
		
	}
	
	public static HashMap<String, String> getStaticIndex() {
	    HashMap<String, String> indexMap = new HashMap<>();
	    
	    try (InputStream input = getResourceStream("static_index")) {
	        String fileContent = new String(input.readAllBytes(), StandardCharsets.UTF_8);
	        
	        // Split the content by lines
	        String[] lines = fileContent.split("\\r?\\n");	
	        
	        // Loop through each line and split by '=' to get key-value pairs
	        for (String line : lines) {
	            // Split the line at the first '=' character
	            String[] parts = line.split("=", 2); // 2 to ensure only two parts (key, value)
	            
	            if (parts.length == 2) {
	                // Add the key-value pair to the HashMap
	                indexMap.put(parts[0], parts[1]);
	                //System.out.println(parts[0] + ", " + parts[1]);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return indexMap;
	}
	
	public static HashMap<String, String> readIndex()
	{
		HashMap<String, String> files = new HashMap<String, String>();
		String baseDir = "index/";
        File folder = new File(baseDir);

        if (folder.exists() && folder.isDirectory()) {
            // Start recursive file reading
            readFiles(folder, files, Paths.get(baseDir));
        } else {
            System.out.println("The specified folder does not exist or is not a directory.");
        }
        return files;

	}
	
	public static void updateStaticIndex(boolean force)
	{
		if (!PortfolioApplication.isInProject())
		{
			logger.error("updateStaticIndex() cannot be run inside Compute!");
			return;
		}
		HashMap<String, String> files = readIndex();
		
		File file = new File("src/main/resources/static_index");
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeIndex(file, files, force);
	}
	
	private static void writeIndex(File file, HashMap<String, String> contents, boolean force) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
	        // Loop through each entry in the HashMap
	        for (Map.Entry<String, String> entry : contents.entrySet()) {
	            // Write the key and value in the format "key=value"
	            writer.write(entry.getKey() + "=" + (force ? "0" : entry.getValue()));
	            writer.newLine(); // Add a new line after each key-value pair
	        }
	        //System.out.println("Key-value pairs written to the file successfully!");
	    } catch (IOException e) {
	        System.err.println("An error occurred while writing to the file: " + e.getMessage());
	    }
	}
	
	private static void readFiles(File folder, HashMap<String, String> files, Path basePath)
	{
		File[] folder_files = folder.listFiles(); // List all files and subdirectories

        if (folder_files != null) {
            for (File file : folder_files) {
                if (file.isFile()) {
                    // Get the relative path
                    Path relativePath = basePath.relativize(file.toPath());
                    String normalizedPath = relativePath.toString().replace("\\", "/");
                    files.put(normalizedPath, getFileMd5(file));
                  
                } else if (file.isDirectory()) {
                    // Recursively read subdirectories
                    readFiles(file, files, basePath);
                }
            }
        }

	}

}
