package io.github.qingranqae.voidairrace.test;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.corelayer.teamroster.TeamRoster;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Team;

public class Test implements Listener {

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        event.getMainClass().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("vartest")
                    .executes(ctx -> {
                        if (ctx.getSource().getSender() instanceof Player player) {
                            Team team = TeamRoster.getInstance().getEntityTeam(player);

                            if (team != null) {
                                player.sendMessage("你当前在队伍: " + team.getName());
                                player.sendMessage(team.displayName());
                            } else {
                                player.sendMessage("你当前不在任何队伍中");
                            }
                        }
                        return 1;
                    })
                    .build();
            commandsEvent.registrar().register(node);
        });
    }
}
