package io.github.hhn756.voidairrace.service.config.files;

import io.github.hhn756.voidairrace.service.config.ConfigKey;
import org.bukkit.Location;

public class GlobalSettingKeys {
    /** 世界/大厅重生点位置 */
    public static final ConfigKey<Location> SPAWN_LOCATION = new ConfigKey<>("spawn_location"){};

    public static final ConfigKey<?>[] ALL_KEYS = {
            SPAWN_LOCATION
    };
}
