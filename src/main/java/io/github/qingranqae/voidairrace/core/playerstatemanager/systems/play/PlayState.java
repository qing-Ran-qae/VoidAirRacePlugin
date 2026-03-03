package io.github.qingranqae.voidairrace.core.playerstatemanager.systems.play;

import io.github.qingranqae.voidairrace.core.playerstatemanager.systems.StateSystem;
import org.bukkit.NamespacedKey;

public enum PlayState {
    FREE(new NamespacedKey(StateSystem.PLAY.getValue(), "free")),
    PLAYING(new NamespacedKey(StateSystem.PLAY.getValue(), "playing")),;

    private final NamespacedKey value;

    PlayState(NamespacedKey value) {
        this.value = value;
    }

    public NamespacedKey getValue() {
        return value;
    }
}
