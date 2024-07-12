package org.m2sec.core.httphook;

import lombok.extern.slf4j.Slf4j;
import org.m2sec.Galaxy;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Option;
import org.m2sec.core.common.Constants;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.slf4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class JavaFileHooker extends IHttpHooker {

    private Class<?> clazz;


    @Override
    public void init(Option opt) {
        option = opt;
        String javaFilePath = FileTools.getExampleScriptFilePath(option.getJavaSelectItem(),
            Constants.JAVA_FILE_SUFFIX);
        init(javaFilePath);
        log.info("load java file success. {}", javaFilePath);
    }

    public void init(String javaFilePath) {
        if (javaFilePath.endsWith(Constants.JAVA_FILE_SUFFIX)) loadJavaFile(javaFilePath);
        else if (javaFilePath.endsWith(Constants.JAVA_COMPILED_FILE_SUFFIX)) loadJavaClass(javaFilePath);
        else throw new IllegalArgumentException("javaFilePath suffix error!");
    }

    @Override
    public void destroy() {
        clazz = null;
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        try {
            Method method = clazz.getMethod(Constants.HOOK_FUNC_1, Request.class);
            return (Request) method.invoke(null, request);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Request hookRequestToServer(Request request) {
        try {
            Method method = clazz.getMethod(Constants.HOOK_FUNC_2, Request.class);
            return (Request) method.invoke(null, request);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        try {
            Method method = clazz.getMethod(Constants.HOOK_FUNC_3, Response.class);
            return (Response) method.invoke(null, response);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response hookResponseToClient(Response response) {
        try {
            Method method = clazz.getMethod(Constants.HOOK_FUNC_4, Response.class);
            return (Response) method.invoke(null, response);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadJavaFile(String javaFilePath) {
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
            loadJavaClass(classFilePath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadJavaClass(String javaClassFilePath) {
        try {
            File javaFile = new File(javaClassFilePath);
            String className = javaFile.getName().replace(Constants.JAVA_COMPILED_FILE_SUFFIX, "");

            URL[] urls = new URL[]{new File(javaFile.getParent()).toURI().toURL()};
            try (URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader())) {
                clazz = classLoader.loadClass(className);
                // set log
                Field field = clazz.getDeclaredField("log");
                field.setAccessible(true); // to access private field
                field.set(null, log);
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
