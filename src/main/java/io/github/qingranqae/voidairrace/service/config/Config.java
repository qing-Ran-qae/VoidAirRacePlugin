package io.github.qingranqae.voidairrace.service.config;

import io.github.qingranqae.voidairrace.service.config.files.ConfigFiles;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 配置文件管理器，负责加载、保存和获取插件配置。
 * 采用单例模式，通过 {@link #getInstance()} 获取实例。
 */
public class Config {
    private static Config instance;

    /**
     * 获取配置管理器实例。
     *
     * @return 配置管理器实例
     * @throws IllegalStateException 如果实例尚未初始化（未调用 {@link #getInstance(JavaPlugin)}）
     */
    public static Config getInstance() {
        if (instance == null) {
            throw new IllegalStateException("配置类还未初始化，无法获取示例");
        }
        return instance;
    }

    /**
     * 初始化配置管理器并获取实例。
     *
     * @param mainClass 插件主类实例
     * @return 配置管理器实例
     */
    public static Config getInstance(JavaPlugin mainClass) {
        if (instance == null) {
            instance = new Config(mainClass);
        }
        return instance;
    }

    // ------------

    /** 已加载的配置文件映射，键为配置文件名（无后缀），值为可观察的YAML配置对象。 */
    private final HashMap<String, ObservableYamlConfiguration> configs = new HashMap<>();

    /** 插件主类实例，用于获取数据文件夹和资源。 */
    private final JavaPlugin mainClass;

    /** 插件日志记录器。 */
    private final Logger logger;

    /**
     * 私有构造器，初始化配置管理器。
     *
     * @param mainClass 插件主类实例
     */
    private Config(JavaPlugin mainClass) {
        this.mainClass = mainClass;
        this.logger = mainClass.getLogger();
    }

    /**
     * 从磁盘重新加载所有已加载的配置文件。
     * <p>
     * 警告：如果在保存前调用此方法，将丢失内存中未保存的修改。
     */
    public void loadAllConfigs() {
        for (Map.Entry<String, ObservableYamlConfiguration> entry : configs.entrySet()) {
            String configName = entry.getKey();
            ObservableYamlConfiguration config = entry.getValue();
            File file = configNameToOSFile(configName);
            try {
                config.load(file);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "无法重新加载配置文件 '" + configName + "'", e);
            }
        }
    }

    /**
     * 将所有已加载的配置文件保存到磁盘。
     * 发生异常时每个配置文件最多重试3次。
     */
    public void saveAllConfigs() {
        int maxRetries = 3;
        for (Map.Entry<String, ObservableYamlConfiguration> i : configs.entrySet()) {
            String configName  = i.getKey();
            FileConfiguration config = i.getValue();
            saveOneConfig(configName, config, maxRetries);
        }
    }

    /**
     * 尝试保存一个配置文件，支持重试。
     *
     * @param configName 配置文件名（无后缀）
     * @param config     配置对象
     * @param maxRetries 最大重试次数
     */
    public void saveOneConfig(String configName, FileConfiguration config, int maxRetries) {
        for (int i = 1; i <= maxRetries; i++) {
            try {
                config.save(configNameToOSFile(configName));
                break;
            } catch (IOException e) {
                if (i == maxRetries) {
                    logger.log(Level.SEVERE, "无法保存配置文件 '" + configName + "' 总共尝试了 " + maxRetries + " 次都失败了", e);
                }
            }
        }
    }

    /**
     * 将配置文件名转换为操作系统文件对象（位于插件数据文件夹下）。
     *
     * @param name 配置文件名（无后缀）
     * @return 对应的文件对象
     */
    private File configNameToOSFile(String name) {
        return new File(mainClass.getDataFolder(), name + ".yml");
    }

    /**
     * 将配置文件名转换为JAR内资源路径（用于复制默认配置）。
     *
     * @param name 配置文件名（无后缀）
     * @return JAR内资源文件对象（仅用于路径构建）
     */
    private File configNameToJarFile(String name) {
        return new File(name + ".yml");
    }

    /**
     * 获取指定配置文件的可观察实例。
     * 如果配置文件尚未加载，则会从磁盘加载；若文件不存在，则尝试从JAR复制默认配置或创建空文件。
     *
     * @param config 配置枚举常量
     * @return 可观察的YAML配置对象，若发生严重错误则返回 null
     */
    public ObservableYamlConfiguration getConfig(ConfigFiles config) {
        String configName = config.getFileName();
        File osFile = configNameToOSFile(configName);

        // 检查上级目录
        File parentDir = osFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            logger.log(Level.SEVERE, "无法创建插件配置目录: " + parentDir.getAbsolutePath());
            return null;
        }

        // 确保配置文件存在（从 jar 复制或创建空文件）
        if (!osFile.isFile()) {
            // 尝试从 jar 复制
            try {
                mainClass.saveResource(configNameToJarFile(configName).getPath(), false);
                logger.fine("已从 jar 复制配置文件: " + osFile.getAbsolutePath());
            } catch (IllegalArgumentException e) {
                // jar 中不存在，创建空文件
                logger.warning("jar 中不存在配置文件 " + configName + "，将尝试创建空文件");
                if (!createEmptyConfigFile(osFile, configName)) {
                    logger.severe("无法创建空配置文件 '" + configName + "'，请检查目录权限");
                    return null;
                }
            }
        }

        // 检查文件是否可读
        if (!osFile.canRead()) {
            logger.severe("配置文件 '" + configName + "' 无法读取，请检查文件权限: " + osFile.getAbsolutePath());
            return null;
        }

        // 获取或创建配置实例
        ObservableYamlConfiguration obsConfig = configs.get(configName);
        if (obsConfig == null) {
            obsConfig = new ObservableYamlConfiguration(config);

            // 注册该文件对应的所有 ConfigKey
            obsConfig.registerConfigKeys(config.getKeys());

            // 从文件加载配置
            try {
                obsConfig.load(osFile);
                logger.fine("成功加载配置文件: " + osFile.getAbsolutePath());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "无法加载配置文件 '" + configName + "'", e);
                // 即使加载失败，仍然返回对象（可能为空配置），但避免返回 null 导致其他模块 NPE
            }
            configs.put(configName, obsConfig);
        }
        return obsConfig;
    }

    /**
     * 创建空配置文件，带重试和详细日志。
     *
     * @param osFile     目标文件
     * @param configName 配置名称（用于日志）
     * @return 是否成功创建
     */
    private boolean createEmptyConfigFile(File osFile, String configName) {
        int maxRetries = 3;
        for (int i = 1; i <= maxRetries; i++) {
            try {
                if (osFile.createNewFile()) {
                    logger.fine("成功创建空配置文件: " + osFile.getAbsolutePath());
                    return true;
                } else {
                    // createNewFile 返回 false 表示文件已存在
                    logger.warning("创建空配置文件失败（文件已存在？）: " + osFile.getAbsolutePath());
                    // 如果文件已存在但不是普通文件（例如目录），记录警告
                    if (osFile.isDirectory()) {
                        logger.severe("配置文件路径是一个目录，无法使用: " + osFile.getAbsolutePath());
                    }
                    return false; // 文件已存在但可能不是普通文件
                }
            } catch (IOException e2) {
                logger.log(Level.WARNING, "创建空配置文件尝试 " + i + " 失败: " + e2.getMessage());
                if (i == maxRetries) {
                    logger.log(Level.SEVERE, "无法创建空配置文件 '" + configName + "' 在路径 " + osFile.getAbsolutePath() + "，请检查目录权限", e2);
                }
            }
        }
        return false;
    }

    /**
     * 合并 JAR 内的默认配置到现有配置文件（补充缺失的字段）。
     * 此方法目前未被使用，但保留以备将来扩展。
     *
     * @param file   配置文件枚举
     * @param config 当前配置对象
     */
    private void mergeDefaults(ConfigFiles file, ObservableYamlConfiguration config) {
        String resourceName = file.getFileName() + ".yml";
        try (InputStream in = mainClass.getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                logger.warning("Jar 中未找到默认配置文件: " + resourceName);
                return;
            }

            // 读取 JAR 中的默认配置
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8)
            );

            // 遍历默认配置的所有键（包括嵌套键）
            for (String key : defaultConfig.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defaultConfig.get(key));
                    logger.fine("为配置文件 '" + file.getFileName() + "' 补充缺失字段: " + key);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "读取默认配置文件 '" + resourceName + "' 时发生错误：", e);
        }
    }
}