package io.github.hhn756.voidairrace.constants;

/**
 * 插件增加的权限节点
 * */
public enum PermissionNode {
    /** 比赛管理命令的使用权限 */
    MATCH_COMMAND("void_air_race.command.match"),

    /** 地图管理命令的使用权限 */
    GAME_MAP_COMMAND("void_air_race.command.game_map"),

    /** 玩家管理命令的使用权限 */
    PLAYER_MANAGER_COMMAND("void_air_race.command.player_manager"),

    /** debug命令的使用权限 */
    DEBUG_COMMAND("void_air_race.command.debug");

    private final String value;

    PermissionNode(String value) {
        this.value = value;
    }

    /**
     * 获取字符串形式的权限节点名
     * */
    public String getValue() {
        return value;
    }
}
