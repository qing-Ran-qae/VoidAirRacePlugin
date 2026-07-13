package io.github.hhn756.voidairrace.service.config.files;

import io.github.hhn756.voidairrace.service.config.ConfigKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * 游戏设置配置文件（game_settings.yml）中定义的配置键
 * 使用静态常量代替枚举，实现完全的类型安全
 */
public final class GameSettingKeys {
    private GameSettingKeys() {}

    /**
     * 已选择的地图ID，一定是{@code "namespace:key"}格式的字符串形式的{@link NamespacedKey}
     * <p>因为{@link NamespacedKey}没有实现{@link ConfigurationSerializable}：</p>
     * <ul>
     *     <li><b>读取时</b>：需对字段原始值调用{@link NamespacedKey#fromString(String)}</li>
     *     <li><b>写入时</b>：需对{@link NamespacedKey}对象调用{@link NamespacedKey#toString()}，如果已经是字符串格式则直接写入</li>
     * </ul>
     * */
    public static final ConfigKey<String> SELECTED_MAP_ID = new ConfigKey<>("selected_map_id"){};
    /** 比赛持续时间 */
    public static final ConfigKey<Integer> MATCH_DURATION = new ConfigKey<>("match_duration"){};
    /** 最大同时运行竞技场数量 */
    public static final ConfigKey<Integer> MAX_ARENAS = new ConfigKey<>("max_arenas"){};

    /**
     * 所有配置键的集合，便于批量注册
     */
    public static final ConfigKey<?>[] ALL_KEYS = {
            SELECTED_MAP_ID, MATCH_DURATION, MAX_ARENAS
    };
}