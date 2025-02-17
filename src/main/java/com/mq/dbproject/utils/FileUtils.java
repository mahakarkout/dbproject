package com.mq.dbproject.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileUtils {
    public static String getShardNumber(String tableName) {
        char firstLetter = Character.toUpperCase(tableName.charAt(0)); // Get the first letter (case-insensitive)

        if (firstLetter >= 'A' && firstLetter <= 'E') {
            return "db_data/shardAtoE/"; // (shardAtoE)
        } else if (firstLetter >= 'F' && firstLetter <= 'M') {
            return "db_data/shardFtoM/"; // (shardFtoM)
        } else if (firstLetter >= 'N' && firstLetter <= 'Z') {
            return "db_data/shardNtoZ/"; // (shardNtoZ)
        } else {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    // Save a table (map of rows) to a file
    public static void saveTableToFile(Map<String, Map<String, String>> rows, String fileName) {
        File directory = new File("db_data");
        if (!directory.exists()) {
            directory.mkdir(); // Create db_data directory if it doesn't exist
        }
        String shardFolder = getShardNumber(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter( shardFolder+ fileName))) {
            if (rows.isEmpty()) {
                writer.write("# This is an empty table: " + fileName.replace(".txt", ""));
                writer.newLine();
            } else {
                for (Map.Entry<String, Map<String, String>> entry : rows.entrySet()) {
                    String line = entry.getKey() + " : " + entry.getValue().toString().replaceAll(", ", ",").replaceAll("[{}]", "");
                    writer.write(line);
                    writer.newLine();
                    logger.info("Writing to file: " + line); // Log what is being written
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
            logger.info("No existing database directory found, creating new one.");
            directory.mkdir(); // Create db_data directory if it doesn't exist
        }
        String shardFolder = getShardNumber(fileName);
        File file = new File(shardFolder + fileName);
        if (!file.exists()) {
            logger.warn("No existing table file found for " + fileName);
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
