package io.github.qingranqae.voidairrace.test;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.qingranqae.voidairrace.constants.PlayerPDCKey;
import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.core.config.ConfigFiles;
import io.github.qingranqae.voidairrace.core.config.FlagsKey;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class Test implements Listener {

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        event.getMainClass().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commandsEvent -> {
            LiteralCommandNode<CommandSourceStack> node = Commands.literal("vartest")
                    .executes(ctx -> {
                        Boolean state = null;
                        if (ctx.getSource().getSender() instanceof Player p) {
                            state = p.getPersistentDataContainer().get(PlayerPDCKey.INITIALIZED.getValue(), PersistentDataType.BOOLEAN);
                            Bukkit.getServer().broadcast(Component.text(p.getName() + " 的当前队伍：" + TeamRoster.getInstance().teamToEnum(TeamRoster.getInstance().getEntityTeam(p)).getName()));
                        }
                        Bukkit.getServer().broadcast(Component.text(ctx.getSource().getSender().getName() + "的初始化状态：" + state));
                        Bukkit.getServer().broadcast(Component.text("当前比赛状态：" + MatchCoordinator.getInstance().getMatchState()));
                        Bukkit.getServer().broadcast(Component.text("“服务器启动时结束比赛” 标志状态：" + Config.getInstance().getConfig(ConfigFiles.FLAGS).getBoolean(FlagsKey.ON_SERVER_STARTED_STOP_MATCH)));

                        // Bukkit.getServer().broadcast(Component.text());

                        return 1;
                    })
                    .build();
            commandsEvent.registrar().register(node);
        });
    }
}
