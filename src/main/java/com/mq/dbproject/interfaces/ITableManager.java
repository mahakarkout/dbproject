package com.mq.dbproject.interfaces;

import java.util.List;

public interface ITableManager {
    void createTable(String tableName, List<String> cols);
    void dropTable(String tableName);
    ITable getTable(String tableName);
    List<String> listTables();
}