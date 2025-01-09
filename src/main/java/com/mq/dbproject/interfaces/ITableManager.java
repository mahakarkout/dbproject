package com.mq.dbproject.interfaces;

import java.util.List;
import java.util.Map;

public interface ITableManager {
    void createTable(String tableName, List<String> cols);
    void dropTable(String tableName);
    ITable getTable(String tableName);
    List<String> listTables();
    List<Map<String, String>> selectByName(String tableName, String partialName); 

}