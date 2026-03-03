package io.github.qingranqae.voidairrace.core.config;

import java.util.Map;

/**
 * 标志配置文件（flags.yml）中定义的配置键枚举。
 * 每个枚举常量对应一个配置项，包含其在文件中的路径和数据类型。
 */
public enum FlagsKey implements ConfigKey {
    /** 记录地图初始化状态的映射，键为地图ID，值为是否已初始化。 */
    MAP_INIT("mapInit", Map.class),

    /** 标记服务器启动时是否需要强制结束正在进行的比赛（例如因上次关闭时比赛未正常结束）。 */
    ON_SERVER_STARTED_STOP_MATCH("onServerStartedStopMatch", Boolean.class);

    /** 配置项在YAML中的路径。 */
    private final String path;

    /** 配置项的数据类型。 */
    private final Class<?> type;

    /**
     * 构造标志配置键。
     *
     * @param path 配置项路径
     * @param type 配置项数据类型
     */
    FlagsKey(String path, Class<?> type) {
        this.path = path;
        this.type = type;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}