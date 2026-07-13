package io.github.hhn756.voidairrace.service.config.files;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.service.config.ConfigFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PublicFiles {
    /**
     * 存放所有临时文件的目录
     * */
    public static final Path TEMP_DIR = Paths.get(
            VoidAirRace.getInstance().getDataFolder().getAbsolutePath(),
            "temp/"
    );

    /**
     * 游戏玩法相关设置
     * */
    public static final ConfigFile GAME_SETTINGS = new ConfigFile("game_settings", GameSettingKeys.ALL_KEYS);
    /**
     * 储存所有需要持久化的状态标记
     * */
    public static final ConfigFile FLAGS = new ConfigFile("flags", FlagsKeys.ALL_KEYS);
    /**
     * 全局设置
     * */
    public static final ConfigFile GLOBAL_SETTINGS = new ConfigFile("global_settings", GlobalSettingKeys.ALL_KEYS);
}