package com.mq.dbproject.replication;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;

public class ReplicationProcess {

    private final WatchService watchService;
    private final Map<WatchKey, Path> watchKeys;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;

    public ReplicationProcess() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchKeys = new HashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static void replicateFiles(String sourceDir, String targetDir) throws IOException {
        Files.walk(Paths.get(sourceDir)).forEach(sourcePath -> {
            try {
                Path targetPath = Paths.get(targetDir, sourcePath.toString().substring(sourceDir.length()));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void registerAllDirectories(Path startDir) throws IOException {
        Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        watchKeys.put(key, dir);
        System.out.println("Registered directory: " + dir);
    }

    public void watchAndReplicate(String sourceDir, String targetDir) throws IOException {
        Path sourcePath = Paths.get(sourceDir);
        registerAllDirectories(sourcePath); // Register all directories under sourceDir

        System.out.println("Watching directory and subdirectories: " + sourceDir);

        while (true) {
            WatchKey key;
            try {
                key = watchService.take(); // Block until an event occurs
            } catch (InterruptedException e) {
                return;
            }

            Path dir = watchKeys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path name = (Path) event.context();
                Path child = dir.resolve(name);

                System.out.println(kind.name() + ": " + child);

                // This is for new directory and its subdirectories
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isDirectory(child)) {
                        registerAllDirectories(child);
                    }
                }

                // Trigger replication with a delay
                scheduleReplication(sourceDir, targetDir);
            }

            boolean valid = key.reset();
            if (!valid) {
                watchKeys.remove(key);
                if (watchKeys.isEmpty()) {
                    break; // All directories are inaccessible
                }
            }
        }
    }

    private synchronized void scheduleReplication(String sourceDir, String targetDir) {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(false); // Cancel the previous task if still pending
        }
        scheduledTask = scheduler.schedule(() -> {
            try {
                replicateFiles(sourceDir, targetDir);
                System.out.println("Replication completed after delay.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 60, TimeUnit.SECONDS); // we can set in hours
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
