package io.github.qingranqae.voidairrace.core.playerstatemanager;

import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.core.config.ConfigFiles;
import io.github.qingranqae.voidairrace.core.config.GameSettingKey;
import io.github.qingranqae.voidairrace.core.config.ObservableYamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class Util {
    static ObservableYamlConfiguration gameSettings = Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS);
    static String spawnWorldName;
    static double spawnX;
    static double spawnY;
    static double spawnZ;

    static {
        updateSpawnLocation();
    }

    /**
     * 将玩家传送到出生点
     *
     * @param player 指定玩家
     */
    public static void tpToSpawnPoint(Player player) {
        player.teleport(new Location(Bukkit.getWorld(spawnWorldName), spawnX, spawnY, spawnZ));
    }

    /**
     * 读取配置，然后更新出生点位置
     * */
    public static void updateSpawnLocation() {
        spawnWorldName = gameSettings.getString(GameSettingKey.SPAWN_WORLD_NAME);
        spawnX = gameSettings.getDouble(GameSettingKey.SPAWN_X);
        spawnY = gameSettings.getDouble(GameSettingKey.SPAWN_Y);
        spawnZ = gameSettings.getDouble(GameSettingKey.SPAWN_Z);
    }
}
