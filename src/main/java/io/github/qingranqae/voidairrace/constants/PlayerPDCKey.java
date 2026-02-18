package io.github.qingranqae.voidairrace.constants;

public enum PlayerPDCKey {
    MONEY("money");

    private String path;

    PlayerPDCKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
