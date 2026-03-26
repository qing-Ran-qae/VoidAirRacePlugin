package io.github.qingranqae.voidairrace.core.mapsystem.maps.grassland;

import io.github.qingranqae.voidairrace.service.config.files.ConfigFiles;
import io.github.qingranqae.voidairrace.service.config.files.ConfigKeys;

enum MapConfigFiles implements ConfigFiles {
    DATA("map_data/" + Const.MAP_ID + "/data", MapConfigKeys.values());

    MapConfigFiles(String fileName, ConfigKeys[] keys) {
        this.fileName = fileName;
        this.keys = keys;
    }

    private final String fileName;
    private final ConfigKeys[] keys;

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public ConfigKeys[] getKeys() {
        return keys;
    }
}
