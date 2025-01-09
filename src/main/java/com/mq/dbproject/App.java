package com.mq.dbproject;

import com.mq.dbproject.replication.ReplicationProcess;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


@SpringBootApplication
public class App {
    public static void main(String[] args) throws IOException
    {
        SpringApplication.run(App.class, args);
        String sourceDir = "db_data";
        String targetDir = "F:\\dbprojectSecondInstance\\db_data";

        ReplicationProcess replicationProcess = new ReplicationProcess();

        // Start watching the directory and trigger replication on changes
        replicationProcess.watchAndReplicate(sourceDir, targetDir);

    }
}