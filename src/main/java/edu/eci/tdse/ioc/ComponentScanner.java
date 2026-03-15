package edu.eci.tdse.ioc;

import edu.eci.tdse.annotation.GetMapping;
import edu.eci.tdse.annotation.RequestParam;
import edu.eci.tdse.annotation.RestController;
import edu.eci.tdse.server.HttpRequest;
import edu.eci.tdse.server.HttpServer;
import java.net.URI;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ComponentScanner {

    /**
     * Auto-scan mode: discovers all @RestController classes on the classpath.
     */
    public static void scanAndRegister() throws Exception {
        List<Class<?>> controllers = findControllers();
        System.out.println("Found " + controllers.size() + " @RestController(s):");
        for (Class<?> cls : controllers) {
            System.out.println("  -> " + cls.getName());
            registerController(cls);
        }
    }


    public static void registerByName(String className) throws Exception {
        Class<?> cls = Class.forName(className);
        if (cls.isAnnotationPresent(RestController.class)) {
            System.out.println("Registering: " + cls.getName());
            registerController(cls);
        } else {
            System.err.println("Warning: " + className + " is not annotated with @RestController");
        }
    }


    private static void registerController(Class<?> cls) throws Exception {
        Object instance = cls.getDeclaredConstructor().newInstance();

        for (Method method : cls.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(GetMapping.class)) continue;

            String path = method.getAnnotation(GetMapping.class).value();
            method.setAccessible(true);


            HttpServer.get(path, (HttpRequest req) -> {
                try {
                    Object[] args = resolveArgs(method, req);
                    Object result = method.invoke(instance, args);
                    return result != null ? result.toString() : "";
                } catch (Exception e) {
                    return "<h1>500 Internal Server Error</h1><pre>" + e.getCause() + "</pre>";
                }
            });

            System.out.println("    GET " + path + " -> "
                    + cls.getSimpleName() + "#" + method.getName());
        }
    }

    /**
     * Resolves method arguments by reading @RequestParam annotations via reflection.
     */
    private static Object[] resolveArgs(Method method, HttpRequest req) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam rp = param.getAnnotation(RequestParam.class);
                String value = req.getParam(rp.value());
                args[i] = (value != null) ? value : rp.defaultValue();
            } else {
                args[i] = null;
            }
        }
        return args;
    }



    private static List<Class<?>> findControllers() throws Exception {
    List<Class<?>> result = new ArrayList<>();
    ClassLoader cl = Thread.currentThread().getContextClassLoader();

    java.security.CodeSource cs = ComponentScanner.class
            .getProtectionDomain().getCodeSource();

    if (cs != null) {
        String location = cs.getLocation().toURI().getPath();
        File locationFile = new File(location);

        if (locationFile.isFile() && location.endsWith(".jar")) {
            scanJar(location, result, cl);
        } else {
            scanDirectory(locationFile, "", result, cl);
        }
    }
    return result;
}

    private static void scanDirectory(File dir, String packagePrefix,
                                      List<Class<?>> result, ClassLoader cl) {
        if (!dir.isDirectory()) return;
        for (File entry : dir.listFiles()) {
            if (entry.isDirectory()) {
                String sub = packagePrefix.isEmpty()
                        ? entry.getName() : packagePrefix + "." + entry.getName();
                scanDirectory(entry, sub, result, cl);
            } else if (entry.getName().endsWith(".class")) {
                String className = (packagePrefix.isEmpty() ? "" : packagePrefix + ".")
                        + entry.getName().replace(".class", "");
                tryLoad(className, result, cl);
            }
        }
    }

    private static void scanJar(String jarPath, List<Class<?>> result, ClassLoader cl) {
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.endsWith(".class")) {
                    tryLoad(name.replace('/', '.').replace(".class", ""), result, cl);
                }
            }
        } catch (Exception ignored) {}
    }

    private static void tryLoad(String className, List<Class<?>> result, ClassLoader cl) {
        try {
            Class<?> cls = cl.loadClass(className);
            if (cls.isAnnotationPresent(RestController.class)) {
                result.add(cls);
            }
        } catch (Throwable ignored) {
        }
    }
}
