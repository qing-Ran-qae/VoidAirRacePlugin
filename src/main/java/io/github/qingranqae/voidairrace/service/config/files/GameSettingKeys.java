package io.github.qingranqae.voidairrace.service.config.files;

import org.bukkit.configuration.ConfigurationSection;

/**
 * 游戏设置配置文件（game_setting.yml）中定义的配置键枚举。
 * 每个枚举常量对应一个配置项，包含其在文件中的路径和数据类型。
 */
public enum GameSettingKeys implements ConfigKeys {
    /** 当前选中的比赛地图ID。 */
    SELECTED_MAP_ID("selected_map_id", String.class),

    /** 比赛持续时间，单位为 tick（20 ticks = 1秒）。 */
    MATCH_DURATION("match_duration", Integer.class),

    /** 玩家出生点位置的完整配置映射。 */
    SPAWN_LOCATION("spawn_location", ConfigurationSection.class),

    /** 出生点所在的世界名称。 */
    SPAWN_WORLD_NAME(SPAWN_LOCATION.getPath() + ".world_name", String.class),

    /** 出生点 X 坐标。 */
    SPAWN_X(SPAWN_LOCATION.getPath() + ".x",  Double.class),

    /** 出生点 Y 坐标。 */
    SPAWN_Y(SPAWN_LOCATION.getPath() + ".y",  Double.class),

    /** 出生点 Z 坐标。 */
    SPAWN_Z(SPAWN_LOCATION.getPath() + ".z",  Double.class),

    /** 出生点偏航角（水平旋转）。 */
    SPAWN_YAW(SPAWN_LOCATION.getPath() + ".yaw",  Float.class),

    /** 出生点俯仰角（垂直旋转）。 */
    SPAWN_PITCH(SPAWN_LOCATION.getPath() + ".pitch",  Float.class),

    /** 最大竞技场数量 */
    MAX_ARENAS("max_arenas", Integer.class);;

    /** 配置项在YAML中的路径。 */
    private final String path;

    /** 配置项的数据类型。 */
    private final Class<?> type;

    /**
     * 构造游戏设置配置键。
     *
     * @param path 配置项路径
     * @param type 配置项数据类型
     */
    GameSettingKeys(String path, Class<?> type) {
        this.path = path;
        this.type = type;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}