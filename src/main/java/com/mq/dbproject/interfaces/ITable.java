package com.mq.dbproject.interfaces;

import java.util.List;
import java.util.Map;

public interface ITable {
    void insert(Map<String, String> row);
    Map<String, String> select(String key);
    void update(String key, Map<String, String> newRow);
    boolean delete(String key); //return type to boolean to indicate success or failure
    void displayTable();
    List<Map<String, String>> selectByName(String partialName);

}
