package com.mq.dbproject.controllers;

import com.mq.dbproject.interfaces.ITable;
import com.mq.dbproject.interfaces.ITableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Table/{tableName}")
public class TableController {

    private final ITableManager tableManager;

    @Autowired
    public TableController(ITableManager tableManager) {
        this.tableManager = tableManager;
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertRow(@PathVariable String tableName, @RequestBody Map<String, String> row) {
        ITable table = tableManager.getTable(tableName);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        table.insert(row);
        return ResponseEntity.ok("Row inserted successfully.");
    }

    @PutMapping("/update/{key}")
    public ResponseEntity<String> updateRow(@PathVariable String tableName, @PathVariable String key, @RequestBody Map<String, String> newRow) {
        ITable table = tableManager.getTable(tableName);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        table.update(key, newRow);
        return ResponseEntity.ok("Row updated successfully.");
    }

    @DeleteMapping("/delete/{key}")
    public ResponseEntity<String> deleteRow(@PathVariable String tableName, @PathVariable String key) {
        ITable table = tableManager.getTable(tableName);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        boolean deleted = table.delete(key);
        if (deleted) {
            return ResponseEntity.ok("Row deleted successfully.");
        } else {
            return ResponseEntity.status(400).body("Row could not be deleted.");
        }
    }

    @GetMapping("/display")
    public ResponseEntity<String> displayTable(@PathVariable String tableName) {
        ITable table = tableManager.getTable(tableName);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        table.displayTable();
        return ResponseEntity.ok("Table displayed in console.");
    }

    @GetMapping("/select/{key}")
    public ResponseEntity<Map<String, String>> selectRow(@PathVariable String tableName, @PathVariable String key) {
        ITable table = tableManager.getTable(tableName);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, String> row = table.select(key);
        if (row == null) {
            return ResponseEntity.status(404).body(null); // Row not found
        }
        return ResponseEntity.ok(row);
    }

    @GetMapping("/selectByName")
    public ResponseEntity<List<Map<String, String>>> selectByName(@PathVariable String tableName, @RequestParam String name) {
        List<Map<String, String>> rows = tableManager.selectByName(tableName, name);
        if (rows.isEmpty()) {
            return ResponseEntity.status(404).body(null); // No rows found
        }
        return ResponseEntity.ok(rows);
    }
}

