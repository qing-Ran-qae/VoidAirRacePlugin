package io.github.qingranqae.voidairrace.core.mapsystem.maps.grassland;

import io.github.qingranqae.voidairrace.constants.Namespace;
import io.github.qingranqae.voidairrace.core.arenasystem.ArenaManager;
import io.github.qingranqae.voidairrace.core.arenasystem.ArenaToken;
import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchScope;
import io.github.qingranqae.voidairrace.core.result.arena.BorrowArenaResult;
import io.github.qingranqae.voidairrace.core.result.arena.LoadArenaResult;
import io.github.qingranqae.voidairrace.core.result.map.MapSelectedStartResult;
import io.github.qingranqae.voidairrace.core.result.matchrule.ManagerEnableRuleResult;
import io.github.qingranqae.voidairrace.core.rulesystem.MatchRule;
import io.github.qingranqae.voidairrace.core.rulesystem.RuleManager;
import io.github.qingranqae.voidairrace.core.rulesystem.generalrules.BasicEndDetermination;
import io.github.qingranqae.voidairrace.core.rulesystem.generalrules.UpdateMatchTime;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.exception.LoadArenaException;
import io.github.qingranqae.voidairrace.infrastructure.util.schedulingutil.SchedulingUtil;
import io.github.qingranqae.voidairrace.infrastructure.util.world.BlockRegion;
import io.github.qingranqae.voidairrace.infrastructure.util.world.blockfinder.BlockFinder;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GrassLand extends PlayableGameMap implements Listener {
    private ArenaToken arena;

    @Override
    public @NonNull String getId() {
        return Const.MAP_ID;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("void_air_race.map.grassland.name");
    }

    @Override
    public @NonNull Component getDescription() {
        return Component.translatable("void_air_race.map.grassland.description.line1");
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public @NonNull MapSelectedStartResult selectedStart(Match match) {
        // 加载竞技场
        BorrowArenaResult borrowResult = ArenaManager.getInstance().borrow();
        ArenaToken arena = borrowResult.getValue();
        if (!borrowResult.isSuccess() || arena == null) return MapSelectedStartResult.failure(
                borrowResult.getDisplayMessage() == null
                        ? Component.translatable("void_air_race.map.grassland.selected_start.failed.cnknown_cause")
                        : Component.translatable("void_air_race.map.grassland.selected_start.failed.specified_reason")
                        .arguments(borrowResult.getDisplayMessage())
        );
        this.arena = arena;
        match.addScope(new MatchScope(arena, new HashMap<>()));

        playerEntryArena(match);
        enableRules(match);
        deliverSupplies(match);

        return MapSelectedStartResult.success();
    }

    private void playerEntryArena(Match match) {
        for (Player player : match.getConfig().contestants()) {
            Team playerTeam = TeamRoster.getInstance().getEntityTeam(player);
            if (playerTeam != null) {
                Location loc = Const.TEAM_TO_SPAWN_LOCATION.get(playerTeam);
                loc.setWorld(arena.getWorld().getValue());
                player.teleport(loc);
            }
        }
    }

    /**
     * 填充地图上的所有补给箱
     */
    private void deliverSupplies(Match match) {
        // 获取箱子坐标列表（类型转换不会失败，因为写入时严格控制了类型）
        List<Location> supplies = (List<Location>) getConfigFile(MapConfigFiles.DATA).getList(MapConfigKeys.SUPPLY_CHESTS);
        if (supplies == null || supplies.isEmpty()) {
            return;
        }

        // 要刷新的物品
        LootTable supplyLoot = Bukkit.getLootTable(
                new NamespacedKey(Namespace.namespace, "maps/grass_land/supply")
        );

        for (Location loc : supplies) {
            // 文件仅记录箱子位置，每局游戏都可能使用不同竞技场（不同世界）
            loc.setWorld(arena.getWorld().getValue());

            Block block = loc.getBlock();
            if (block.getState() instanceof Chest chest) {
                Inventory inv = chest.getInventory();
                inv.clear(); // 清空箱子，避免残留
                chest.setLootTable(supplyLoot);
                chest.update();
            }
        }
    }

    /**
     * 启用一些规则
     */
    private void enableRules(Match match) {
        RuleManager ruleManager = match.getRuleManager();
        ArrayList<MatchRule> rules = new ArrayList<>();

        rules.add(new UpdateMatchTime());
        rules.add(new BasicEndDetermination());

        for (MatchRule rule : rules) {
            ManagerEnableRuleResult enableRuleResult = ruleManager.enableRule(rule);
            Component displayMessage = enableRuleResult.getDisplayMessage();
            if (!enableRuleResult.isSuccess()) {
                Component message = (displayMessage == null
                        ? Component.translatable("void_air_race.map.grassland.selected_start.failed.unknown_cause")
                        :Component.translatable("void_air_race.map.grassland.selected_start.failed.specified_reason")
                        .arguments(displayMessage));
                Bukkit.getServer().broadcast(message.color(NamedTextColor.RED)); // 后面可能还有其他步骤，所以不能返回
            }
        }
    }

    @Override
    public int maxTeams() {
        return 4;
    }

    @EventHandler
    public void onPlayerFailMove(PlayerFailMoveEvent event) {
        if (event.getPlayer().getLocation().getWorld().equals(arena.getWorld().getValue())) {
            event.setLogWarning(false);
        }
    }

    @Override
    public @NonNull CompletableFuture<?> initAsync(JavaPlugin mainClass) {
        return findSupplyBoxes();
    }

    /**
     * 获取并储存所有物资箱子的位置
     */
    private CompletableFuture<?> findSupplyBoxes() {
        // 申请临时竞技场
        BorrowArenaResult borrowResult = ArenaManager.getInstance().borrow();
        ArenaToken tempArena = borrowResult.getValue();
        if (!borrowResult.isSuccess() || tempArena == null) {
            return CompletableFuture.failedFuture(
                    new NullPointerException("未申请到临时竞技场，无法初始化")
            );
        }
        LoadArenaResult loadArenaResult = tempArena.loadArena(Const.MAP_ID);
        if (!loadArenaResult.isSuccess()) {
            return CompletableFuture.failedFuture(
                    new LoadArenaException(
                            "加载竞技场失败：" + loadArenaResult.getDisplayMessage(),
                            Component.translatable("void_air_race.map.grassland.find_supply_boxes.load_arena_failure")
                    )
            );
        }

        // 为每个区域创建异步扫描任务
        World world = tempArena.getWorld().getValue();
        if (world == null) {
            tempArena.returnArena(); // 归还竞技场
            return CompletableFuture.failedFuture(
                    new IllegalStateException("无法获取竞技场世界，初始化失败")
            );
        }
        List<CompletableFuture<List<Location>>> futures = new ArrayList<>();
        for (BlockRegion area : Const.SUPPLY_BOX_SEARCH_RANGE) {
            futures.add(BlockFinder.findBlocksByMaterialAsync(
                    world,
                    area.getMinX(), area.getMinY(), area.getMinZ(),
                    area.getMaxX(), area.getMaxY(), area.getMaxZ(),
                    Material.CHEST));
        }

        // 等待所有扫描完成，合并结果并去重
        CompletableFuture<List<Location>> allLocationsFuture = CompletableFuture.allOf(
                        futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    // 合并所有扫描结果
                    List<Location> combined = new ArrayList<>();
                    for (CompletableFuture<List<Location>> f : futures) {
                        combined.addAll(f.join());
                    }
                    // 去重（同一世界，坐标相同即视为重复）
                    return combined.stream().distinct().collect(Collectors.toList());
                });

        // 保存结果并归还竞技场
        return allLocationsFuture.thenAcceptAsync(locations -> {
            // 设置正确的世界引用
            for (Location loc : locations) {
                loc.setWorld(world);
            }
            // 保存到配置文件
            getConfigFile(MapConfigFiles.DATA).set(MapConfigKeys.SUPPLY_CHESTS, locations);
            // 归还临时竞技场
            // debug
            //tempArena.returnArena();
        }, SchedulingUtil::runOnMainThread);
    }
}