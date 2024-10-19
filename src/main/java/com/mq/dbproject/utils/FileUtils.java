package com.mq.dbproject.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class FileUtils {

    // Save a table (map of rows) to a file
    public static void saveTableToFile(Map<String, Map<String, String>> rows, String fileName) {
        File directory = new File("db_data");
        if (!directory.exists()) {
            directory.mkdir(); // Create db_data directory if it doesn't exist
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db_data/" + fileName))) {
            if (rows.isEmpty()) {
                writer.write("# This is an empty table: " + fileName.replace(".txt", ""));
                writer.newLine();
            } else {
                for (Map.Entry<String, Map<String, String>> entry : rows.entrySet()) {
                    String line = entry.getKey() + " : " + entry.getValue().toString().replaceAll(", ", ",").replaceAll("[{}]", "");
                    writer.write(line);
                    writer.newLine();
                    System.out.println("Writing to file: " + line); // Log what is being written
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    

    // Load a table from a file
    public static void loadTableFromFile(Map<String, Map<String, String>> rows, String fileName) {
        File directory = new File("db_data");
        if (!directory.exists()) {
            System.out.println("No existing database directory found, creating new one.");
            directory.mkdir(); // Create db_data directory if it doesn't exist
        }

        File file = new File("db_data/" + fileName);
        if (!file.exists()) {
            System.out.println("No existing table file found for " + fileName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keyValue = line.split(" : ");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String[] keyValues = keyValue[1].split(",");
                    Map<String, String> row = new HashMap<>();
                    for (String keyValuePair : keyValues) {
                        String[] entry = keyValuePair.split("=");
                        if (entry.length == 2) {
                            row.put(entry[0], entry[1]);
                        }
                    }
                    rows.put(key, row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
