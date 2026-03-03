package io.github.qingranqae.voidairrace.core.config;

/**
 * 插件配置文件的枚举，每个枚举常量对应一个配置文件，并包含该文件中定义的配置键列表。
 */
public enum ConfigFiles {
    /** 游戏设置配置文件，包含 {@link GameSettingKey} 中定义的所有键。 */
    GAME_SETTINGS("gameSetting", GameSettingKey.values()),

    /** 标志配置文件，包含 {@link FlagsKey} 中定义的所有键。 */
    FLAGS("flags", FlagsKey.values());

    /** 配置文件名（不含扩展名）。 */
    private final String fileName;

    /** 该配置文件中定义的所有配置键。 */
    private final ConfigKey[] keys;

    /**
     * 构造配置枚举项。
     *
     * @param fileName 配置文件名（不含 .yml 后缀）
     * @param keys     该文件中定义的配置键数组
     */
    ConfigFiles(String fileName, ConfigKey[] keys) {
        this.fileName = fileName;
        this.keys = keys;
    }

    /**
     * 获取配置文件名（不含扩展名）。
     *
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取该配置文件中定义的所有配置键。
     *
     * @return 配置键数组
     */
    public ConfigKey[] getKeys() {
        return keys;
    }
}