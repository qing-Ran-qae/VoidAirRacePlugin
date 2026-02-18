package io.github.qingranqae.voidairrace.corelayer.config;

public enum FlagsKey implements ConfigKey {
    MAP_INIT("mapInit");

    private final String path;

    FlagsKey(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
