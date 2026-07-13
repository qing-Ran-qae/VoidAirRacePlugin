package io.github.hhn756.voidairrace.core.playerstatemanager;

/**
 * 状态体系的元数据，由状态注册表自动实例化
 * */
public class StateSystemMeta {
    private final String id;
    private final String defaultState;

    /**
     * 仅状态注册表可实例化
     *
     * @param id 该对象所代表的体系的id
     * @param defaultState 该对象所代表的体系中的默认状态
     * */
    StateSystemMeta(String id, String defaultState) {
        this.id = id;
        this.defaultState = defaultState;
    }

    /**
     * 获取该对象所代表的体系的id
     * */
    public String getId() {
        return id;
    }

    /**
     * 获取该对象所代表的体系中的默认状态
     * */
    public String getDefaultState() {
        return defaultState;
    }
}
