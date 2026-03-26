package io.github.qingranqae.voidairrace.core.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.core.mapsystem.GameMap;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import javax.lang.model.type.NullType;
import java.util.concurrent.CompletableFuture;

public class Lobby extends GameMap implements Listener {
    @Override
    public @NonNull String getId() {
        return Const.getMapId();
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("void_air_race.map.lobby.name");
    }

    @Override
    public @NonNull Component getDescription() {
        return Component.translatable("void_air_race.map.lobby.description.line1");
    }

    @Override
    public @NonNull CompletableFuture<NullType> initAsync(JavaPlugin mainClass) {
        Renderer.reRenderAll();
        return CompletableFuture.completedFuture(null);
    }
}