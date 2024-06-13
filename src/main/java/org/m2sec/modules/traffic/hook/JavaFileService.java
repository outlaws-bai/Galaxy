package org.m2sec.modules.traffic.hook;

import org.m2sec.GalaxyMain;
import org.m2sec.common.Constants;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * @author: outlaws-bai
 * @date: 2024/6/13 22:16
 * @description:
 */
public class JavaFileService extends AbstractHttpHookService {

    private Class<?> clazz;

    @Override
    public void init() {
        loadJavaFile(
                GalaxyMain.config
                        .getHttpTrafficAutoModificationConfig()
                        .getHookConfig()
                        .getJavaFilePath());
    }

    public void init(String javaFilePath) {
        loadJavaFile(javaFilePath);
    }

    @Override
    public void destroy() {
        clazz = null;
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        try {
            Method method = clazz.getMethod("hookRequestToBurp", Request.class);
            return (Request) method.invoke(null, request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Request hookRequestToServer(Request request) {
        try {
            Method method = clazz.getMethod("hookRequestToServer", Request.class);
            return (Request) method.invoke(null, request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        try {
            Method method = clazz.getMethod("hookRequestToServer", Response.class);
            return (Response) method.invoke(null, response);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response hookResponseToClient(Response response) {
        try {
            Method method = clazz.getMethod("hookRequestToServer", Response.class);
            return (Response) method.invoke(null, response);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadJavaFile(String javaFilePath) {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            if (compiler == null) {
                throw new IllegalStateException(
                        "Cannot find the system Java compiler. "
                                + "Check that your class path includes tools.jar.");
            }

            // Set up the file manager
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            fileManager.setLocation(
                    StandardLocation.CLASS_OUTPUT,
                    Collections.singletonList(new File(Constants.TMP_FILE_DIR)));

            // Get the compilation unit from the Java file
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromStrings(Collections.singleton(javaFilePath));

            // StringJoiner to collect error messages
            StringJoiner errorMessages = new StringJoiner("\n");

            // Compile the Java file
            JavaCompiler.CompilationTask task =
                    compiler.getTask(
                            null,
                            fileManager,
                            diagnostic -> {
                                String errorMessage =
                                        "Error on line "
                                                + diagnostic.getLineNumber()
                                                + " in "
                                                + diagnostic.getSource()
                                                + ": "
                                                + diagnostic.getMessage(Locale.ENGLISH);
                                errorMessages.add(errorMessage);
                            },
                            null,
                            null,
                            compilationUnits);

            boolean success = task.call();
            fileManager.close();

            if (!success) {
                throw new RuntimeException("Compilation failed:\n" + errorMessages);
            }

            // Load the class
            File javaFile = new File(javaFilePath);
            String className = javaFile.getName().replace(".java", "");

            URL[] urls = new URL[] {new File(Constants.TMP_FILE_DIR).toURI().toURL()};
            try (URLClassLoader classLoader = new URLClassLoader(urls)) {
                clazz = classLoader.loadClass(className);
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
