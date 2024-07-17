package org.m2sec.core.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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

    public static void renameDir(String sourceDir, String targetDir) {
        File oldDir = new File(sourceDir);
        File newDir = new File(targetDir);

        // 检查旧文件夹是否存在并且是一个目录
        if (!oldDir.exists() || !oldDir.isDirectory()) {
            throw new RuntimeException("Old directory does not exist or is not a directory.");
        }

        // 如果新文件夹路径已存在，则重命名失败
        if (newDir.exists()) {
            throw new RuntimeException("New directory already exists.");
        }

        // 重命名文件夹
        //noinspection ResultOfMethodCallIgnored
        oldDir.renameTo(newDir);
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


    public static void deleteFiles(String... filePaths) {
        deleteFiles(Stream.of(filePaths).map(File::new).toArray(File[]::new));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteFiles(File... files) {
        if (files == null) return;
        for (File file : files) {
            if (file.exists()) file.delete();
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


    public static void mvResource(String resourceName, String targetDir) {
        Path path = Paths.get(targetDir);
        if (Files.exists(path) && Files.isDirectory(path)) {
            writeFile(targetDir + File.separator + resourceName, readResourceAsString(resourceName));
        }
    }

    public static void mvResources(String resourceDir, String targetDir) {
        try {
            String dir = targetDir + File.separator + resourceDir;
            Path targetPath = Paths.get(dir);
            if (!Files.exists(targetPath)) {
                createDirs(dir);
            }

            URL resource = FileTools.class.getClassLoader().getResource(resourceDir);
            if (resource == null) {
                throw new IllegalArgumentException("Resource directory not found: " + resourceDir);
            }

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
            } else if (resource.getProtocol().equals("jar")) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                try (JarFile jarFile = new JarFile(Paths.get(new URL("file:" + jarPath).toURI()).toFile())) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().startsWith(resourceDir + "/") && !entry.isDirectory()) {
                            try (InputStream in = jarFile.getInputStream(entry)) {
                                String fileName = entry.getName().substring(resourceDir.length() + 1);
                                Files.copy(in, targetPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException();
            }

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getExampleScriptFilePath(String item, String suffix) {
        return Constants.HTTP_HOOK_EXAMPLES_DIR + File.separator + item + suffix;
    }

    public static boolean isExist(String filepath) {
        return Files.exists(Paths.get(filepath));
    }

}
