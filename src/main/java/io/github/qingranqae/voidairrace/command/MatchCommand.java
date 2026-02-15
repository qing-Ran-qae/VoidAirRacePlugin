package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.exception.MapNotPlayableException;
import io.github.qingranqae.voidairrace.match.MatchConfig;
import io.github.qingranqae.voidairrace.pluginevent.PluginEnableEvent;
import io.github.qingranqae.voidairrace.mapregistry.GameMap;
import io.github.qingranqae.voidairrace.mapregistry.MapRegistry;
import io.github.qingranqae.voidairrace.match.MatchCoordinator;
import io.github.qingranqae.voidairrace.config.Config;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;

public class MatchCommand implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        event.getMainClass().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("match")
                    .then(Commands.literal("start")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                try {
                                    MatchCoordinator.getInstance().startMatch(MatchConfig.createDefaultConfig());
                                    sender.sendMessage(Component.translatable("void_air_race.command.match.start.success"));
                                } catch (IllegalStateException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.illegal_state_exception");
                                } catch (MapNotPlayableException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.map_not_playable_exception");
                                } catch (InvocationTargetException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.no_such_method_exception");
                                } catch (NoSuchMethodException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.instantiation_exception");
                                } catch (InstantiationException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.illegal_access_exception");
                                } catch (IllegalAccessException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.invocation_target_exception");
                                } catch (NullPointerException e) {
                                    startCommandSendFailure(sender, "void_air_race.command.match.start.failure.null_pointer_exception");
                                }
                                return 1;
                            }))
                    .then(Commands.literal("stop")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                try {
                                    MatchCoordinator.getInstance().stopMatch(false);
                                    sender.sendMessage(Component.translatable("void_air_race.command.match.stop.success"));
                                } catch (IllegalStateException e) {
                                    sender.sendMessage(
                                            Component.translatable("void_air_race.command.match.stop.failure")
                                                    .arguments(Component.text(e.getMessage()))
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
                                        GameMap targetMap = null;
                                        try {
                                            targetMap = MapRegistry.getMapById(newMapId).getDeclaredConstructor().newInstance();
                                        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NullPointerException ignored) {}

                                        if (targetMap == null) {
                                            sender.sendMessage(
                                                    Component.translatable("void_air_race.command.match.set_map.map_notfound")
                                                            .arguments(Component.text(newMapId))
                                                            .color(NamedTextColor.RED)
                                            );
                                            return 1;
                                        }

                                        // 修改值
                                        Config.getInstance().setSelectedMapId(newMapId);

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
                                String selectedMapId = Config.getInstance().getSelectedMapId();
                                GameMap selectedMap = null;
                                try {
                                    selectedMap = MapRegistry.getMapById(selectedMapId).getDeclaredConstructor().newInstance();
                                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NullPointerException ignored) {}

                                if (selectedMap == null) {
                                    sender.sendMessage(
                                            Component.translatable("void_air_race.command.match.get_map.map_notfound")
                                                    .arguments(Component.text(selectedMapId))
                                                    .color(NamedTextColor.RED)
                                    );
                                    return 1;
                                }

                                sender.sendMessage(
                                        Component.translatable("void_air_race.command.match.get_map.success")
                                                .arguments(Component.text(selectedMapId), selectedMap.getDisplayName())
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
}
