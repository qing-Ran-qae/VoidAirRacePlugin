package io.github.qingranqae.voidairrace.core.playerstatemanager.systems;

public enum StateSystem {
    PLAY("play");

    private final String value;

    StateSystem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
