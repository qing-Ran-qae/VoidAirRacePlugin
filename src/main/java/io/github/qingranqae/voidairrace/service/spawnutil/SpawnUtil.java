package io.github.qingranqae.voidairrace.service.spawnutil;

import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.service.config.files.GameSettingKeys;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnUtil {
    static ObservableYamlConfiguration gameSetting = Config.getInstance().getConfig(PublicFiles.GAME_SETTINGS);
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
        player.teleport(getSpawnLocation());
    }

    /**
     * 读取配置，然后更新出生点位置
     * */
    public static void updateSpawnLocation() {
        spawnWorldName = gameSetting.getString(GameSettingKeys.SPAWN_WORLD_NAME);
        spawnX = gameSetting.getDouble(GameSettingKeys.SPAWN_X);
        spawnY = gameSetting.getDouble(GameSettingKeys.SPAWN_Y);
        spawnZ = gameSetting.getDouble(GameSettingKeys.SPAWN_Z);
    }

    public static Location getSpawnLocation() {
        return new Location(Bukkit.getWorld(spawnWorldName), spawnX, spawnY, spawnZ);
    }
}
