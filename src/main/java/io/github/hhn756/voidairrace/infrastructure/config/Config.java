package io.github.hhn756.voidairrace.infrastructure.config;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.exception.ConfigException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

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
 * 配置文件管理器，负责加载、保存和获取插件配置
 * 采用单例模式，通过 {@link #getInstance()} 获取实例
 */
public class Config {
    private static Config instance;

    /** 插件卸载时执行 */
    static void load() {
        instance = new Config();
    }

    /** 插件加载时执行 */
    static void unload() {
        instance = null;
    }

    /**
     * 获取配置管理器实例
     *
     * @return 配置管理器实例
     *
     * @throws NullPointerException 如果配置管理器实例不存在
     */
    public static @NonNull Config getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("配置管理器实例不存在");
        return instance;
    }

    // ------------

    /** 已加载的配置文件映射，键为配置文件名（无后缀），值为YAML配置对象 */
    private final @NonNull HashMap<@NonNull String, @NonNull YamlConfig> configs = new HashMap<>();

    /** 插件日志记录器 */
    private final @NonNull Logger logger;

    /**
     * 插件配置目录
     * */
    private final @NonNull File configFolder;

    /**
     * 私有构造器，初始化配置管理器
     */
    private Config() {
        VoidAirRace mainClass = VoidAirRace.getInstance();
        logger = mainClass.getLogger();
        configFolder = mainClass.getDataFolder();
    }

    /**
     * 将所有已加载的配置文件保存到磁盘（不会删除内存中的数据）<br>
     * 发生异常时每个文件最多重试3次
     *
     * @throws ConfigException 如果有任何文件在重试后仍然保存失败
     */
    public void saveAll() throws ConfigException {
        int maxRetries = 3;
        for (Map.Entry<String, YamlConfig> i : configs.entrySet()) {
            FileConfig config = i.getValue();
            save(config, maxRetries);
        }
    }

    /**
     * 尝试保存一个配置文件，支持重试
     *
     * @param config 要保存的、已加载到内存中的配置
     * @param maxRetries 最大重试次数
     *
     * @throws ConfigException 如果达到最大重试次数后仍然保存失败
     */
    public void save(@NonNull FileConfig config, int maxRetries) throws ConfigException {
        for (int i = 1; i <= maxRetries; i++) {
            try {
                config.save();
                return;
            } catch (ConfigException ignored) {}
        }
        logAndThrow(
                Level.SEVERE,
                "无法保存配置文件 '" + config.getDefine().filePath() + "' 总共尝试了 " + maxRetries + " 次都失败了");
    }


    /**
     * 尝试保存一个配置文件，最多重试 {@code 3} 次
     *
     * @param config 要保存的、已加载到内存中的配置
     *
     * @throws ConfigException 如果达到最大重试次数后仍然保存失败
     *
     * @see Config#save(FileConfig, int)
     * */
    public void save(@NonNull FileConfig config) throws ConfigException {
        save(config, 3);
    }

    /**
     * 获取指定配置文件的可观察实例<br>
     * 如果配置文件尚未加载，则会从磁盘加载；若文件不存在，则尝试从JAR复制默认配置或创建空文件
     *
     * @param configDefinition 要读取的配置文件的定义对象
     *
     * @return 配置实例
     *
     * @throws ConfigException 因各种原因获取配置失败时抛出
     */
    public @NonNull YamlConfig getYmlConfig(@NonNull ConfigDefinition configDefinition) throws ConfigException {
        String definedPath = configDefinition.filePath();
        File dataSourceFile = new File(configFolder, definedPath + ".yml");

        // 返回已加载的配置对象，防止平行配置对象导致状态混乱；减少IO次数
        YamlConfig cacheValue = configs.get(definedPath);
        if (cacheValue != null) return cacheValue;

        // 检查文件的上级目录是否存在
        File parentDir = dataSourceFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) logAndThrow(
                Level.SEVERE,
                "无法创建插件配置目录: " + parentDir.getAbsolutePath());

        // 确保配置文件存在
        if (!dataSourceFile.isFile()) {
            // 尝试从 jar 复制
            try {
                VoidAirRace.getInstance().saveResource(definedPath + ".yml", false);
            } catch (IllegalArgumentException e) {
                // jar 中不存在，创建空文件
                logger.warning("jar 中不存在默认配置文件 " + definedPath + "，将尝试创建空文件");
                createEmptyYmlFile(dataSourceFile);
            }
        }

        // 检查文件是否可读
        if (!dataSourceFile.canRead()) logAndThrow(
                Level.SEVERE,
                "配置文件 '" + dataSourceFile.getAbsolutePath() + "' 无法读取，请检查权限");

        // 从文件加载配置数据
        return loadYmlConfig(configDefinition, dataSourceFile);
    }

    /**
     * 内部辅助方法，用于加载一个yml配置文件
     *
     * @param configDefinition 要加载的文件的定义对象
     * @param dataSource       从指定文件读取配置数据
     * */
    private @NonNull YamlConfig loadYmlConfig(@NonNull ConfigDefinition configDefinition, File dataSource) throws ConfigException {
        YamlConfig ymlConfig = new YamlConfig(configDefinition);
        try {
            ymlConfig.load(dataSource);
        } catch (Exception e) {
            logAndThrow(Level.SEVERE, "无法加载配置文件 “" + configDefinition.filePath() + "”");
        }
        // 防止配置不全
        mergeDefaults(configDefinition, ymlConfig);

        // 记录
        configs.put(configDefinition.filePath(), ymlConfig);
        return ymlConfig;
    }

    /**
     * 内部辅助方法，记录指定等级日志然后抛出配置异常
     *
     * @param logLevel 日志等级
     * @param msg      日志和异常消息
     * */
    private void logAndThrow(@NonNull Level logLevel, @NonNull String msg) throws ConfigException {
        logger.log(logLevel, msg);
        throw new ConfigException(msg, null);
    }

    /**
     * 创建空配置文件，支持重试和详细日志
     *
     * @param osFile 目标文件
     */
    private void createEmptyYmlFile(File osFile) {
        int maxRetries = 3;
        String absolutePath = osFile.getAbsolutePath();
        for (int n = 1; n <= maxRetries; n++) {
            try {
                if (osFile.createNewFile()) return;

                // 防止无用尝试
                if (osFile.isDirectory()) {
                    String msg = "尝试创建空配置文件失败，路径被目录占用: " + absolutePath;
                    logger.severe(msg);
                    throw new ConfigException(msg, null);
                } else {
                    String msg = "尝试创建空配置文件失败，路径被其他项目占用： " + absolutePath;
                    logger.severe(msg);
                    throw new ConfigException(msg, null);
                }
            } catch (IOException e) {
                logger.warning("第 " + n + " 次尝试创建空配置文件 “" + absolutePath + "”时出现异常: " + e.getMessage());
            }
        }
        String msg = "尝试了 "+ maxRetries + "次创建空配置文件 “" + absolutePath + "”，都失败了，请检查目录权限等可能因素";
        logger.severe(msg);
        throw new ConfigException(msg, null);
    }

    /**
     * 将插件 jar 中的默认配置 A 融合到配置对象 B 中<br>
     * 具体来说：对于 A 中的每个字段，如果它不同时存在于 B 中，则将其复制到 B（包括值）
     *
     * @param source 复制字段的源，默认配置（A）
     * @param target 复制字段的目标，配置对象（B）
     */
    private void mergeDefaults(ConfigDefinition source, YamlConfig target) throws ConfigException {
        String resourceName = source.filePath() + ".yml";
        try (InputStream in = VoidAirRace.getInstance().getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                logger.warning("jar 中未找到默认配置文件: “" + resourceName + "”，无法为其补充缺失字段");
                return;
            }

            // 读取 JAR 中的默认配置
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8)
            );

            // 遍历默认配置的所有键（包括嵌套键）
            for (String key : defaultConfig.getKeys(true)) {
                if (!target.contains(key)) {
                    target.set(key, defaultConfig.get(key));
                    logger.fine("自动为配置文件 '" + source.filePath() + "' 补充缺失字段: " + key);
                }
            }
        } catch (Exception e) {
            String msg = "读取默认配置文件 '" + resourceName + "' 时发生错误：";
            logger.log(Level.WARNING, msg, e);
            throw new ConfigException(msg, null);
        }
    }
}
