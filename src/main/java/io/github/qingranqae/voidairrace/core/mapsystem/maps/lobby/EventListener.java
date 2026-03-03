package io.github.qingranqae.voidairrace.core.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.constants.PlayerPDCKey;
import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.core.config.ConfigFiles;
import io.github.qingranqae.voidairrace.core.config.GameSettingKey;
import io.github.qingranqae.voidairrace.core.mapsystem.MapRegistry;
import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerState;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerStateManager;
import io.github.qingranqae.voidairrace.core.playerstatemanager.systems.StateSystem;
import io.github.qingranqae.voidairrace.core.playerstatemanager.systems.play.PlayState;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.core.teamsystem.Teams;
import io.github.qingranqae.voidairrace.event.ConfigFieldChangeEvent;
import io.github.qingranqae.voidairrace.event.MatchOverEvent;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.util.worldutil.Region;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class EventListener implements Listener {

    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        State.matchRunning = true;
    }

    @EventHandler
    public void onMatchOver(MatchOverEvent event) {
        State.matchRunning = false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (State.matchRunning) return;

        Player player = event.getPlayer();
        TeamRoster teamRoster = TeamRoster.getInstance();

        for (HashMap.Entry<Region, teamArea> i: Const.getRegionToTeam().entrySet()) {
            Region region = i.getKey();
            teamArea area = i.getValue();
            if (region.contains(player.getLocation())
                    && area.id() <= State.activeTeamAreaCount) { // 将玩家加入区域对应的队伍
                teamRoster.join(player, area.team());
            }
        }
    }

    @EventHandler
    public void onPlayerOpenContainer(InventoryOpenEvent event) {
        try {
            World eventWorld = event.getPlayer().getWorld();
            if (Const.getMapWorld() != null && Const.getMapWorld().equals(eventWorld)) {
                event.setCancelled(true);
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onConfigChange(ConfigFieldChangeEvent  event) {
        if (event.getField() == GameSettingKey.SELECTED_MAP_ID) {
            String newValue = event.getNewValue(String.class);

            // 更新 激活的 队伍选择区域 数量
            try {
                if (MapRegistry.getInstance().getMapById(newValue) instanceof PlayableGameMap map) {
                    State.activeTeamAreaCount = map.maxTeamsNumber();
                }
            } catch (IllegalArgumentException e) {
                State.activeTeamAreaCount = 0;
            } catch (IllegalStateException ignored) {
                // 在插件启用期间会触发这个事件，这时获取地图注册表实例会抛出这个异常。但这不影响初始化后功能的正常运行
            }

            // 将所有 加入了 不被地图允许的队伍 的玩家 踢出队伍
            TeamRoster teamRoster = TeamRoster.getInstance();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                // 跳过 不是 空闲状态 的玩家
                NamespacedKey state = PlayerStateManager.getInstance().getState(player, StateSystem.PLAY.getValue());
                if (!PlayState.FREE.getValue().equals(state)) continue;

                // 跳过 没加入队伍的 玩家
                Teams playerTeam = teamRoster.teamToEnum(teamRoster.getEntityTeam(player));
                if (playerTeam == null) continue;

                // 检查 玩家当前加入的队伍 是否是 当前地图 不允许的
                if (playerTeam.ordinal() >= State.activeTeamAreaCount) teamRoster.leave(player);
            }
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        String selectedMapId = Config.getInstance().getConfig(ConfigFiles.GAME_SETTINGS).getString(GameSettingKey.SELECTED_MAP_ID);
        if (MapRegistry.getInstance().getMapById(selectedMapId) instanceof PlayableGameMap map) {
            State.activeTeamAreaCount = map.maxTeamsNumber();
        } else {
            State.activeTeamAreaCount = 0;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity source = event.getDamager();
        if (source instanceof Player player &&
                source.getLocation().getWorld().equals(Const.getMapWorld())) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.LEVITATION,
                    60,
                    2
            ));
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    120,
                    9
            ));
            event.setCancelled(true);
        }
    }
}
