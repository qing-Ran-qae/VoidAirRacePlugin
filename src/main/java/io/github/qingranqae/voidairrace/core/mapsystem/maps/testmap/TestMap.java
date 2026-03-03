package io.github.qingranqae.voidairrace.core.mapsystem.maps.testmap;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Range;

public class TestMap implements PlayableGameMap {
    @Override
    public String getId() {
        return "TestMap";
    }

    @Override
    public Component getDisplayName() {
        return Component.text("TestMap");
    }

    @Override
    public Component getDescription() {
        return Component.text("TestMap");
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void selectedStart(Match match) {}

    @Override
    public void selectedOver(Match match) {}

    @Override
    public @Range(from = 2, to = Integer.MAX_VALUE) int maxTeamsNumber() {
        return 8;
    }
}
