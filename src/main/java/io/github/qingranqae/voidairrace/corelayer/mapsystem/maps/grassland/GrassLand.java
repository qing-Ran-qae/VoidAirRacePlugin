package io.github.qingranqae.voidairrace.corelayer.mapsystem.maps.grassland;

import io.github.qingranqae.voidairrace.corelayer.mapsystem.GameMap;
import io.github.qingranqae.voidairrace.corelayer.matchsystem.Match;
import io.github.qingranqae.voidairrace.util.WorldCreatorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class GrassLand implements GameMap {

    private static final String ID = "GrassLand";
    private static final World mapWorld = WorldCreatorUtil.createVoidWorld(ID);

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("void_air_race.mapregistry.maps.grassland.name");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("void_air_race.mapregistry.maps.grassland.description.line1");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public void selectedStart(Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 草原 开始比赛"));
        // 玩家进场
    }

    @Override
    public void selectedOver(Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 草原 结束比赛"));
    }
}
