package org.m2sec.core.common;

import org.m2sec.Galaxy;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:57
 * @description:
 */
public class ReflectTools {

    public static Class<?> loadJavaFile(String javaFilePath) {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            File javaFile = new File(javaFilePath);

            if (compiler == null) {
                throw new IllegalStateException("Cannot find the system Java compiler. " + "Check that your class " +
                    "path includes tools.jar.");
            }

            // Set up the file manager
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            // 设置类路径，包含所有依赖的 JAR 文件
            List<String> optionList = new ArrayList<>();
            if (Galaxy.isInBurp()) {
                optionList.add("-classpath");
                optionList.add(Constants.JAR_FILE_PATH);
            }

            // 设置输出目录
            optionList.add("-d");
            optionList.add(Constants.TMP_FILE_DIR);

            // Get the compilation unit from the Java file
            Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromStrings(Collections.singleton(javaFilePath));

            // StringJoiner to collect error messages
            StringJoiner errorMessages = new StringJoiner("\n");

            // Compile the Java file
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostic -> {
                String errorMessage =
                    "Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource() + ": " + diagnostic.getMessage(Locale.ENGLISH);
                errorMessages.add(errorMessage);
            }, optionList, null, compilationUnits);

            boolean success = task.call();
            fileManager.close();

            if (!success) {
                throw new RuntimeException("Compilation failed:\n" + errorMessages);
            }

            String classFilePath =
                Constants.TMP_FILE_DIR + File.separator + javaFile.getName().replace(Constants.JAVA_FILE_SUFFIX,
                    Constants.JAVA_COMPILED_FILE_SUFFIX);
            return loadJavaClass(classFilePath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> loadJavaClass(String javaClassFilePath) {
        try {
            File javaFile = new File(javaClassFilePath);
            String className = javaFile.getName().replace(Constants.JAVA_COMPILED_FILE_SUFFIX, "");

            URL[] urls = new URL[]{new File(javaFile.getParent()).toURI().toURL()};
            try (URLClassLoader classLoader = new URLClassLoader(urls, ReflectTools.class.getClassLoader())) {
                return classLoader.loadClass(className);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object newInstance(Class<?> clazz, Class<?> parameterType, Object object) {
        return newInstance(clazz, new Class[]{parameterType}, new Object[]{object});
    }

    public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] objects) {
        try {
            Constructor<?> constructor = clazz.getConstructor(parameterTypes);
            return constructor.newInstance(objects);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setStaticAttr(Class<?> clazz, String attrName, Object attr) {
        try {
            Field field = clazz.getDeclaredField(attrName);
            field.setAccessible(true); // to access private field
            field.set(null, attr);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object callStaticFunc(Class<?> clazz, String funcName, Class<?>[] parameterTypes, Object[] objects) {
        try {
            Method method = clazz.getMethod(funcName, parameterTypes);
            return method.invoke(null, objects);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object callFunc(Class<?> clazz, Object instance, String funcName, Class<?>[] parameterTypes,
                                  Object[] objects) {
        try {
            Method method = clazz.getMethod(funcName, parameterTypes);
            return method.invoke(instance, objects);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


}
