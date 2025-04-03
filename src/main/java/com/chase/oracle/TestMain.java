package com.chase.oracle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.chase.portfolio.EnvLoader;
import com.chase.portfolio.services.OCIStorageService;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;

public class TestMain {
	
	private static boolean isInProject()
	{
		return !(new File(".project").exists());
	}
	
	public static void main(String[] args)
	{
		
		try {
			InputStream input = new FileInputStream(new File("src/main/resources/static_index"));
			String fileContent = new String(input.readAllBytes(), StandardCharsets.UTF_8);
			System.out.println(fileContent);
			HashSet<String> list = new HashSet<String>(Arrays.asList(fileContent.split("\\r?\\n"))); // Split into lines
			input.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Decode to String
		
	}
	
	/*
	 * so can do this, when in dev. delete and upload the new static_index file to oci everytime
	 * 
	 */
	
	
	
	
	
	private static File getResourceFile(String filePath)
	{
		URL resourceUrl = TestMain.class.getClassLoader().getResource(filePath);
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
	
	private static List<String> getLocalResources()
	{
		List<String> resources = new ArrayList<String>();
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
		for (String file : getDirResources("static/texts"))
		{
			resources.add("texts/" + file);
		}
		//resources.add("videos/background.mp4");	
		return resources;
	}
	
	
	
	private static void readFiles(File folder, List<String> files, Path basePath)
	{
		File[] folder_files = folder.listFiles(); // List all files and subdirectories

        if (folder_files != null) {
            for (File file : folder_files) {
                if (file.isFile()) {
                    // Get the relative path
                    Path relativePath = basePath.relativize(file.toPath());
                    files.add(relativePath.toString());
                  
                } else if (file.isDirectory()) {
                    // Recursively read subdirectories
                    readFiles(file, files, basePath);
                }
            }
        }

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
	
	private static AbstractAuthenticationDetailsProvider authenticateAPI()
	{	
		//./private.pem
		try
		{
			ConfigFileReader.ConfigFile configFile = null;
			if (!isInProject())
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
	
	public static void other()
	{
		String folder = "static/images"; // e.g., src/main/resources/assets/
		InputStream resourceStream = TestMain.class.getClassLoader().getResourceAsStream(folder);
        if (resourceStream == null) {
            throw new IllegalArgumentException("❌ Folder not found: " + folder);
        }

        // Read directory listing
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
            for (String file : reader.lines().collect(Collectors.toList())) {
                System.out.println("Found resource: " + file);
            }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String filePath = "static/images/code.png"; // Path inside src/main/resources/

        // Get file as InputStream
        try (InputStream inputStream = TestMain.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("❌ File not found: " + filePath);
            }

            // Read file content
            String content = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            System.out.println("✅ File Content:\n" + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	

}
