package com.chase.oracle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class EnvReader {
	
	public static void main(String[] args)
	{
		Properties properties = new Properties();
	    try (FileInputStream fileInputStream = new FileInputStream(".env")) {
	        properties.load(fileInputStream);
	        String bucketName = properties.getProperty("bucket-name");
	        String namespace = properties.getProperty("bucket-namespace");
	        String region = properties.getProperty("bucket-region");

	        System.out.println("Bucket Name: " + bucketName);
	        System.out.println("Namespace: " + namespace);
	        System.out.println("Region: " + region);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private final Properties properties = new Properties();
	
	public EnvReader()
	{
		try (FileInputStream fileInputStream = new FileInputStream(".env")) {
	        properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getBucketName()
	{
		return properties.getProperty("bucket-name");
	}
	
	
	public String getBucketNamespace()
	{
		return properties.getProperty("bucket-namespace");
	}
	
	public String getBucketRegion()
	{
		return properties.getProperty("bucket-region");
	}
	
	

}
