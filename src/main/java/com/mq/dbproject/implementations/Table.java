package com.mq.dbproject.implementations;

import com.mq.dbproject.interfaces.ITable;
import com.mq.dbproject.utils.FileUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Override
    public void insert(Map<String, String> row) {
        String primaryKey = row.get("id");
        if (primaryKey == null) {
            System.out.println("Primary key 'id' is required.");
            return;
        }

        if (!validateRow(row)) {
            System.out.println("Invalid row. The row must contain all columns specified: " + cols);
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
                System.out.println("Invalid row. The row must contain all columns specified : " + cols);
                return;
            }
            rows.put(key, newRow);
            saveToFile();
        } else {
            System.out.println("Row not found for key: " + key);
        }
    }

    @Override
    public boolean delete(String key) {
        if (rows.containsKey(key)) {
            rows.remove(key);
            saveToFile();
            System.out.println("Row with key '" + key + "' has been successfully deleted.");
            return true; // Return true if the deletion was successful
        } else {
            System.out.println("Error: Row with key '" + key + "' does not exist.");
            return false; // Return false if the key was not found
        }
    }

    @Override
    public void displayTable() {
        System.out.println("Table: " + tableName);
        System.out.println("Columns: " + cols);
        for (Map.Entry<String, Map<String, String>> entry : rows.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Row: " + entry.getValue());
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
        System.out.println("Attempting to save table '" + tableName + "' to disk...");
        FileUtils.saveTableToFile(rows, tableName + ".txt");
        System.out.println("Save operation completed for table '" + tableName + "'.");
    }


    private void loadFromFile() {
        FileUtils.loadTableFromFile(rows, tableName + ".txt");
    }
}
