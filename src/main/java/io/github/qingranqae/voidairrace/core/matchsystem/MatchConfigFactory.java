package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.core.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.result.match.ConfigCreationResult;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.service.config.files.GameSettingKeys;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 比赛配置工厂，负责根据当前插件配置创建默认的 {@link MatchConfig} 实例。<br>
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
        if (instance == null) instance = new MatchConfigFactory();
        return instance;
    }

    /** 当前游戏设置配置节，缓存用于构建配置。 */
    private ObservableYamlConfiguration gameSetting;

    /** 私有构造器，防止外部实例化。 */
    private MatchConfigFactory() {}

    /**
     * 根据当前游戏设置创建一个默认的比赛配置对象。
     *
     * @return 构建的 {@link MatchConfig} 实例
     */
    public @NonNull ConfigCreationResult createDefaultConfig() {
        this.gameSetting = Config.getInstance().getConfig(PublicFiles.GAME_SETTINGS);

        // 获取游戏地图
        PlayableGameMap gameMap = getGameMap();
        if (gameMap == null) return ConfigCreationResult.failure(
                Component.translatable("void_air_race.match.match_config_factory.create_default_config.failure.map_not_playable"));

        // 创建配置对象
        MatchConfig configInst = new MatchConfig(
                gameMap,
                getMatchDuration(),
                getContestants()
        );
        // 检查配置有效性
        configInst.validate();
        return ConfigCreationResult.success(configInst);
    }

    /**
     * 从配置中获取并实例化当前选中的地图。
     *
     * @return 地图实例
     */
    private PlayableGameMap getGameMap() {
        String mapId = gameSetting.getString(GameSettingKeys.SELECTED_MAP_ID);
        MapRegistry mapRegistry = MapRegistry.getInstance();
        if (mapId == null || mapId.isEmpty() || !mapRegistry.containsMap(mapId)) {
            return null;
        }
        if (mapRegistry.getMapById(mapId) instanceof PlayableGameMap map) {
            return map;
        }
        return null;
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