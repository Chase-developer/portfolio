package com.chase.portfolio.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.chase.portfolio.PortfolioUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class StatisticsService {
	
	public static enum Type
	{
		Views
	}
	
	private final ConcurrentHashMap<Type, Integer> values = new ConcurrentHashMap<Type, Integer>();
	
	@PreDestroy
    public void save() {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("statistics"))) {
            // Loop through the enum map and write each key-value pair to the file
            for (Type key : values.keySet()) {
                String value = String.valueOf(values.get(key));
                writer.write(key.name() + "=" + value);
                writer.newLine(); // Add a new line after each key-value pair
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@PostConstruct
	public void read()
	{
		if (PortfolioUtils.isTestProfile()) {
            return;  // Skip initialization in test environment
        }
		File file = new File("statistics");

		// Ensure file exists before reading
		if (!file.exists()) {
		    try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // creates the file if it doesn't exists
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Split the line by '=' to get the key-value pair
                String[] parts = line.split("=", 2);
                
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    Type enumKey = Type.valueOf(key);
                	values.put(enumKey, Integer.valueOf(value));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		for (Type type : Type.values())
		{
			values.putIfAbsent(type, 0);
		}
	}
	
	public void incrementViews()
	{
		values.put(Type.Views, values.get(Type.Views) + 1);
	}
	
	public int getViews()
	{
		return values.get(Type.Views);
	}

}
