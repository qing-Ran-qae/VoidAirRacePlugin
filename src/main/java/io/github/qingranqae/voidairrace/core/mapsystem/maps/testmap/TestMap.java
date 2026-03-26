package io.github.qingranqae.voidairrace.core.mapsystem.maps.testmap;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.result.map.MapSelectedStartResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public class TestMap extends PlayableGameMap {
    @Override
    public @NonNull String getId() {
        return "TestMap";
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.text("TestMap");
    }

    @Override
    public @NonNull Component getDescription() {
        return Component.text("TestMap");
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public @NonNull MapSelectedStartResult selectedStart(Match match) {
        return null;
    }

    @Override
    public @Range(from = 2, to = Integer.MAX_VALUE) int maxTeams() {
        return 8;
    }
}
