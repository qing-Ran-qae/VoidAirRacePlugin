package io.github.hhn756.voidairrace.core.playerstatemanager.systems;

/**
 * 记录所有状态体系的名称
 * */
public enum StateSystem {
    PLAY("play");

    private final String value;

    StateSystem(String value) {
        this.value = value;
    }

    /**
     * 获取字符串形式的状态体系名称
     * */
    public String getValue() {
        return value;
    }
}
