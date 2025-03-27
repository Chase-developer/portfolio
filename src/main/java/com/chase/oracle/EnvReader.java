package com.chase.oracle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.oracle.bmc.Region;

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
	
	public static final String Bucket_Name;
	public static final String Bucket_Namespace;
	public static final Region Bucket_Region;
	public static final String IP_Address;
	
	static
	{
		EnvReader reader = new EnvReader();
		Bucket_Name = reader.getProperty("bucket-name");
		Bucket_Namespace = reader.getProperty("bucket-namespace");
		Bucket_Region = Region.fromRegionId(reader.getProperty("bucket-region"));
		IP_Address = reader.getProperty("ip-address");
	}
	private final Properties properties = new Properties();
	
	private EnvReader()
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
	
	private String getProperty(String property)
	{
		return properties.getProperty(property);
	}
	
//	public String getIPAddress()
//	{
//		return properties.getProperty("ip-address");
//	}
//	
//	public String getBucketName()
//	{
//		return properties.getProperty("bucket-name");
//	}
//	
//	
//	public String getBucketNamespace()
//	{
//		return properties.getProperty("bucket-namespace");
//	}
//	
//	public String getBucketRegion()
//	{
//		return properties.getProperty("bucket-region");
//	}
	
	

}
