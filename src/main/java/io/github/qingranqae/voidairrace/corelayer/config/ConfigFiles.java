package io.github.qingranqae.voidairrace.corelayer.config;

public enum ConfigFiles {
    GAME_SETTINGS("gameSetting"),
    FLAGS("flags");

    private final String fileName;

    ConfigFiles(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
