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
import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chase.portfolio.PortfolioApplication;

public class ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
	
	public static File getIndexFile(String file)
	{
		return new File("index/" + file);
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
		updateStaticIndex();
		for (String s : getStaticIndex())
		{
			System.out.println(s);
		}
	}
	
	public static HashSet<String> getStaticIndex()
	{
		try (InputStream input = getResourceStream("static_index");)
		{
			String fileContent = new String(input.readAllBytes(), StandardCharsets.UTF_8);
			return new HashSet<String>(Arrays.asList(fileContent.split("\\r?\\n"))); // Split into lines
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static HashSet<String> readIndex()
	{
		HashSet<String> files = new HashSet<String>();
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
	
	public static void updateStaticIndex()
	{
		if (!PortfolioApplication.isInProject())
		{
			logger.error("updateStaticIndex() cannot be run inside Compute!");
			return;
		}
		HashSet<String> files = readIndex();
		File file = new File("src/main/resources/static_index");
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeIndex(file, files);
	}
	
	private static void writeIndex(File file, HashSet<String> contents)
	{
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write each string to the file
            for (String line : contents) {
                writer.write(line);
                writer.newLine(); // Add a new line after each string
            }
            //System.out.println("Strings written to the file successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }

	}
	
	private static void readFiles(File folder, HashSet<String> files, Path basePath)
	{
		File[] folder_files = folder.listFiles(); // List all files and subdirectories

        if (folder_files != null) {
            for (File file : folder_files) {
                if (file.isFile()) {
                    // Get the relative path
                    Path relativePath = basePath.relativize(file.toPath());
                    String normalizedPath = relativePath.toString().replace("\\", "/");
                    files.add(normalizedPath);
                  
                } else if (file.isDirectory()) {
                    // Recursively read subdirectories
                    readFiles(file, files, basePath);
                }
            }
        }

	}

}
