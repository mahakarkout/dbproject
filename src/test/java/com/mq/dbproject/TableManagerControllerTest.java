package com.mq.dbproject;

import com.mq.dbproject.controllers.TableManagerController;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(TableManagerController.class)
public class TableManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITableManager tableManager;

    @Test
    public void testCreateTable() throws Exception {
        Mockito.doNothing().when(tableManager).createTable(eq("testTable"), any(List.class));

        mockMvc.perform(post("/api/TableManager/create")
                        .param("tableName", "tempTable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"firstcol\", \"secondcol\"]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Table created successfully."));
    }

    @Test
    public void testDropTable() throws Exception {
        Mockito.doNothing().when(tableManager).dropTable("tempTable");

        mockMvc.perform(delete("/api/TableManager/drop")
                        .param("tableName", "tempTable"))
                .andExpect(status().isOk())
                .andExpect(content().string("Table dropped successfully."));
    }

    @Test
    public void testListTables() throws Exception {
        List<String> mockTables = Arrays.asList("users", "managers");
        Mockito.when(tableManager.listTables()).thenReturn(mockTables);

        mockMvc.perform(get("/api/TableManager/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("users")))
                .andExpect(jsonPath("$[1]", is("managers")));
    }

    @Test
    public void testGetTableNotFound() throws Exception {
        Mockito.when(tableManager.getTable("nonExistentTable")).thenReturn(null);

        mockMvc.perform(get("/api/TableManager/nonExistentTable"))
                .andExpect(status().isNotFound());
    }
}

