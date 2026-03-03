package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.core.config.ConfigFiles;
import io.github.qingranqae.voidairrace.core.config.GameSettingKey;
import io.github.qingranqae.voidairrace.core.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.core.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.exception.ConfigFieldInvalidException;
import io.github.qingranqae.voidairrace.exception.MapNotPlayableException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 比赛配置工厂，负责根据当前插件配置创建默认的 {@link MatchConfig} 实例。
 * 单例模式，通过 {@link #getInstance()} 获取实例。
 */
public class MatchConfigFactory {
    private static MatchConfigFactory instance;

    /**
     * 获取工厂实例。
     *
     * @return 工厂单例
     */
    public static MatchConfigFactory getInstance() {
        if (instance == null) {
            instance = new MatchConfigFactory();
        }
        return instance;
    }

    /** 当前游戏设置配置节，缓存用于构建配置。 */
    private ObservableYamlConfiguration gameSetting;

    /** 私有构造器，防止外部实例化。 */
    private MatchConfigFactory() {}

    /**
     * 根据当前游戏设置（gameSetting.yml）创建一个默认的比赛配置对象。
     * 配置包括：
     * <ul>
     *     <li>地图：从 {@link GameSettingKey#SELECTED_MAP_ID} 读取并实例化</li>
     *     <li>时长：从 {@link GameSettingKey#MATCH_DURATION} 读取，默认 12000 tick</li>
     *     <li>参赛者：所有在线且已分配队伍的玩家</li>
     * </ul>
     *
     * @return 构建的 {@link MatchConfig} 实例
     * @throws ConfigFieldInvalidException 配置字段无效时抛出（由 {@link MatchConfig#validate()} 抛出）
     * @throws MapNotPlayableException     所选地图不可游玩时抛出
     */
    public MatchConfig createDefaultConfig() throws ConfigFieldInvalidException, MapNotPlayableException {
        this.gameSetting = Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS);

        // 创建配置对象
        MatchConfig configInst = new MatchConfig(
                getGameMap(),
                getMatchDuration(),
                getContestants()
        );
        configInst.validate(); // 检查数据有效性，异常在这个方法里抛出
        return configInst;
    }

    /**
     * 从配置中获取并实例化当前选中的地图。
     *
     * @return 地图实例
     * @throws MapNotPlayableException 如果地图 ID 无效或地图不可游玩
     */
    private PlayableGameMap getGameMap() {
        String mapId = gameSetting.getString(GameSettingKey.SELECTED_MAP_ID);
        MapRegistry mapRegistry = MapRegistry.getInstance();
        if (mapId == null || mapId.isEmpty() || !mapRegistry.containsMap(mapId)) {
            throw new MapNotPlayableException("在设置中指定的 游戏地图ID 为 null 或空字符串，也可能是不存在对应 ID 的地图");
        }
        if (mapRegistry.getMapById(mapId) instanceof PlayableGameMap map) {
            return map;
        }
        throw new MapNotPlayableException("所选地图 '" + mapId + "' 不可游玩");
    }

    /**
     * 从配置中获取比赛时长，若未设置则返回默认值 12000 tick。
     *
     * @return 比赛时长（tick）
     */
    private int getMatchDuration() {
        return gameSetting.getInt("matchDuration", 12000);
    }

    /**
     * 获取当前在线且已加入队伍的玩家集合。
     *
     * @return 参赛玩家集合
     */
    private Collection<? extends Player> getContestants() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        ArrayList<Player> result = new ArrayList<>(onlinePlayers.size());
        for (Player player : onlinePlayers) {
            if (TeamRoster.getInstance().getEntityTeam(player) != null) {
                result.add(player);
            }
        }
        return result;
    }
}