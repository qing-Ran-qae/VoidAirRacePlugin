package io.github.hhn756.voidairrace.playerinteraction.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.hhn756.voidairrace.constants.PermissionNode;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.map.GameMap;
import io.github.hhn756.voidairrace.core.map.MapRegistry;
import io.github.hhn756.voidairrace.core.map.PlayableGameMap;
import io.github.hhn756.voidairrace.core.match.MatchCoordinator;
import io.github.hhn756.voidairrace.infrastructure.BootstrapModule;
import io.github.hhn756.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.hhn756.voidairrace.service.config.Config;
import io.github.hhn756.voidairrace.service.config.files.GameSettingKeys;
import io.github.hhn756.voidairrace.service.config.files.PublicFiles;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

/**
 * 比赛管理命令
 * */
@AutoRegistration
public class MatchCommand implements BootstrapModule {
    public void onBootstrap(@NonNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("match")
                    .requires(
                            ctx -> ctx.getSender().hasPermission(PermissionNode.MATCH_COMMAND.getValue())
                    )
                    .then(Commands.literal("start")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();

                                // 开始游戏
                                if (!MatchCoordinator.getInstance().startMatch(null).isSuccess()) {
                                    return 0;
                                }

                                return 1;
                            })
                    ).then(Commands.literal("stop")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();

                                MatchCoordinator.StopResult stopResult = MatchCoordinator.getInstance().stopMatch();
                                if (!stopResult.isSuccess()) {
                                    Component message = stopResult.getDisplayMessage();
                                    if (message == null) message = Component.translatable(
                                            TranslateKeys.Command.MatchCmd.Stop.FAILURE
                                    );
                                    sender.sendMessage(message.color(NamedTextColor.RED));
                                    return 1;
                                }

                                sender.sendMessage(Component.translatable(TranslateKeys.Command.MatchCmd.Stop.SUCCESS));
                                return 1;
                            })
                    ).then(Commands.literal("set_map")
                            .then(Commands.argument("map_id", StringArgumentType.string())
                                    .executes(ctx -> {
                                        CommandSender sender = ctx.getSource().getSender();
                                        String rawMapId = ctx.getArgument("map_id", String.class);
                                        NamespacedKey newMapId = NamespacedKey.fromString(rawMapId);
                                        if (newMapId == null) {
                                            sender.sendMessage(Component.translatable(
                                                            TranslateKeys.Command.MatchCmd.SetMap.ID_FORMAT_ERROR
                                                    )
                                                    .color(NamedTextColor.RED));
                                            return 1;
                                        }

                                        // 检查地图是否存在
                                        if (!MapRegistry.getInstance().containsMap(newMapId)) {
                                            sender.sendMessage(Component.translatable(
                                                    TranslateKeys.Command.MatchCmd.SetMap.MAP_NOTFOUND
                                                    ).arguments(Component.text(newMapId.toString()))
                                                    .color(NamedTextColor.RED));
                                            return 1;
                                        }

                                        // 检查地图是否可玩
                                        GameMap targetMap = MapRegistry.getInstance().CreateMapInstance(newMapId);
                                        if (!(targetMap instanceof PlayableGameMap)) {
                                            sender.sendMessage(Component.translatable(
                                                    TranslateKeys.Command.MatchCmd.SetMap.MAP_NOT_PLAYABLE
                                                    ).arguments(Component.text(rawMapId))
                                                    .color(NamedTextColor.RED));
                                            return 1;
                                        }

                                        // 修改值
                                        Config.getInstance()
                                                .getYmlConfig(PublicFiles.GAME_SETTINGS)
                                                .set(GameSettingKeys.SELECTED_MAP_ID, newMapId.toString());

                                        // 提示
                                        sender.sendMessage(
                                                Component.translatable(TranslateKeys.Command.MatchCmd.SetMap.SUCCESS)
                                                        .arguments(
                                                                Component.text(rawMapId),
                                                                targetMap.getElementMeta().mainName()
                                                        )
                                        );
                                        return 1;
                                    })
                            )
                    )
                    .then(Commands.literal("get_map")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                NamespacedKey selectedMapId = NamespacedKey.fromString(
                                        Config.getInstance()
                                                .getYmlConfig(PublicFiles.GAME_SETTINGS)
                                                .get(GameSettingKeys.SELECTED_MAP_ID, null)
                                );

                                GameMap selectedMap = MapRegistry.getInstance().CreateMapInstance(selectedMapId);
                                sender.sendMessage(
                                        Component.translatable(TranslateKeys.Command.MatchCmd.GetMap.SUCCESS)
                                                .arguments(
                                                        Component.text(selectedMapId == null
                                                                ? "null"
                                                                : selectedMapId.toString()
                                                        ),
                                                        selectedMap == null
                                                            ? Component.translatable(
                                                                    TranslateKeys.Command.MatchCmd.GetMap.DEFAULT_MAP_NAME)
                                                            : selectedMap.getElementMeta().mainName()
                                                )
                                );
                                return 1;
                            })
                    )
                    .build();
            commandsEvent.registrar().register(node);
        });
    }
}
