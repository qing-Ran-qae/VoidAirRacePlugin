package io.github.qingranqae.voidairrace.constants;

import org.bukkit.NamespacedKey;

import java.util.Map;

public enum PlayerPDCKey {
    MONEY(new NamespacedKey("void_air_race", "money"), Integer.class),
    STATES(new NamespacedKey("void_air_race", "states"), Map.class),
    INITIALIZED(new NamespacedKey("void_air_race", "initialized"), Boolean.class),;

    private final NamespacedKey value;
    private final Class<?> type;

    PlayerPDCKey(NamespacedKey path, Class<?> type) {
        this.value = path;
        this.type = type;
    }

    public NamespacedKey getValue() {
        return value;
    }

    public Class<?> getType() {
        return type;
    }
}
