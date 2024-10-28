package com.mq.dbproject;

import com.mq.dbproject.controllers.TableController;
import com.mq.dbproject.interfaces.ITable;
import com.mq.dbproject.interfaces.ITableManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(TableController.class)
public class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITableManager tableManager;

    @Test
    public void testInsertRow() throws Exception {
        ITable mockTable = Mockito.mock(ITable.class);
        Mockito.when(tableManager.getTable("testTable")).thenReturn(mockTable);
        Mockito.doNothing().when(mockTable).insert(any(Map.class));

        mockMvc.perform(post("/api/Table/testTable/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"column1\": \"value1\", \"column2\": \"value2\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Row inserted successfully."));
    }

    @Test
    public void testUpdateRow() throws Exception {
        ITable mockTable = Mockito.mock(ITable.class);
        Mockito.when(tableManager.getTable("testTable")).thenReturn(mockTable);
        Mockito.doNothing().when(mockTable).update(eq("key1"), any(Map.class));

        mockMvc.perform(put("/api/Table/testTable/update/key1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"column1\": \"newValue1\", \"column2\": \"newValue2\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Row updated successfully."));
    }

    @Test
    public void testDeleteRow() throws Exception {
        ITable mockTable = Mockito.mock(ITable.class);
        Mockito.when(tableManager.getTable("testTable")).thenReturn(mockTable);
        Mockito.when(mockTable.delete("key1")).thenReturn(true);

        mockMvc.perform(delete("/api/Table/testTable/delete/key1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Row deleted successfully."));
    }

    @Test
    public void testDeleteRowFailure() throws Exception {
        ITable mockTable = Mockito.mock(ITable.class);
        Mockito.when(tableManager.getTable("testTable")).thenReturn(mockTable);
        Mockito.when(mockTable.delete("key1")).thenReturn(false);

        mockMvc.perform(delete("/api/Table/testTable/delete/key1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Row could not be deleted."));
    }

    @Test
    public void testDisplayTable() throws Exception {
        ITable mockTable = Mockito.mock(ITable.class);
        Mockito.when(tableManager.getTable("testTable")).thenReturn(mockTable);
        Mockito.doNothing().when(mockTable).displayTable();

        mockMvc.perform(get("/api/Table/testTable/display"))
                .andExpect(status().isOk())
                .andExpect(content().string("Table displayed in console."));
    }

    @Test
    public void testTableNotFound() throws Exception {
        Mockito.when(tableManager.getTable("nonExistentTable")).thenReturn(null);

        mockMvc.perform(post("/api/Table/nonExistentTable/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"column1\": \"value1\"}"))
                .andExpect(status().isNotFound());
    }
}
