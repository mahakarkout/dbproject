package com.mq.dbproject.implementations;

import com.mq.dbproject.interfaces.ITable;
import com.mq.dbproject.interfaces.ITableManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mq.dbproject.utils.FileUtils.getShardNumber;

@Service
public class TableManager implements ITableManager {
    private Map<String, ITable> tables;

    public TableManager() {
        tables = new HashMap<>();
        loadExistingTables(); // Load existing tables from disk
    }
    private static final Logger logger = LoggerFactory.getLogger(TableManager.class);
    @Override
    public void createTable(String tableName, List<String> cols) {
        if (tables.containsKey(tableName)) {
            logger.warn("Table " + tableName + " already exists.");
            return;
        }
        ITable newTable = new Table(tableName, cols); //we create using Table implementation but store as ITable
        tables.put(tableName, newTable);
        
        // Save the empty table to file immediately
        newTable.displayTable(); 
        ((Table)newTable).saveToFile(); 
        
        logger.info("Table " + tableName + " created with columns: " + cols);
    }

    @Override
    public void dropTable(String tableName) {
        if (tables.containsKey(tableName)) {
            tables.remove(tableName);
            String shardFolder = getShardNumber(tableName);
            File tableFile = new File(shardFolder + tableName + ".txt");
            if (tableFile.exists()) {
                tableFile.delete(); // Delete the file when dropping the table
            }
            logger.info("Table " + tableName + " dropped.");
        } else {
            logger.info("Table " + tableName + " does not exist.");
        }
    }

    @Override
    public ITable getTable(String tableName) {
        return tables.get(tableName);
    }

    @Override
    public List<String> listTables() {
        List<String> tableNames = new ArrayList<>(tables.keySet());
        return tableNames;
    }

    @Override
    public List<Map<String, String>> selectByName(String tableName, String nameString) {
        ITable table = tables.get(tableName);
        if (table != null) {
            return table.selectByName(nameString);
        }
        return new ArrayList<>();
    }
    



    // Load existing tables from the disk

    private void loadExistingTables() {
        File dbDirectory = new File("db_data");
        if (dbDirectory.exists() && dbDirectory.isDirectory()) {
            // Recursively list all `.txt` files in shard folders
            List<File> tableFiles = getAllTableFiles(dbDirectory);
            if (!tableFiles.isEmpty()) {
                for (File tableFile : tableFiles) {
                    String tableName = tableFile.getName().replace(".txt", "");
                    ITable table = new Table(tableName, List.of("id", "name", "email")); // Example columns
                    tables.put(tableName, table);
                    logger.info("Loaded table: " + tableName);
                }
            } else {
                logger.warn("No existing tables found.");
            }
        } else {
            logger.warn("Database directory does not exist or is not a directory.");
        }
    }

    // Helper method to get all `.txt` files from subdirectories
    private List<File> getAllTableFiles(File directory) {
        List<File> tableFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call for subdirectories
                    tableFiles.addAll(getAllTableFiles(file));
                } else if (file.isFile() && file.getName().endsWith(".txt")) {
                    // Add .txt files to the list
                    tableFiles.add(file);
                }
            }
        }
        return tableFiles;
    }
}
