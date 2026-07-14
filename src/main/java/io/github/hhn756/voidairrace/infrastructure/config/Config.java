package io.github.hhn756.voidairrace.infrastructure.config;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.exception.ConfigException;
import net.kyori.adventure.text.Component;
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

    /**
     * 插件卸载时执行
     * */
    static void load() {
        instance = new Config();
    }

    /**
     * 插件加载时执行
     * */
    static void unload() {
        instance = null;
    }

    /**
     * 获取配置管理器实例
     *
     * @return 配置管理器实例
     */
    public static Config getInstance() throws ConfigException {
        if (instance == null) throw new ConfigException("配置管理器实例不存在", null);
        return instance;
    }

    // ------------

    /** 已加载的配置文件映射，键为配置文件名（无后缀），值为YAML配置对象 */
    private final HashMap<String, YamlConfig> configs = new HashMap<>();

    /** 插件日志记录器 */
    private final Logger logger;

    /**
     * 插件配置目录
     * */
    private final File configFolder;

    /**
     * 私有构造器，初始化配置管理器
     */
    private Config() {
        VoidAirRace mainClass = VoidAirRace.getInstance();
        logger = mainClass.getLogger();
        configFolder = VoidAirRace.getInstance().getDataFolder();
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
                return; // 保存成功，直接返回
            } catch (ConfigException e) {
                if (i == maxRetries) {
                    String msg = "无法保存配置文件 '" + config.getSource().fileName() + "' 总共尝试了 " + maxRetries + " 次都失败了";
                    logger.log(Level.SEVERE, msg, e);
                    throw new ConfigException(
                            msg,
                            e,
                            Component.translatable(
                                    TranslateKeys.Config.Save.CANT_SAVE
                            )
                    );
                }
            }
        }
    }


    /**
     * 尝试保存一个配置文件，最多重试 {@code 3} 次
     *
     * @param config 要保存的、已加载到内存中的配置
     *
     * @see Config#save(FileConfig, int)
     * */
    public void save(@NonNull FileConfig config) throws ConfigException {
        save(config, 3);
    }

    /**
     * 将配置文件名转换为操作系统文件对象（位于插件数据目录下）
     *
     * @param name 配置文件名（无后缀）
     * @return 对应的文件对象
     */
    private File configNameToOSFile(@NonNull String name) {
        return new File(configFolder, name + ".yml");
    }

    /**
     * 将配置文件名转换为JAR内资源路径（用于复制默认配置）
     *
     * @param name 配置文件名（无后缀）
     * @return JAR内资源文件对象（仅用于路径构建）
     */
    private File configNameToJarFile(@NonNull String name) {
        return new File(name + ".yml");
    }

    /**
     * 获取指定配置文件的可观察实例<br>
     * 如果配置文件尚未加载，则会从磁盘加载；若文件不存在，则尝试从JAR复制默认配置或创建空文件
     *
     * @param targetConfigEnum 配置枚举常量，代表要获取的目标文件数据
     *
     * @return 配置实例
     *
     * @throws ConfigException 因各种原因获取配置失败时抛出
     */
    public @NonNull YamlConfig getYmlConfig(@NonNull ConfigFile targetConfigEnum) throws ConfigException {
        String configName = targetConfigEnum.fileName();
        File osFile = configNameToOSFile(configName);

        // 返回已加载的配置对象，防止多次获取导致状态不一致；减少IO次数
        YamlConfig cacheValue = configs.get(configName);
        if (cacheValue != null) return cacheValue;

        // 检查文件的上级目录是否存在
        {
            File parentDir = osFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                logger.log(Level.SEVERE, "无法创建插件配置目录: " + parentDir.getAbsolutePath());
                throw new ConfigException(
                        "无法创建插件配置目录: " + parentDir.getAbsolutePath(),
                        Component.translatable(TranslateKeys.Config.GetYmlConfig.CANT_CREATE_DIR)
                );
            }
        }

        // 确保配置文件存在，不存在将依次尝试从 jar 复制默认值或创建空文件
        {
            if (!osFile.isFile()) {
                // 尝试从 jar 复制
                try {
                    VoidAirRace.getInstance().saveResource(configNameToJarFile(configName).getPath(), false);
                    logger.fine("已从 jar 复制配置文件: " + osFile.getAbsolutePath());
                } catch (IllegalArgumentException e) {
                    // jar 中不存在，创建空文件
                    logger.warning("jar 中不存在默认配置文件 " + configName + "，将尝试创建空文件");
                    if (!createEmptyYmlFile(osFile)) {
                        String msg = "无法创建空配置文件 '" + configName + "'，请检查目录权限";
                        logger.severe(msg);
                        throw new ConfigException(
                                msg,
                                Component.translatable(
                                        TranslateKeys.Config.GetYmlConfig.CANT_CREATE_EMPTY_CONFIG
                                )
                        );
                    }
                }
            }
        }

        // 检查配置文件是否可读
        {
            if (!osFile.canRead()) {
                logger.severe("配置文件 '" + osFile.getAbsolutePath() + "' 无法读取，请检查权限" );
                throw new ConfigException(
                        "配置文件 '" + configName + "' 无法读取，请检查文件权限",
                        Component.translatable(
                                TranslateKeys.Config.GetYmlConfig.FILE_CANT_READ
                        )
                );
            }
        }

        // 从文件加载配置数据
        YamlConfig loadedConfig = new YamlConfig(targetConfigEnum);
        {
            try {
                loadedConfig.load(osFile);
            } catch (Exception e) {
                String msg = "无法加载配置文件 “" + configName + "”";
                logger.log(Level.SEVERE, msg, e);
                throw new ConfigException(
                        msg,
                        Component.translatable(
                                TranslateKeys.Config.GetYmlConfig.CANT_LOAD
                        )
                );
            }
            // 防止配置不全
            mergeDefaults(targetConfigEnum, loadedConfig);
            configs.put(configName, loadedConfig);
        }

        return loadedConfig;
    }

    /**
     * 创建空配置文件，支持重试和详细日志
     *
     * @param osFile 目标文件
     * @return 是否成功创建
     */
    private boolean createEmptyYmlFile(File osFile) {
        int maxRetries = 3;
        for (int i = 1; i <= maxRetries; i++) {
            try {
                if (osFile.createNewFile()) {
                    logger.fine("成功创建空Yml配置文件: " + osFile.getAbsolutePath());
                    return true;
                } else {
                    // createNewFile 返回 false 表示文件已存在
                    logger.warning("创建空配置文件失败，文件已存在: " + osFile.getAbsolutePath());
                    // 如果文件已存在但不是普通文件（例如目录），记录警告
                    if (osFile.isDirectory()) {
                        logger.severe("创建空配置文件失败，配置文件路径被目录占用: " + osFile.getAbsolutePath());
                    }
                    return false; // 文件已存在但可能不是普通文件
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "尝试了 " + i + " 次创建空配置文件，都失败了: " + e.getMessage());
                if (i == maxRetries) {
                    logger.log(Level.SEVERE, "无法创建空配置文件 '" + osFile.getAbsolutePath() + "' 在路径 ，请检查目录权限", e);
                }
            }
        }
        return false;
    }

    /**
     * 将插件 jar 中的默认配置 A 融合到配置对象 B 中<br>
     * 具体来说：对于 A 中的每个字段，如果它不同时存在于 B 中，则将其复制到 B（包括值）
     *
     * @param source 复制字段的源，默认配置（A）
     * @param target 复制字段的目标，配置对象（B）
     */
    private void mergeDefaults(ConfigFile source, YamlConfig target) throws ConfigException {
        String resourceName = source.fileName() + ".yml";
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
                    logger.fine("自动为配置文件 '" + source.fileName() + "' 补充缺失字段: " + key);
                }
            }
        } catch (Exception e) {
            String msg = "读取默认配置文件 '" + resourceName + "' 时发生错误：";
            logger.log(Level.WARNING, msg, e);
            throw new ConfigException(msg, null);
        }
    }
}