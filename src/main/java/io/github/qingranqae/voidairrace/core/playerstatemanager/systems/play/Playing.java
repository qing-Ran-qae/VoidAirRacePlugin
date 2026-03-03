package io.github.qingranqae.voidairrace.core.playerstatemanager.systems.play;

import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerState;
import org.bukkit.NamespacedKey;

public class Playing implements PlayerState {
    private static final NamespacedKey id = PlayState.PLAYING.getValue();

    @Override
    public NamespacedKey getId() {
        return id;
    }
}
