package com.mq.dbproject.controllers;

import com.mq.dbproject.interfaces.ITableManager;
import com.mq.dbproject.interfaces.ITable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/TableManager")
public class TableManagerController {

    private final ITableManager tableManager;

    @Autowired
    public TableManagerController(ITableManager tableManager) {
        this.tableManager = tableManager;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTable(@RequestParam String tableName, @RequestBody List<String> columns) {
        tableManager.createTable(tableName, columns);
        return ResponseEntity.ok("Table created successfully.");
    }

    @DeleteMapping("/drop")
    public ResponseEntity<String> dropTable(@RequestParam String tableName) {
        tableManager.dropTable(tableName);
        return ResponseEntity.ok("Table dropped successfully.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listTables() {
        List<String> tables = tableManager.listTables(); 
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/selectByName")
    public ResponseEntity<List<Map<String, String>>> selectByName(@RequestParam String tableName, @RequestParam String name) {
       // logger.info("Received selectByName request for table: " + tableName + " with partial name: " + name);
    
        List<Map<String, String>> rows = tableManager.selectByName(tableName, name);
        
        if (rows == null || rows.isEmpty()) {
            //logger.warn("No rows found for table: " + tableName + " with partial name: " + name);
            return ResponseEntity.status(404).body(null); // No rows found
        }
        return ResponseEntity.ok(rows);
    }
    

}
