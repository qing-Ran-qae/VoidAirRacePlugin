package io.github.qingranqae.voidairrace.corelayer.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.corelayer.mapsystem.GameMap;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;

public class Lobby implements GameMap, Listener {
    @Override
    public String getId() {
        return Const.getMapId();
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
