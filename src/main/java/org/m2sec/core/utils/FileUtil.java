package org.m2sec.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:20
 * @description:
 */
@Slf4j
public class FileUtil {

    public static String readResourceAsString(String path) {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            assert inputStream != null;
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void cpResourceFileToTarget(String resourceFilePath, String targetDir) {
        Path targetDirPath = Paths.get(targetDir);
        Path targetPath = targetDirPath.resolve(new File(resourceFilePath).getName());
        writeFile(targetPath.toAbsolutePath().toString(), readResourceAsString(resourceFilePath));
    }


    public static List<String> listDir(String dir) {
        List<String> files = new ArrayList<>();
        Path startDir = Paths.get(dir); // 替换为你的目录路径
        try {
            Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    files.add(file.toAbsolutePath().toString());
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }


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


    public static void deleteFileIfExist(String... filePaths) {
        deleteFileIfExist(Stream.of(filePaths).map(File::new).toArray(File[]::new));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteFileIfExist(File... files) {
        if (files == null) return;
        for (File file : files) {
            file.delete();
        }
    }

    public static void overwriteFile(String targetFilePath, String content) {
        try {
            Files.write(Paths.get(targetFilePath), content.getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFile(String targetFilePath, String content) {
        try {
            Files.write(Paths.get(targetFilePath), content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
