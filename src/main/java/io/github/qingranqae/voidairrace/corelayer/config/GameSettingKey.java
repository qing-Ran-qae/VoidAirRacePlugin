package io.github.qingranqae.voidairrace.corelayer.config;

public enum GameSettingKey implements ConfigKey {
    SELECTED_MAP_ID("selectedMapId"),
    MATCH_DURATION("matchDuration");

    private final String path;

    GameSettingKey(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
