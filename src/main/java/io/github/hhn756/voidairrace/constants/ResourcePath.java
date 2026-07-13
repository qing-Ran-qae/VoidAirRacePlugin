package io.github.hhn756.voidairrace.constants;

/**
 * 插件中资源文件的路径，不包含后缀
 * */
public enum ResourcePath {
    DATAPACK("/datapack/");

    private final String path;

    ResourcePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
