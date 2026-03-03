package io.github.qingranqae.voidairrace.core.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.core.mapsystem.GameMap;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;

public class Lobby implements GameMap, Listener {
    @Override
    public String getId() {
        return Const.getMapId();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("void_air_race.map.lobby.name");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("void_air_race.map.lobby.description.line1");
    }

    @Override
    public void init() {
        Renderer.reRenderAll();
    }
}
