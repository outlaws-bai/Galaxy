package org.m2sec.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class FileUtil {
    public static String readFileAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readFileAsStringArray(String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDirs(String... dirs) {
        for (String filePath : dirs) {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void createFiles(String... filePaths) {
        for (String filePath : filePaths) {
            Path path = Paths.get(filePath);
            try {
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeToFileIfEmpty(String filePath, String content) {
        writeToFileIfEmpty(filePath, content.getBytes());
    }

    public static void writeToFileIfEmpty(String filePath, byte[] content) {
        Path path = Paths.get(filePath);
        try {
            if (Files.size(path) == 0) {
                Files.write(path, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFileIfExist(String... filePaths) {
        deleteFileIfExist(Stream.of(filePaths).map(File::new).toArray(File[]::new));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteFileIfExist(File... files) {
        if (files==null) return;
        for (File file : files) {
            file.delete();
        }
    }
}
