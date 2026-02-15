package io.github.qingranqae.voidairrace.mapregistry.maps;

import io.github.qingranqae.voidairrace.mapregistry.GameMap;
import io.github.qingranqae.voidairrace.match.Match;
import net.kyori.adventure.text.Component;

public class Lobby implements GameMap {
    @Override
    public String getId() {
        return "Lobby";
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("void_air_race.mapregistry.maps.lobby.name");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("void_air_race.mapregistry.maps.lobby.description.line1");
    }

    @Override
    public boolean isPlayable() {
        return false;
    }
}
