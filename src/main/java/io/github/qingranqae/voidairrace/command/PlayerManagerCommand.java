package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerManagerCommand implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        event.getMainClass().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("player_manager")
                    .then(Commands.literal("init")
                            .then(Commands.literal("get")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .executes(ctx -> {
                                                return 1;
                                            })
                                    )
                            )
                            .then(Commands.literal("reinit")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .executes(ctx -> {
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
