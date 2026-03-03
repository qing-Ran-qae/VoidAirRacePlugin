package io.github.qingranqae.voidairrace.core.playerstatemanager.systems.play;

import io.github.qingranqae.voidairrace.core.playerstatemanager.DefaultState;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerState;
import org.bukkit.NamespacedKey;

public class Free implements PlayerState, DefaultState {
    private static final NamespacedKey id = PlayState.FREE.getValue();

    @Override
    public NamespacedKey getId() {
        return id;
    }
}
