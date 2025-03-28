package com.chase.portfolio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.bmc.Region;

public class EnvLoader {
	
	public static void init()
	{
		new EnvLoader();
	}
	
	public static EnvLoader get()
	{
		return instance;
	}
	
	private static EnvLoader instance;
	private static final Logger logger = LoggerFactory.getLogger(EnvLoader.class);
	
	public final String bucket_Name;
	public final String bucket_Namespace;
	public final Region bucket_Region;
	public final String web_Address;
	/*
	 * 
	 */
	private final Properties properties = new Properties();
	
	private EnvLoader()
	{
		try (FileInputStream fileInputStream = new FileInputStream(".env")) {
	        properties.load(fileInputStream);
	        if (!PortfolioApplication.isInProject())
	        {
	        	/*
	        	 * server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-alias=${SSL_KEY_ALIAS}
	        	 */
	        	logger.info("Setting SSL Properties");
	        	System.setProperty("server.ssl.key-store", properties.getProperty("SSL_KEYSTORE_PATH"));
	        	System.setProperty("server.ssl.key-store-password", properties.getProperty("SSL_KEYSTORE_PASSWORD"));
	        	System.setProperty("server.ssl.key-alias", properties.getProperty("SSL_KEY_ALIAS"));
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bucket_Name = properties.getProperty("bucket-name");
		bucket_Namespace = properties.getProperty("bucket-namespace");
		bucket_Region = Region.fromRegionId(properties.getProperty("bucket-region"));
		web_Address = properties.getProperty("web-address");
		instance = this;
	}

}
