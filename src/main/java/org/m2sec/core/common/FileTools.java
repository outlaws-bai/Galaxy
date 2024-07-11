package org.m2sec.core.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:20
 * @description:
 */
@Slf4j
public class FileTools {

    public static String readResourceAsString(String path) {
        ClassLoader classLoader = FileTools.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            assert inputStream != null;
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void cpResourceToTargetIfExist(String resourceFilePath, String targetDir) {
        Path targetDirPath = Paths.get(targetDir);
        Path targetPath = targetDirPath.resolve(new File(resourceFilePath).getName());
        writeFile(targetPath.toAbsolutePath().toString(), readResourceAsString(resourceFilePath));
    }


    public static List<String> listDir(String dir) {
        List<String> files = new ArrayList<>();
        Path startDir = Paths.get(dir); // 替换为你的目录路径
        try {
            Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
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
        createFiles(Stream.of(filePaths).map(Paths::get).toArray(Path[]::new));
    }

    public static void createFiles(Path... filePaths) {
        for (Path path : filePaths) {
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


    public static void writeFile(String targetFilePath, String content) {
        try {
            Path path = Paths.get(targetFilePath);
            if (!Files.exists(path)) createFiles(path);
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFileIfEmptyOfResource(String resourceName, String filepath) {
        Path path = Paths.get(filepath);
        if (!Files.exists(path) || readFileAsString(filepath).isBlank()) {
            writeFile(filepath, readResourceAsString(resourceName));
        }
    }

    public static void writeFileIfEmpty(String targetFilePath, String content) {
        String raw = readFileAsString(targetFilePath);
        if (raw.isBlank()) {
            writeFile(targetFilePath, content);
        }
    }

    public static void copyDirResourcesToTargetDirIfEmpty(String sourceDir, String targetDir) {
        try {
            // 获取目标目录路径
            Path targetPath = Paths.get(targetDir);
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            // 获取资源目录 URL
            URL resource = FileTools.class.getClassLoader().getResource(sourceDir);
            if (resource == null) {
                throw new IllegalArgumentException("Resource directory not found: " + sourceDir);
            }

            // 处理资源目录是文件系统目录的情况
            if (resource.getProtocol().equals("file")) {
                Path sourcePath = Paths.get(resource.toURI());
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
                    for (Path path : directoryStream) {
                        try (InputStream in = Files.newInputStream(path)) {
                            Files.copy(in, targetPath.resolve(path.getFileName().toString()),
                                StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
            // 处理资源目录在 JAR 文件中的情况
            else if (resource.getProtocol().equals("jar")) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                try (JarFile jarFile = new JarFile(Paths.get(new URL("file:" + jarPath).toURI()).toFile())) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().startsWith(sourceDir + "/") && !entry.isDirectory()) {
                            try (InputStream in = jarFile.getInputStream(entry)) {
                                String fileName = entry.getName().substring(sourceDir.length() + 1);
                                Files.copy(in, targetPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVersion() {
        return readResourceAsString("version.txt");
    }


}
