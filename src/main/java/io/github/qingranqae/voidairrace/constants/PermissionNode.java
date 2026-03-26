package io.github.qingranqae.voidairrace.constants;

public enum PermissionNode {
    MATCH_COMMAND("void_air_race.command.match"),
    GAME_MAP_COMMAND("void_air_race.command.game_map"),
    PLAYER_MANAGER_COMMAND("void_air_race.command.player_manager"),
    DEBUG_COMMAND("void_air_race.command.debug");

    private final String value;


    PermissionNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
