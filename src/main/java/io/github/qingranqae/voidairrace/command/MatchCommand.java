package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.core.config.ConfigFiles;
import io.github.qingranqae.voidairrace.core.config.GameSettingKey;
import io.github.qingranqae.voidairrace.core.mapsystem.GameMap;
import io.github.qingranqae.voidairrace.core.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchConfig;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchConfigFactory;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.exception.ConfigFieldInvalidException;
import io.github.qingranqae.voidairrace.exception.InvalidMatchStateException;
import io.github.qingranqae.voidairrace.exception.MapNotPlayableException;
import io.github.qingranqae.voidairrace.exception.NoContestantsException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class MatchCommand implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        event.getMainClass().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("match")
                    .then(
                            Commands.literal("start")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                try {
                                    MatchConfig config = MatchConfigFactory.getInstance().createDefaultConfig();
                                    if (config.contestants().size() < 2) {
                                        throw new NoContestantsException("至少需要 2 名玩家才能开始比赛");
                                    }

                                    MatchCoordinator.getInstance().startMatch(config);
                                    sender.sendMessage(Component.translatable("void_air_race.command.match.start.success"));
                                } catch (InvalidMatchStateException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.invalid_match_state_exception");
                                } catch (MapNotPlayableException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.map_not_playable_exception");
                                } catch (ConfigFieldInvalidException e) {
                                    ArrayList<ComponentLike> args = new ArrayList<>();
                                    args.add(Component.text(e.getFieldPath()));
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.config_field_invalid_exception", args);
                                } catch (NoContestantsException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.no_contestants_exception");
                                }
                                return 1;
                            })
                    )
                    .then(
                            Commands.literal("stop")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                try {
                                    MatchCoordinator.getInstance().stopMatch(false);
                                    sender.sendMessage(Component.translatable("void_air_race.command.match.stop.success"));
                                } catch (InvalidMatchStateException e) {
                                    sender.sendMessage(
                                            Component.translatable("void_air_race.command.match.stop.failure.invalid_match_state_exception")
                                                    .color(NamedTextColor.RED)
                                    );
                                }
                                return 1;
                            })
                    )
                    .then(
                            Commands.literal("set_map")
                            .then(Commands.argument("map_id", StringArgumentType.string())
                                    .executes(ctx -> {
                                        String newMapId = ctx.getArgument("map_id", String.class);
                                        CommandSender sender = ctx.getSource().getSender();

                                        // 检查地图是否存在
                                        if (!MapRegistry.getInstance().containsMap(newMapId)) {
                                            sender.sendMessage(
                                                    Component.translatable("void_air_race.command.match.set_map.map_notfound")
                                                            .arguments(Component.text(newMapId))
                                                            .color(NamedTextColor.RED)
                                            );
                                            return 1;
                                        }

                                        // 检查地图是否可玩
                                        GameMap targetMap = null;
                                        try {
                                            targetMap = MapRegistry.getInstance().getMapById(newMapId);
                                        } catch (IllegalArgumentException ignored) {
                                            // 前面已经检查过地图是否存在了，所以这里理论上不会抛出这个异常
                                        }
                                        if (!(targetMap instanceof PlayableGameMap)) {
                                            sender.sendMessage(
                                                    Component.translatable("void_air_race.command.match.set_map.map_not_playable")
                                                            .arguments(Component.text(newMapId))
                                                            .color(NamedTextColor.RED)
                                            );
                                            return 1;
                                        }

                                        // 修改值
                                        Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS).set(GameSettingKey.SELECTED_MAP_ID, newMapId);

                                        // 提示
                                        sender.sendMessage(
                                                Component.translatable("void_air_race.command.match.set_map.success")
                                                        .arguments(Component.text(newMapId), targetMap.getDisplayName())
                                        );
                                        return 1;
                                    })
                            )

                    )
                    .then(
                            Commands.literal("get_map")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                String selectedMapId = Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS).getString(GameSettingKey.SELECTED_MAP_ID);
                                GameMap selectedMap;
                                try {
                                    selectedMap = MapRegistry.getInstance().getMapById(selectedMapId);
                                } catch (MapNotPlayableException e) {
                                    sender.sendMessage(
                                            Component.translatable("void_air_race.command.match.get_map.map_notfound")
                                                    .arguments(Component.text(selectedMapId == null ? "null" : selectedMapId))
                                                    .color(NamedTextColor.RED)
                                    );
                                    return 1;
                                }

                                sender.sendMessage(
                                        Component.translatable("void_air_race.command.match.get_map.success")
                                                .arguments(Component.text(selectedMapId == null ? "null" : selectedMapId), selectedMap.getDisplayName())
                                );
                                return 1;
                            })
                    )
                    .build();
            commandsEvent.registrar().register(node);
        });
    }

    private static void startCommandSendFailure(CommandSender sender, String translationKey) {
        sender.sendMessage(
                Component.translatable(translationKey)
                        .color(NamedTextColor.RED)
        );
    }

    private static void startCommandSendFailure(CommandSender sender, String translationKey, List<ComponentLike> args) {
        sender.sendMessage(
                Component.translatable(translationKey)
                        .arguments(args)
                        .color(NamedTextColor.RED)
        );
    }
}
