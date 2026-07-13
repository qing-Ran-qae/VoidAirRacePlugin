package io.github.hhn756.voidairrace.infrastructure.util;

import io.github.hhn756.voidairrace.VoidAirRace;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * 插件类扫描器，用于扫描指定插件内指定类型的所有子类
 */
public class ClassScanner {
    private ClassScanner() {}


    /**
     * 扫描插件中所有指定类型的子类（包括实现类），使用插件主类的包作为根包
     * @param type   要查找的父类型（类或接口）
     * @param <T>    泛型类型
     * @return 符合条件的子类列表（不包含 type 本身）
     */
    public static <T> Collection<Class<T>> scanSubclasses(Class<T> type) {
        VoidAirRace mainClass = VoidAirRace.getInstance();

        // 默认使用插件主类的包
        String defaultPackage = mainClass.getClass().getPackage().getName();
        return scanSubclasses(type, defaultPackage);
    }

    /**
     * 扫描插件中指定包及其子包下所有指定类型的子类（包括实现类）
     * @param type        要查找的父类型（类或接口）
     * @param basePackage 要扫描的基础包名（例如 "com.abc.project"）
     *                    若为 null 或空字符串，则回退到扫描插件主类的包
     * @param <T>         泛型类型
     * @return 符合条件的子类列表（不包含 type 本身）
     */
    public static <T> Collection<Class<T>> scanSubclasses(Class<T> type, String basePackage) {
        VoidAirRace mainClass = VoidAirRace.getInstance();

        // 如果未指定包名，则使用插件主类的包
        if (basePackage == null || basePackage.trim().isEmpty()) {
            basePackage = mainClass.getClass().getPackage().getName();
        }

        List<String> classNames = new ArrayList<>();
        try {
            // 获取插件代码源（jar 文件或编译输出目录）
            URL location = mainClass.getClass().getProtectionDomain().getCodeSource().getLocation();
            File source = new File(location.toURI());

            // 将包名转换为路径格式
            String basePath = basePackage.replace('.', File.separatorChar);
            String jarBasePath = basePackage.replace('.', '/'); // jar 条目中统一使用 '/'

            // 根据源类型收集所有类名
            if (source.isDirectory()) {
                // 开发环境：目录结构
                File baseDir = new File(source, basePath);
                if (baseDir.exists() && baseDir.isDirectory()) {
                    collectClassNamesInDirectory(baseDir, basePackage, classNames);
                } else {
                    mainClass.getLogger().warning("指定的包路径不存在于源代码目录中: " + baseDir.getPath());
                }
            } else {
                // 生产环境：jar 文件
                try (JarFile jar = new JarFile(source)) {
                    collectClassNamesInJar(jar, jarBasePath, classNames);
                }
            }

            // 加载类并过滤出指定类型的子类
            List<Class<T>> result = new ArrayList<>();
            ClassLoader classLoader = mainClass.getClass().getClassLoader();
            for (String className : classNames) {
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    // type.isAssignableFrom(type) 判断 type 是否为 type 的子类型
                    // 排除 type 本身（如果 type 是具体类）以及接口、抽象类可根据需要额外过滤
                    if (type.isAssignableFrom(clazz) && !clazz.equals(type)) {
                        result.add((Class<T>) clazz);
                    }
                } catch (ClassNotFoundException e) {
                    mainClass.getLogger().warning("无法加载类: " + className);
                }
            }
            return result;

        } catch (Exception e) {
            mainClass.getLogger().log(Level.SEVERE, "扫描插件类失败", e);
            return Collections.emptyList();
        }
    }

    /** 递归扫描目录，收集所有 .class 文件的完整类名 */
    private static void collectClassNamesInDirectory(File dir, String packageName, List<String> classNames) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                collectClassNamesInDirectory(file, packageName + "." + file.getName(), classNames);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }

    /** 扫描 jar 文件，收集所有 .class 文件的完整类名 */
    private static void collectClassNamesInJar(JarFile jar, String jarBasePath, List<String> classNames) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 检查条目是否在目标包路径下，并且是 .class 文件
            if (name.startsWith(jarBasePath) && name.endsWith(".class")) {
                // 确保是直接子包或更深层包中的类（排除其他不相关路径）
                // 但 names.startsWith(jarBasePath) 已经足够，因为 jar 路径是唯一前缀
                String className = name.replace('/', '.').replace(".class", "");
                classNames.add(className);
            }
        }
    }

    /**
     * 扫描指定代码源（目录或 JAR 文件）中指定包下的所有类，并返回给定类型的非抽象子类型列表
     *
     * @param <T>            目标类型，用于限定返回的 Class 对象必须是其子类型
     * @param classLoader    用于加载找到的类的类加载器
     * @param codeSourceUrl  代码源的位置，必须是有效的 {@code file:} URL，指向一个目录或 JAR 文件
     * @param type           要查找的子类型的父类型（接口或类）
     * @param basePackage    要扫描的基础包名，例如 "com.example"；包名中的点将自动转换为文件系统路径分隔符
     *
     * @return               所有满足条件的子类型的 Class 对象列表；如果扫描过程出错或未找到任何匹配类，则返回空列表（非 {@code null}）
     *
     * @see java.lang.ClassLoader#loadClass(String)
     * @see java.net.URL
     * @see java.util.jar.JarFile
     */
    public static <T> Collection<Class<T>> scanSubclasses(ClassLoader classLoader, URL codeSourceUrl, Class<T> type, String basePackage) {
        List<String> classNames = new ArrayList<>();
        try {
            File source = new File(codeSourceUrl.toURI());
            String basePath = basePackage.replace('.', File.separatorChar);
            String jarBasePath = basePackage.replace('.', '/');

            if (source.isDirectory()) {
                File baseDir = new File(source, basePath);
                if (baseDir.exists() && baseDir.isDirectory()) {
                    collectClassNamesInDirectory(baseDir, basePackage, classNames);
                }
            } else {
                try (JarFile jar = new JarFile(source)) {
                    collectClassNamesInJar(jar, jarBasePath, classNames);
                }
            }

            List<Class<T>> result = new ArrayList<>();
            for (String className : classNames) {
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    if (type.isAssignableFrom(clazz) && !clazz.equals(type)) {
                        result.add((Class<T>) clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // 忽略
                }
            }
            return result;
        } catch (Exception e) {
            // 忽略异常，返回空列表
            return Collections.emptyList();
        }
    }
}
