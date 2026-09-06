package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.Categories;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.map.maps.grassland.GrassLand;
import io.github.hhn756.voidairrace.core.match.ComponentPriority;
import io.github.hhn756.voidairrace.core.match.DataKey;
import io.github.hhn756.voidairrace.core.match.Match;
import io.github.hhn756.voidairrace.core.match.componentbase.*;
import io.github.hhn756.voidairrace.infrastructure.config.Config;
import io.github.hhn756.voidairrace.infrastructure.config.files.GameSettingKeys;
import io.github.hhn756.voidairrace.infrastructure.config.files.PublicFiles;
import io.github.hhn756.voidairrace.infrastructure.registry.Registry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class MapComp extends MatchComp
        implements ConfigurableComp<MapComp.MapECFG, MapComp.MapConfig>,
        StartableComp<CustomData, MapComp.MapSC>,
        EndableComp<CustomData, CustomData> {

    // -------------------- ConfigurableComp --------------------

    public static final DataKey<MapConfig> CONFIG_KEY = DataKey.of(MapComp.class, MapConfig.class);

    @Override
    public DataKey<MapConfig> getConfigKey() {
        return CONFIG_KEY;
    }

    @Override
    public @NonNull CustomConfigResult<MapConfig> createCustomConfig(@Nullable MapECFG expected) {
        NamespacedKey mapId;

        if (expected != null) {
            mapId = expected.expectedMapId();
        } else {
            // 回退到默认配置
            return new CustomConfigResult<>(
                    true,
                    createDefaultConfig().getValue(),
                    null
            );
        }
        MapEntry mapEntry = Registry.getInstance().category(Categories.MAP).get(expected.expectedMapId());

        // 检查 变量是否为null 和 地图是否存在
        if (mapEntry == null) {
            return new CustomConfigResult<>(
                    false,
                    null,
                    Component.translatable(TranslateKeys.Map.CREATE_DEFAULT_CONFIG_MAP_NOTFOUND)
            );
        }

        // 检查地图是否可玩
        if (!mapEntry.isPlayable()) {
            return new CustomConfigResult<>(
                    false,
                    null,
                    Component.translatable(TranslateKeys.Map.CREATE_CUSTOM_CONFIG_MAP_NOT_PLAYABLE)
            );
        }

        return CustomConfigResult.success(new MapConfig((PlayableGameMap) mapEntry.newInstance()));
    }

    @Override
    public @NonNull DefaultConfigResult<MapConfig> createDefaultConfig() {
        NamespacedKey selectedMapId = NamespacedKey.fromString(
                Config.getInstance()
                        .getYmlConfig(PublicFiles.GAME_SETTINGS)
                        .get(GameSettingKeys.SELECTED_MAP_ID, GrassLand.getID().toString())
        );
        GameMap map = Registry.getInstance().category(Categories.MAP).get(selectedMapId).newInstance();
        if (!(map instanceof PlayableGameMap playableMap)) {
            return new DefaultConfigResult<>(
                    false,
                    null,
                    Component.translatable(TranslateKeys.Map.CREATE_DEFAULT_CONFIG_MAP_NOT_PLAYABLE)
            );
        }
        return DefaultConfigResult.success(new MapConfig(playableMap));
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getConfigPriority() {
        return ComponentPriority.LOW.getValue();
    }

    // -------------------- StartableComp --------------------

    private static final DataKey<MapSC> START_KEY = DataKey.of(MapComp.class, MapSC.class);

    @Override
    public @NonNull DataKey<MapSC> getSCK() {
        return START_KEY;
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getInstallPriority() {
        return ComponentPriority.LOW.getValue();
    }

    @Override
    public StartableComp.@NonNull InstallResult<MapSC> install(
            @NonNull Match match,
            @Nullable CustomData startArg) {

        PlayableGameMap gameMap = match.getConfigData(MapComp.CONFIG_KEY).map();

        // 调用地图的开始方法
        PlayableGameMap.StartResult startResult = gameMap.start(match);
        if (!startResult.isSuccess()) {
            Component msg = startResult.getDisplayMessage() == null
                    ? Component.translatable(TranslateKeys.Map.MAP_COMPONENT_SELECTED_START_FAILED)
                    : startResult.getDisplayMessage();
            return new InstallResult<>(false, msg, null);
        }

        // 注册是 bukkit 事件监听器的地图
        if (gameMap instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, VoidAirRace.getInstance());
        }

        return InstallResult.success(new MapSC(gameMap));
    }

    // -------------------- EndableComp --------------------

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getUninstallPriority() {
        return ComponentPriority.HIGH.getValue();
    }

    @Override
    public @NonNull ComponentUninstallResult<CustomData> uninstall(
            @NonNull Match match,
            @Nullable CustomData endArg) {
        PlayableGameMap gameMap = match.getConfig().getData(MapComp.CONFIG_KEY).map();
        gameMap.over(match);
        if (gameMap instanceof Listener listener) {
            HandlerList.unregisterAll(listener);
        }
        return new ComponentUninstallResult<>(true, null, null);
    }

    /**
     * @param map 比赛所使用的游戏地图
     * */
    public record MapConfig(@NonNull PlayableGameMap map) implements CustomData {
        @Override
        public @NonNull Class<? extends MatchComp> getSource() {
            return MapComp.class;
        }
    }

    public record MapECFG(@NonNull NamespacedKey expectedMapId) implements CustomData {
        @Override
        public @NonNull Class<? extends MatchComp> getSource() {
            return MapComp.class;
        }
    }

    public record MapSC(@NonNull PlayableGameMap gameMap) implements CustomData {
        @Override
        public @NonNull Class<? extends MatchComp> getSource() {
            return MapComp.class;
        }
    }
}
