package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.constants.PermissionNode;
import io.github.qingranqae.voidairrace.core.mapsystem.GameMap;
import io.github.qingranqae.voidairrace.core.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchConfig;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchConfigFactory;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.core.result.match.ConfigCreationResult;
import io.github.qingranqae.voidairrace.core.result.match.CoordinatorStartMatchResult;
import io.github.qingranqae.voidairrace.core.result.match.CoordinatorStopMatchResult;
import io.github.qingranqae.voidairrace.infrastructure.BootstrapModule;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.files.GameSettingKeys;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

@AutoRegistration
public class MatchCommand implements BootstrapModule {
    public void onBootstrap(@NonNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("match")
                    .requires(ctx -> ctx.getSender().hasPermission(PermissionNode.MATCH_COMMAND.getValue()))
                    .then(Commands.literal("start")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();

                                ConfigCreationResult def_config = MatchConfigFactory.getInstance().createDefaultConfig();
                                MatchConfig config = def_config.getValue();
                                if (!def_config.isSuccess() || config == null) {
                                    Component message = def_config.getDisplayMessage();
                                    if (message == null) message = Component.translatable("void_air_race.command.match.start.failure.create_match_config_failure");
                                    sender.sendMessage(message.color(NamedTextColor.RED));
                                    return 1;
                                }
                                if (config.contestants().size() < 2) {
                                    sender.sendMessage(
                                            Component.translatable("void_air_race.command.match.start.failure.no_contestants")
                                                    .color(NamedTextColor.RED));
                                    return 1;
                                }

                                CoordinatorStartMatchResult startMatchResult = MatchCoordinator.getInstance().startMatch(config);
                                if (!startMatchResult.isSuccess()) {
                                    sender.sendMessage(
                                            Component.translatable("void_air_race.match.match_coordinator.start_match.invalid_match_state"));
                                    return 1;
                                }

                                sender.sendMessage(Component.translatable("void_air_race.command.match.start.success"));
                                return 1;
                            })
                    ).then(Commands.literal("stop")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();

                                CoordinatorStopMatchResult stopResult = MatchCoordinator.getInstance().stopMatch(false);
                                if (!stopResult.isSuccess()) {
                                    Component message = stopResult.getDisplayMessage();
                                    if (message == null) message = Component.translatable("void_air_race.command.match.stop.failure");
                                    sender.sendMessage(message.color(NamedTextColor.RED));
                                    return 1;
                                }

                                sender.sendMessage(Component.translatable("void_air_race.command.match.stop.success"));
                                return 1;
                            })
                    ).then(Commands.literal("set_map")
                            .then(Commands.argument("map_id", StringArgumentType.string())
                                    .executes(ctx -> {
                                        CommandSender sender = ctx.getSource().getSender();
                                        String newMapId = ctx.getArgument("map_id", String.class);

                                        // 检查地图是否存在
                                        if (!MapRegistry.getInstance().containsMap(newMapId)) {
                                            sender.sendMessage(Component.translatable("void_air_race.command.match.set_map.map_notfound")
                                                    .arguments(Component.text(newMapId))
                                                    .color(NamedTextColor.RED));
                                            return 1;
                                        }

                                        // 检查地图是否可玩
                                        GameMap targetMap = MapRegistry.getInstance().getMapById(newMapId);
                                        if (!(targetMap instanceof PlayableGameMap)) {
                                            sender.sendMessage(Component.translatable("void_air_race.command.match.set_map.map_not_playable")
                                                    .arguments(Component.text(newMapId))
                                                    .color(NamedTextColor.RED));
                                            return 1;
                                        }

                                        // 修改值
                                        Config.getInstance().getConfig(PublicFiles.GAME_SETTINGS).set(GameSettingKeys.SELECTED_MAP_ID, newMapId);

                                        // 提示
                                        sender.sendMessage(Component.translatable("void_air_race.command.match.set_map.success")
                                                .arguments(Component.text(newMapId), targetMap.getDisplayName()));
                                        return 1;
                                    })
                            )
                    )
                    .then(Commands.literal("get_map")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                String selectedMapId = Config.getInstance().getConfig(PublicFiles.GAME_SETTINGS).getString(GameSettingKeys.SELECTED_MAP_ID);

                                GameMap selectedMap = MapRegistry.getInstance().getMapById(selectedMapId);
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
}
