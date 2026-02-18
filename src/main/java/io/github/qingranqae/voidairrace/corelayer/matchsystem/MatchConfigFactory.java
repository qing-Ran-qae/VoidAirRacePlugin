package io.github.qingranqae.voidairrace.corelayer.matchsystem;

import io.github.qingranqae.voidairrace.corelayer.config.Config;
import io.github.qingranqae.voidairrace.corelayer.config.ConfigFiles;
import io.github.qingranqae.voidairrace.corelayer.config.GameSettingKey;
import io.github.qingranqae.voidairrace.corelayer.mapsystem.GameMap;
import io.github.qingranqae.voidairrace.corelayer.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.exception.config.ConfigFieldInvalidException;
import io.github.qingranqae.voidairrace.exception.map.MapNotPlayableException;
import org.bukkit.configuration.ConfigurationSection;

public class MatchConfigFactory {
    private static MatchConfigFactory instance;

    public static MatchConfigFactory getInstance() {
        if (instance == null) {
            instance = new MatchConfigFactory();
        }
        return instance;
    }
    private ConfigurationSection gameSetting;

    private MatchConfigFactory() {}

    /**
     * 根据当前配置创建一个比赛配置对象
     *
     * @return 创建的比赛配置对象
     */
    public MatchConfig createDefaultConfig() throws ConfigFieldInvalidException, MapNotPlayableException {
        this.gameSetting = Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS);

        // 创建配置对象
        MatchConfig configInst = new MatchConfig(
                getGameMap(),
                getMatchDuration()
        );
        configInst.validate(); // 检查数据有效性，异常在这个方法里抛出
        return configInst;
    }

    private GameMap getGameMap() throws MapNotPlayableException {
        String mapName = gameSetting.getString(GameSettingKey.SELECTED_MAP_ID.getPath());
        MapRegistry mapRegistry = MapRegistry.getInstance();
        if (mapName == null || mapName.isEmpty() || !mapRegistry.containsMap(mapName)) {
            throw new MapNotPlayableException("已选地图 ID 为 null 或空字符串，也可能是不存在对应 ID 的地图");
        }
        return mapRegistry.getMapById(mapName);
    }

    private int getMatchDuration() {
        return gameSetting.getInt("matchDuration", -1); // 默认值设为-1表示配置项不存在或无效
    }
}