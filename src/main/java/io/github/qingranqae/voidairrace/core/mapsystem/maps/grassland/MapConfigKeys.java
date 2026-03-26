package io.github.qingranqae.voidairrace.core.mapsystem.maps.grassland;

import io.github.qingranqae.voidairrace.service.config.files.ConfigKeys;

import java.util.List;

enum MapConfigKeys implements ConfigKeys {
    SUPPLY_CHESTS("supply_chests", List.class),;

    private final String path;
    private final Class<?> type;

    MapConfigKeys(String path, Class<?> type) {
        this.path = path;
        this.type = type;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
