package com.chase.oracle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.chase.portfolio.services.OCIStorageService;

public class TestMain {
	
	public static void main(String[] args)
	{
		OCIStorageService api = OCIStorageService.setupAPI();
		
		//File file = OCIStorageAPI.getResourceFile("static/images/amazon.png");
		//InputStream is = OCIStorageAPI.getResourceStream(file);
		//api.uploadFile("images/amazon.png", is, file.length());
		/*
		 * https://objectstorage.eu-frankfurt-1.oraclecloud.com/p/n8Jx6sJrWkJCi5q4pVrL5adXHpDzUuQAMXdr4qsaI-F1X_zJefk8CKW5VFvo8ws5/n/frcxzo8ihnil/b/portfolio-bucket/o/images/amazon.png
		 */
		//api.clearPreAuthURLs();
		Scanner scanner = new Scanner(System.in);
		int i = 1;
		while (i != -1)
		{
			i = scanner.nextInt();
		}
		api.close();
		scanner.close();
		//OCIStorageAPI.getTest();
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
