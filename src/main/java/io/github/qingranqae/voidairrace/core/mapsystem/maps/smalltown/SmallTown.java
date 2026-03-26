package io.github.qingranqae.voidairrace.core.mapsystem.maps.smalltown;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.result.map.MapSelectedStartResult;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public class SmallTown extends PlayableGameMap {
    private static final String mapId = "SmallTown";

    @Override
    public @NonNull String getId() {
        return mapId;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("void_air_race.map.small_town.name");
    }

    @Override
    public @NonNull Component getDescription() {
        return Component.translatable("void_air_race.map.small_town.description.line1");
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public @NonNull MapSelectedStartResult selectedStart(Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 小镇 开始比赛"));
        return MapSelectedStartResult.success();
    }

    @Override
    public void selectedOver(Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 小镇 结束比赛"));
    }

    @Override
    public @Range(from = 2, to = Integer.MAX_VALUE) int maxTeams() {
        return 0;
    }
}
