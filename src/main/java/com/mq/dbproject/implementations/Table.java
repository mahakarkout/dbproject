package com.mq.dbproject.implementations;

import com.mq.dbproject.interfaces.ITable;
import com.mq.dbproject.utils.FileUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;




public class Table implements ITable {
    private String tableName;
    private Map<String, Map<String, String>> rows;
    private List<String> cols;

    public Table(String tableName, List<String> cols) {
        this.tableName = tableName;
        this.cols = cols;
        this.rows = new HashMap<>();
        loadFromFile();
    }
    private static final Logger logger = LoggerFactory.getLogger(Table.class);
    @Override
    public void insert(Map<String, String> row) {
        String primaryKey = row.get("id");
        if (primaryKey == null) {
            logger.warn("Primary key 'id' is required.");
            return;
        }

        if (!validateRow(row)) {
            logger.warn("Invalid row. The row must contain all columns specified: " + cols);
            return;
        }

        rows.put(primaryKey, row);
        saveToFile();
    }

    @Override
    public Map<String, String> select(String key) {
        return rows.get(key);
    }

    @Override
    public void update(String key, Map<String, String> newRow) {
        if (rows.containsKey(key)) {
            if (!validateRow(newRow)) {
                logger.warn("Invalid row. The row must contain all columns specified : " + cols);
                return;
            }
            rows.put(key, newRow);
            saveToFile();
        } else {
            logger.warn("Row not found for key: " + key);
        }
    }

    @Override
    public boolean delete(String key) {
        if (rows.containsKey(key)) {
            rows.remove(key);
            saveToFile();
            logger.info("Row with key '" + key + "' has been successfully deleted.");
            return true; // Return true if the deletion was successful
        } else {
            logger.error("Error: Row with key '" + key + "' does not exist.");
            return false; // Return false if the key was not found
        }
    }

    @Override
    public void displayTable() {
        logger.info("Table: " + tableName);
        logger.info("Columns: " + cols);
        for (Map.Entry<String, Map<String, String>> entry : rows.entrySet()) {
            logger.info("Key: " + entry.getKey() + ", Row: " + entry.getValue());
        }
    }

    // Helper methods for validation and file handling
    private boolean validateRow(Map<String, String> row) {
        if (row.size() != cols.size()) {
            return false;
        }
        for (String column : cols) {
            if (!row.containsKey(column)) {
                return false;
            }
        }
        return true;
    }

    public void saveToFile() {
        logger.info("Attempting to save table '" + tableName + "' to disk...");
        FileUtils.saveTableToFile(rows, tableName + ".txt");
        logger.info("Save operation completed for table '" + tableName + "'.");
    }


    private void loadFromFile() {
        FileUtils.loadTableFromFile(rows, tableName + ".txt");
    }
}
