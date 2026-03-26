package io.github.qingranqae.voidairrace.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.constants.Namespace;
import io.github.qingranqae.voidairrace.core.arenasystem.ArenaManager;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerInitializer;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerStateManager;
import io.github.qingranqae.voidairrace.core.playerstatemanager.StateRegistry;
import io.github.qingranqae.voidairrace.core.playerstatemanager.StateSystemMeta;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.files.FlagsKeys;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.Map;

@AutoRegistration
public class DebugCommand implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        event.getMainClass().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("vardebug")
                    .then(Commands.literal("info")
                            .then(Commands.literal("player")
                                    .executes(ctx -> {
                                        if (!(ctx.getSource().getSender() instanceof Player)) return 1;

                                        Player sender = (Player) ctx.getSource().getSender();
                                        Server server = Bukkit.getServer();

                                        server.broadcast(Component.text("------ [玩家信息] ------"));

                                        Team playerTeam = TeamRoster.getInstance().getEntityTeam(sender);
                                        if (playerTeam != null) {
                                            server.broadcast(Component.text(sender.getName() + "的当前队伍："
                                                            + TeamRoster.getInstance().teamToEnum(
                                                                    TeamRoster.getInstance().getEntityTeam(sender)
                                                            )
                                                            .getName())
                                            );
                                        } else {
                                            server.broadcast(Component.text(sender.getName() + "的当前队伍：无"));
                                        }

                                        server.broadcast(Component.text(sender.getName() + "的初始化状态：" + PlayerInitializer.getInstance().isInitialized(sender)));

                                        server.broadcast(Component.text(sender.getName() + "在所有状态体系的当前状态："));
                                        Map<String, String> states = PlayerStateManager.getInstance().getAllStates(sender);
                                        for (Map.Entry<String, String> entry : states.entrySet()) {
                                            server.broadcast(Component.text("-" + entry.getKey() + ": " + entry.getValue()));
                                        }
                                        if (states.isEmpty()) server.broadcast(Component.text("（没有任何状态）"));

                                        server.broadcast(Component.text("------"));

                                        return 1;
                                    })
                            ).then(Commands.literal("server")
                                    .executes(ctx -> {
                                        Server server = Bukkit.getServer();

                                        server.broadcast(Component.text("------ [服务器信息] ------"));

                                        server.broadcast(Component.text("已注册状态体系列表："));
                                        for (Map.Entry<String, StateSystemMeta> state : StateRegistry.getInstance().getAllSystems().entrySet()) {
                                            server.broadcast(Component.text("- " + state.toString()));
                                        }

                                        server.broadcast(Component.text("当前比赛状态：" + MatchCoordinator.getInstance().getMatchState()));
                                        server.broadcast(Component.text("“服务器启动时结束比赛” 标志状态：" + Config.getInstance().getConfig(PublicFiles.FLAGS).getBoolean(FlagsKeys.ON_SERVER_STARTED_STOP_MATCH)));

                                        ArenaManager arenaManager = ArenaManager.getInstance();
                                        server.broadcast(Component.text("竞技场：" + arenaManager.getFreesArena() + "/" + arenaManager.getMaxArenas()));

                                        server.broadcast(Component.text("------"));
                                        return 0;
                                    })
                            )
                    ).then(Commands.literal("temp")
                            .then(Commands.literal("a")
                                    .executes(ctx -> {
                                        if (ctx.getSource().getSender() instanceof Player player) {
                                            World world = player.getLocation().getWorld();
                                            if (world.getBlockAt(player.getLocation()).getState() instanceof Chest chest) {
                                                Server server = Bukkit.getServer();

                                                chest.getInventory().setItem(0, new ItemStack(Material.DIAMOND));

//                                                Entity tempEntity = chest.getLocation().getWorld().spawnEntity(chest.getLocation(), EntityType.MARKER);
//                                                ItemStack[] items = Bukkit.getLootTable(
//                                                        new NamespacedKey(Namespace.namespace, "supply/battle/level_b")
//                                                ).populateLoot(
//                                                        new Random(),
//                                                        new LootContext.Builder(chest.getLocation())
//                                                                .lootedEntity(tempEntity)
//                                                                .build()
//                                                ).toArray(new ItemStack[0]);
//                                                for (ItemStack itemstack : items) {
//                                                    chest.getInventory().addItem(itemstack);
//                                                }

                                                server.broadcast(Component.text("已填充物品："));
                                                server.broadcast(Component.text("-chest_loc: " + chest.getLocation()));
                                            }
                                        }
                                        return 1;
                                    })
                            ).then(Commands.literal("b")
                                    .executes(ctx -> {

                                        return 1;
                                    })
                            )
                    )
                    .build();
            commandsEvent.registrar().register(node);
        });
    }
}