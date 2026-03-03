package io.github.qingranqae.voidairrace.core.mapsystem.maps.smalltown;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.util.WorldCreatorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Range;

public class SmallTown implements PlayableGameMap {
    private static final String mapId = "SmallTown";
    private static final World mapWorld = WorldCreatorUtil.createVoidWorld(mapId);

    @Override
    public String getId() {
        return mapId;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("void_air_race.map.small_town.name");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("void_air_race.map.small_town.description.line1");
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void selectedStart(Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 小镇 开始比赛"));
    }

    @Override
    public void selectedOver(Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 小镇 结束比赛"));
    }

    @Override
    public @Range(from = 2, to = Integer.MAX_VALUE) int maxTeamsNumber() {
        return 0;
    }
}
