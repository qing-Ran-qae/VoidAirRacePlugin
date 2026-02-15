package io.github.qingranqae.voidairrace.match;

import io.github.qingranqae.voidairrace.config.Config;
import io.github.qingranqae.voidairrace.mapregistry.GameMap;
import io.github.qingranqae.voidairrace.mapregistry.MapRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

/**
 * 一局比赛的配置
 */
public record MatchConfig(
        GameMap gameMap,
        int duration
) {

    /**
     * 根据当前配置创建一个比赛配置对象
     *
     * @return 创建的比赛配置对象
     */
    public static MatchConfig createDefaultConfig() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Config configInst = Config.getInstance();

        GameMap gameMap = MapRegistry.getMapById(configInst.getSelectedMapId()).getDeclaredConstructor().newInstance();

        // 创建配置对象
        return new MatchConfig(
                gameMap,
                configInst.getMatchDuration()
        );
    }

    public MatchConfig withGameMap(GameMap gameMap) {
        return new MatchConfig(gameMap,  this.duration);
    }
}
