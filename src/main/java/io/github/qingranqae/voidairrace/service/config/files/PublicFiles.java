package io.github.qingranqae.voidairrace.service.config.files;

/**
 * 公共配置文件
 */
public enum PublicFiles implements ConfigFiles {
    GAME_SETTINGS("game_setting", GameSettingKeys.values()),
    FLAGS("flags", FlagsKeys.values()),;

    private final String fileName;
    private final ConfigKeys[] keys;

    PublicFiles(String fileName, ConfigKeys[] keys) {
        this.fileName = fileName;
        this.keys = keys;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public ConfigKeys[] getKeys() {
        return keys;
    }
}