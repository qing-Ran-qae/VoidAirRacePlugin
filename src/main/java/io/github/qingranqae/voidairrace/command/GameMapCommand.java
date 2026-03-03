package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.core.mapsystem.GameMap;
import io.github.qingranqae.voidairrace.core.mapsystem.MapInitializer;
import io.github.qingranqae.voidairrace.core.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.function.Supplier;

public class GameMapCommand implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        event.getMainClass().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("gamemap")
                    .then(
                            Commands.literal("list")
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                HashMap<String, Supplier<GameMap>> maps = MapRegistry.getInstance().getAllMaps();

                                sender.sendMessage(Component.translatable("void_air_race.command.gamemap.list.start"));
                                for (Supplier<GameMap> mapSupplier : maps.values()) {
                                    GameMap mapInst = mapSupplier.get();

                                    String translateKey = (mapInst instanceof PlayableGameMap ?
                                            "void_air_race.command.gamemap.list.playable_map_info"
                                            : "void_air_race.command.gamemap.list.not_playable_map_info");
                                    sender.sendMessage(Component.translatable(translateKey)
                                            .arguments(mapInst.getDisplayName(), Component.text(mapInst.getId())));
                                }
                                sender.sendMessage(Component.translatable("void_air_race.command.gamemap.list.end"));
                                return 1;
                            })
                    ).then(
                            Commands.literal("reinit")
                            .then(Commands.argument("map_id", StringArgumentType.string())
                                    .executes(ctx -> {
                                        CommandSender sender = ctx.getSource().getSender();
                                        String targetMapId =  ctx.getArgument("map_id", String.class);
                                        MapRegistry mapRegistry = MapRegistry.getInstance();
                                        if (!mapRegistry.containsMap(targetMapId)) {
                                            sender.sendMessage(Component.translatable("void_air_race.command.gamemap.reinit.map_not_found"));
                                            return 1;
                                        }
                                        MapInitializer.getInstance().reinitMap(targetMapId);
                                        sender.sendMessage(Component.translatable("void_air_race.command.gamemap.reinit.ok"));
                                        return 1;
                                    })
                            )
                    )
                    .build();
            commandsEvent.registrar().register(node);
        });
    }
}
