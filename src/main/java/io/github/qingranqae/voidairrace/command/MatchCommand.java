package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.corelayer.config.Config;
import io.github.qingranqae.voidairrace.corelayer.config.ConfigFiles;
import io.github.qingranqae.voidairrace.corelayer.config.GameSettingKey;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.corelayer.mapsystem.GameMap;
import io.github.qingranqae.voidairrace.corelayer.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.corelayer.matchsystem.MatchConfigFactory;
import io.github.qingranqae.voidairrace.corelayer.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.exception.config.ConfigFieldInvalidException;
import io.github.qingranqae.voidairrace.exception.map.MapNotPlayableException;
import io.github.qingranqae.voidairrace.exception.match.InvalidMatchStateException;
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
                    .then(Commands.literal("start")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                try {
                                    MatchCoordinator.getInstance().startMatch(MatchConfigFactory.getInstance().createDefaultConfig());
                                    sender.sendMessage(Component.translatable("void_air_race.command.match.start.success"));
                                } catch (InvalidMatchStateException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.invalid_match_state_exception");
                                } catch (MapNotPlayableException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.map_not_playable_exception");
                                } catch (ConfigFieldInvalidException e) {
                                    ArrayList<ComponentLike> args = new ArrayList();
                                    args.add(Component.text(e.getFieldPath()));
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.config_field_invalid_exception", args);
                                }
                                return 1;
                            }))
                    .then(Commands.literal("stop")
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
                            }))
                    .then(Commands.literal("set_map")
                            .then(Commands.argument("map_id", StringArgumentType.string())
                                    .executes(ctx -> {
                                        String newMapId = ctx.getArgument("map_id", String.class);
                                        CommandSender sender = ctx.getSource().getSender();
                                        GameMap targetMap;
                                        try {
                                            targetMap = targetMap = MapRegistry.getInstance().getMapById(newMapId);
                                        } catch (MapNotPlayableException e) {
                                            sender.sendMessage(
                                                    Component.translatable("void_air_race.command.match.set_map.map_notfound")
                                                            .arguments(Component.text(newMapId))
                                                            .color(NamedTextColor.RED)
                                            );
                                            return 1;
                                        }

                                        // 修改值
                                        Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS).set(GameSettingKey.SELECTED_MAP_ID.getPath(), newMapId);

                                        // 提示
                                        sender.sendMessage(
                                                Component.translatable("void_air_race.command.match.set_map.success")
                                                        .arguments(Component.text(newMapId), targetMap.getDisplayName())
                                        );
                                        return 1;
                                    })
                            )

                    )
                    .then(Commands.literal("get_map")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                String selectedMapId = Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS).getString(GameSettingKey.SELECTED_MAP_ID.getPath());
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
        startCommandSendFailure(sender, translationKey, new ArrayList<>());
    }

    private static void startCommandSendFailure(CommandSender sender, String translationKey, List<ComponentLike> args) {
        sender.sendMessage(
                Component.translatable(translationKey)
                        .arguments(args)
                        .color(NamedTextColor.RED)
        );
    }
}
