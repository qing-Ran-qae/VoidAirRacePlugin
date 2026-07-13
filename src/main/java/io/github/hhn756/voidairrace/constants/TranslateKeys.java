package io.github.hhn756.voidairrace.constants;

/**
 * 插件中的所有文本组件翻译键
 * */
public class TranslateKeys {
    private TranslateKeys() {}

    /**
     * 比赛、比赛配置和协调器的分类
     * */
    public static class Match {
        public static class Create {
            public static final String CONFIG_IS_USED = Namespace.str + ".match.create.config_is_used";
        }

        public static class Start {
            public static final String START_FAILED_UNKNOWN_REASONS = Namespace.str + ".match.start.start_failed_unknown_reasons";
            public static final String START_FAILED_SPECIFIED_REASONS = Namespace.str + ".match.start.start_failed_specified_reasons";
            public static final String INSTANCE_IS_USED = Namespace.str + ".match.start.instance_is_used";
            public static final String INVALID_STATE = Namespace.str + ".match.start.invalid_state";
            public static final String INSTALL_FAILED = Namespace.str + ".match.start.install_failed";
        }

        public static class Stop {
            public static final String INVALID_STATE = Namespace.str + ".match.stop.invalid_state";
        }

        public static class MatchConfig {
            public static class CreateConfig {
                public static final String FAILURE_UNKNOWN_CAUSE = Namespace.str + ".match.match_config.create_config.failure_unknown_cause";
                public static final String FAILURE_SPECIFIED_CAUSE = Namespace.str + ".match.match_config.create_config.failure_specified_cause";
            }

            public static class CreateDefaultConfig {
                public static final String FAILURE_UNKNOWN_CAUSE = Namespace.str + ".match.match_config.create_default_config.failure_unknown_cause";
                public static final String FAILURE_SPECIFIED_CAUSE = Namespace.str + ".match.match_config.create_default_config.failure_specified_cause";
            }

            public static class Init {
                public static final String FAILURE_UNKNOWN_CAUSE = Namespace.str + ".match.match_config.init.failure_unknown_cause";
                public static final String FAILURE_SPECIFIED_CAUSE = Namespace.str + ".match.match_config.init.failure_specified_cause";
            }

            public static class LoadCustomConfig {
                public static final String FAILURE_UNKNOWN_CAUSE = Namespace.str + ".match.match_config.load_custom_config.failure_unknown_cause";
                public static final String FAILURE_SPECIFIED_CAUSE = Namespace.str + ".match.match_config.load_custom_config.failure_specified_cause";
            }

            public static class LoadDefaultConfig {
                public static final String FAILURE_UNKNOWN_CAUSE = Namespace.str + ".match.match_config.load_default_config.failure_unknown_cause";
                public static final String FAILURE_SPECIFIED_CAUSE = Namespace.str + ".match.match_config.load_default_config.failure_specified_cause";
            }
        }

        public static class MatchCoordinator {
            public static class StartMatch {
                public static final String FAILURE_UNKNOWN_CAUSE = Namespace.str + ".match.match_coordinator.start_match.failure_unknown_cause";
                public static final String FAILURE_SPECIFIED_CAUSE = Namespace.str + ".match.match_coordinator.start_match.failure_specified_cause";
                public static final String REPEAT_START = Namespace.str + ".match.match_coordinator.repeat_start";
            }

            public static class StopMatch {
                public static final String INVALID_MATCH_STATE = Namespace.str + ".match.match_coordinator.stop_match.invalid_match_state";
            }
        }
    }

    /**
     * 比赛组件的分类<br>
     * 如果一个组件是某个模块为了实现功能而创建的，那它应在上级模块的类别下
     * */
    public static class MatchComp {
        /**
         * 所有组件的基类
         * */
        public static class CompBase {
            public static final String DEFAULT_NAME = Namespace.str + ".match_comp.comp_base.default_name";
        }

        public static class BasicEndDetermination {
            public static final String NAME = Namespace.str + ".match_comp.basic_end_determination.name";
            public static final String AUTHOR = Namespace.str + ".match_comp.basic_end_determination.author";

        }
    }

    public static class Map {
        public static class CreateCustomConfig {
            public static final String MAP_NOT_PLAYABLE = Namespace.str + ".map.create_custom_config.map_not_playable";
        }

        public static class CreateDefaultConfig {
            public static final String MAP_NOTFOUND = Namespace.str + ".map.create_default_config.map_notfound";
            public static final String MAP_NOT_PLAYABLE = Namespace.str + ".map.create_default_config.map_not_playable";
        }

        public static class MapComponent {
            public static final String SELECTED_START_FAILED = Namespace.str + ".map.map_component.selected_start_failed";
        }

        public static class GrassLand {
            public static final String NAME = Namespace.str + ".map.grass_land.names";
            public static final String DESCRIPTION_LINE1 = Namespace.str + ".map.grass_land.description_line1";
            public static final String AUTHOR1 = Namespace.str + ".map.grass_land.author1";
            public static final String DISPLAY_VERSION = Namespace.str + ".map.grass_land.display_version";

            public static class SelectedStart {
                public static final String FAILURE_UNKNOWN_CAUSE = Namespace.str + ".map.grass_land.selected_start.failure_unknown_cause";
                public static final String FAILURE_SPECIFIED_CAUSE = Namespace.str + ".map.grass_land.selected_start.failure_specified_cause";
            }

            public static class FindSupplyBoxes {
                public static final String LOAD_ARENA_FAILURE = Namespace.str + ".map.grass_land.find_supply_boxes.load_arena_failure";
            }
        }

        public static class Lobby {
            public static final String NAME = Namespace.str + ".map.lobby.names";
            public static final String DESCRIPTION_LINE1 = Namespace.str + ".map.lobby.description_line1";
            public static final String AUTHOR1 = Namespace.str + ".map.lobby.author1";
            public static final String DISPLAY_VERSION = Namespace.str + ".map.lobby.display_version";
        }

        public static class SmallTown {
            public static final String NAME = Namespace.str + ".map.small_town.names";
            public static final String DESCRIPTION_LINE1 = Namespace.str + ".map.small_town.description_line1";
            public static final String AUTHOR1 = Namespace.str + ".map.small_town.author1";
            public static final String DISPLAY_VERSION = Namespace.str + ".map.small_town.display_version";
        }
    }

    public static class MatchRule {
        public static class BasicEndDetermination {
            public static final String DISPLAY_NAME = Namespace.str + ".match_rule.basic_end_determination.display_name";
            public static final String DESCRIPTION = Namespace.str + ".match_rule.basic_end_determination.description";
            public static final String DISPLAY_VERSION =  Namespace.str + ".match_rule.basic_end_determination.display_version";
        }
    }

    public static class Arena {
        public static class ArenaManager {
            public static final String NO_FREE_ARENA = Namespace.str + ".arena.arena_manager.no_free_arena";
            public static final String TOKEN_IS_INVALID = Namespace.str + ".arena.arena_manager.token_is_invalid";
            public static final String IO_EXCEPTION = Namespace.str + ".arena.arena_manager.io_exception";
        }
    }

    public static class Team {
        // 队伍翻译键在 Teams 枚举中通过拼接生成，此处暂不定义
    }

    public static class Command {
        public static class GameMapCmd {
            public static class List {
                public static final String START = Namespace.str + ".command.game_map.list.start";
                public static final String PLAYABLE_MAP_INFO = Namespace.str + ".command.game_map.list.playable_map_info";
                public static final String NOT_PLAYABLE_MAP_INFO = Namespace.str + ".command.game_map.list.not_playable_map_info";
                public static final String END = Namespace.str + ".command.game_map.list.end";
            }

            public static class Reinit {
                public static final String MAP_NOT_FOUND = Namespace.str + ".command.game_map.reinit.map_not_found";
                public static final String STARTED = Namespace.str + ".command.game_map.reinit.started";
                public static final String OK = Namespace.str + ".command.game_map.reinit.ok";
                public static final String ID_FORMAT_ERROR = Namespace.str + ".command.game_map.reinit.id_format_error";
            }
        }

        public static class MatchCmd {
            public static class Stop {
                public static final String FAILURE = Namespace.str + ".command.match_cmd.stop.failure";
                public static final String SUCCESS = Namespace.str + ".command.match_cmd.stop.success";
            }

            public static class SetMap {
                public static final String MAP_NOTFOUND = Namespace.str + ".command.match_cmd.set_map.map_notfound";
                public static final String MAP_NOT_PLAYABLE = Namespace.str + ".command.match_cmd.set_map.map_not_playable";
                public static final String SUCCESS = Namespace.str + ".command.match_cmd.set_map.success";
                public static final String ID_FORMAT_ERROR = Namespace.str + ".command.match_cmd.set_map.id_format_error";
            }

            public static class GetMap {
                public static final String SUCCESS = Namespace.str + ".command.match_cmd.get_map.success";
                public static final String DEFAULT_MAP_NAME = Namespace.str + ".command.match_cmd.get_map.default_map_name";
            }
        }

        public static class PlayerManagerCmd {
            public static class Init {
                public static class Get {
                    public static final String NULL_PLAYER = Namespace.str + ".command.player_manager.init.get.null_player";
                    public static final String YES = Namespace.str + ".command.player_manager.init.get.yes";
                    public static final String NO = Namespace.str + ".command.player_manager.init.get.no";
                }

                public static class Reinit {
                    public static final String NULL_PLAYER = Namespace.str + ".command.player_manager.init.reinit.null_player";
                    public static final String OK = Namespace.str + ".command.player_manager.init.reinit.ok";
                }
            }
        }
    }

    public static class ComponentRegistry {
        public static class RegisterComponent {
            public static final String FAILURE = Namespace.str + ".component_registry.register_component.failure";
        }

        public static class NewComponent {
            public static final String FAILURE_NOT_FOUND = Namespace.str + ".component_registry.new_component.failure_not_found";
            public static final String FAILURE_INSTANTIATION_FAILED = Namespace.str + ".component_registry.new_component.failure_instantiation_failed";
        }
    }

    public static class Rule {
        public static class RuleComp {
            public static final String ALREADY_ENABLED = Namespace.str + ".rule.already_enabled";
        }

        public static class Registry {
            public static final String ID_NOT_FOUND = Namespace.str + ".rule.registry.id_not_found";
            public static final String ID_MISMATCH = Namespace.str + ".rule.registry.id_mismatch";
            public static final String INSTANTIATION_FAILED = Namespace.str + ".rule.registry.instantiation_failed";
        }
    }

    public static class Custom {
        public static class GameElementMeta {
            public static final String DEFAULT_ELEMENT_NAME = Namespace.str + ".custom.default_element_name";
        }
    }

    public static class AudioVisualServices {
        public static class GameTimeBossbar {
            public static final String BOSSBAR_NAME = Namespace.str + ".audio_visual_services.game_time_bossbar.bossbar_name";
        }

        public static class LoginMessage {
            public static final String LINE1 = Namespace.str + ".audio_visual_services.login_message.line1";
            public static final String LINE1_SELF = Namespace.str + ".audio_visual_services.login_message.line1_self";
        }

        public static class MatchMessage {
            public static class MatchStarted {
                public static final String LINE1 = Namespace.str + ".audio_visual_services.match_message.match_started.line1";
                public static final String LINE2 = Namespace.str + ".audio_visual_services.match_message.match_started.line2";
                public static final String LINE3 = Namespace.str + ".audio_visual_services.match_message.match_started.line3";
                public static final String LINE4 = Namespace.str + ".audio_visual_services.match_message.match_started.line4";
            }

            public static class MatchOver {
                public static final String LINE1 = Namespace.str + ".audio_visual_services.match_message.match_over.line1";
            }
        }
    }

    public static class BaseComponents {
        public static class ContestantComp {
            public static final String NO_CONFIG = Namespace.str + ".base_components.contestant_comp.no_config";
        }

        public static class GameTimeComp {
            public static final String INVALID_DURATION = Namespace.str + ".base_components.game_time_comp.invalid_duration";
            public static final String NO_CONFIG = Namespace.str + ".base_components.game_time_comp.no_config";
        }
    }

    public static class Config {
        public static class Save {
            public static final String CANT_SAVE = Namespace.str + ".config.save.cant_save";
        }

        public static class SaveAtomic {
            public static final String CANT_SAVE = Namespace.str + ".config.save_atomic.cant_save";
        }

        public static class GetYmlConfig {
            public static final String CANT_CREATE_DIR = Namespace.str + ".config.get_yml_config.cant_create_dir";
            public static final String CANT_CREATE_EMPTY_CONFIG = Namespace.str + ".config.get_yml_config.cant_create_empty_config";
            public static final String FILE_CANT_READ = Namespace.str + ".config.get_yml_config.file_cant_read";
            public static final String CANT_LOAD = Namespace.str + ".config.get_yml_config.cant_load";
        }
    }
}