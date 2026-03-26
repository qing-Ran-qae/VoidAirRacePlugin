package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.constants.PermissionNode;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerInitializer;
import io.github.qingranqae.voidairrace.infrastructure.BootstrapModule;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

@AutoRegistration
public class PlayerManagerCommand implements BootstrapModule {
    public void onBootstrap(@NonNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("player_manager")
                    .requires(ctx -> ctx.getSender().hasPermission(PermissionNode.PLAYER_MANAGER_COMMAND.getValue()))
                    .then(Commands.literal("init")
                            .then(Commands.literal("get")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .executes(ctx -> {
                                                CommandSender sender = ctx.getSource().getSender();
                                                Player targetPlayer = Bukkit.getPlayer(ctx.getArgument("player", String.class));
                                                if (targetPlayer == null) {
                                                    sender.sendMessage(Component.translatable("void_air_race.command.player_manager.init.get.null_player")
                                                            .arguments(Component.text(ctx.getArgument("player", String.class)))
                                                            .color(NamedTextColor.RED)
                                                    );
                                                    return 1;
                                                }
                                                String key = PlayerInitializer.getInstance().isInitialized(targetPlayer)
                                                        ? "void_air_race.command.player_manager.init.get.yes"
                                                        : "void_air_race.command.player_manager.init.get.no";
                                                sender.sendMessage(Component.translatable(key)
                                                            .arguments(Component.text(targetPlayer.getName())));
                                                return 1;
                                            })
                                    )
                            ).then(Commands.literal("reinit")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .executes(ctx -> {
                                                CommandSender sender = ctx.getSource().getSender();
                                                Player targetPlayer = Bukkit.getPlayer(ctx.getArgument("player", String.class));
                                                if (targetPlayer == null) {
                                                    sender.sendMessage(Component.translatable("void_air_race.command.player_manager.init.reinit.null_player")
                                                            .arguments(Component.text(ctx.getArgument("player", String.class)))
                                                            .color(NamedTextColor.RED)
                                                    );
                                                    return 1;
                                                }
                                                PlayerInitializer.getInstance().reInitPlayer(targetPlayer);
                                                sender.sendMessage(Component.translatable("void_air_race.command.player_manager.init.reinit.ok")
                                                        .arguments(Component.text(targetPlayer.getName()))
                                                );
                                                return 1;
                                            })
                                    )
                            )
                    ).then(Commands.literal("state")
                            .then(Commands.literal("get"))
                    )
                    .build();
            commandsEvent.registrar().register(node);
        });
    }
}
