package io.github.qingranqae.voidairrace.constants;

public enum ResourcePath {
    ARENA("arena"),
    DATAPACK("datapack");

    private final String path;

    ResourcePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
        return path.endsWith("/");
    }

    public boolean isFile() {
        return !isDirectory();
    }
}
