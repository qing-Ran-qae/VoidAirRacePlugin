package io.github.qingranqae.voidairrace.core.arenasystem;

import io.github.qingranqae.voidairrace.constants.Namespace;
import io.github.qingranqae.voidairrace.constants.ResourcePath;
import io.github.qingranqae.voidairrace.core.result.arena.*;
import io.github.qingranqae.voidairrace.exception.LoadArenaException;
import io.github.qingranqae.voidairrace.infrastructure.util.JarEntryUtil;
import io.github.qingranqae.voidairrace.infrastructure.util.world.WorldCreatorUtil;
import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.service.config.files.GameSettingKeys;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import io.github.qingranqae.voidairrace.service.spawnutil.SpawnUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 管理多个竞技场的加载/卸载，每个竞技场用数字 ID 标识（{@code 1} ~ {@code maxArenas}）
 */
public class ArenaManager {
    private static ArenaManager instance;

    public static ArenaManager getInstance(JavaPlugin mainClass) {
        if (instance == null) instance = new ArenaManager(mainClass);
        return instance;
    }

    public static ArenaManager getInstance() {
        if (instance == null) throw new IllegalStateException("竞技场管理器还未初始化，无法获取实例");
        return instance;
    }

    // ----------

    /**
     * 最大竞技场数量（从配置文件读取）
     * */
    private Integer maxArenas;

    /**
     * 插件主类实例引用
     * */
    private final JavaPlugin mainClass;

    /**
     * 提示语列表
     * */
    private static final ArrayList<String> tips = new ArrayList<>();

    private final HashMap<Integer, ArenaState> arenaStates = new HashMap<>();

    /**
     * 借据 uid，递增
     * */
    private Integer nextTokenUid = 1;

    static {
        tips.add("Don't put your files here!");
        tips.add("别把你的文件放在这里！");
        tips.add("別把你的文件放在這裡！");
        tips.add("ここにあなたのファイルを置かないでください！");
        tips.add("Не клади свои документы сюда!");
    }

    private ArenaManager(JavaPlugin mainClass) {
        this.mainClass = mainClass;

        // 读取最大竞技场数量，若配置项不存在或无效则使用默认值 4
        ObservableYamlConfiguration gameSettings = Config.getInstance().getConfig(PublicFiles.GAME_SETTINGS);
        this.maxArenas = gameSettings.getInt(GameSettingKeys.MAX_ARENAS, 4);
        if (maxArenas < 1) {
            mainClass.getLogger().warning("配置项 max_arenas 小于 1，已强制改为 1");
            maxArenas = 1;
        }

        // 初始化借用状态
        for (Integer id = 1; id <= maxArenas; id++) {
            arenaStates.put(id, new ArenaState());
        }
    }

    // ------ 借用竞技场世界 ------

    /**
     * 借用一个竞技场世界
     * */
    public @NonNull BorrowArenaResult borrow() {
        Integer freeArenaId = getFreeArena();
        if (freeArenaId == -1) return BorrowArenaResult.failure(
                Component.translatable("void_air_race.arena.arena_manager.no_free_arena")
        );

        // 更新状态
        ArenaState arenaState = arenaState(freeArenaId);
        arenaState.setBorrowed(true);
        Integer tokenUid = nextTokenUid++;
        arenaState.setActiveTokenUid(tokenUid);

        return BorrowArenaResult.success(
                new ArenaToken(freeArenaId, tokenUid)
        );
    }

    /**
     * 凭借据归还一个竞技场世界
     *
     * @param token 借据
     * */
    public @NonNull ReturnArenaResult returnArena(@NonNull ArenaToken token) {
        if (!validateToken(token)) return ReturnArenaResult.failure(
                Component.translatable("void_air_race.arena.arena_manager.token_is_invalid")
        );

        // 卸载世界
        unloadArenaWorld(token.getArenaId());

        // 更新状态
        ArenaState arenaState = arenaState(token.getArenaId());
        arenaState.setBorrowed(false);
        arenaState.setActiveTokenUid(null);

        return ReturnArenaResult.success();
    }

    // ------ 对竞技场的操作 ------

    /**
     * 将指定竞技场数据加载到竞技场世界中
     *
     * @param token 此借据都应的竞技场世界将要承载竞技场数据
     * @param arenaName 要加载的竞技场名称（对应 {@code resources/arena/} 下的子目录名）
     * */
    public @NonNull LoadArenaResult loadArena(ArenaToken token, String arenaName) {
        if (!validateToken(token)) return LoadArenaResult.failure(
                Component.translatable("void_air_race.arena.arena_manager.token_is_invalid")
        );
        try {
            loadArena(token.getArenaId(), arenaName);
        } catch (IOException e) {
            return LoadArenaResult.failure(
                    Component.translatable("void_air_race.arena.arena_manager.io_exception")
            );
        }
        return LoadArenaResult.success();
    }

    /**
     * 加载竞技场世界
     *
     * @param token 加载此借据对应的竞技场世界
     *
     * @see LoadArenaException
     * */
    public @NonNull LoadArenaResult loadArenaWorld(ArenaToken token)  {
        if (!validateToken(token)) return LoadArenaResult.failure(
                Component.translatable("void_air_race.arena.arena_manager.token_is_invalid")
        );
        try {
            loadArenaWorld(token.getArenaId());
        } catch (LoadArenaException e) {
            return LoadArenaResult.failure(
                    Component.translatable("")
            );
        }
        return LoadArenaResult.success();
    }

    /**
     * 卸载竞技场世界（不保存内存中的修改）
     * */
    public @NonNull UnloadArenaWorldResult unloadArenaWorld(ArenaToken token) {
        if (!validateToken(token)) return UnloadArenaWorldResult.failure(
                Component.translatable("void_air_race.arena.arena_manager.token_is_invalid")
        );
        unloadArenaWorld(token.getArenaId());
        return UnloadArenaWorldResult.success();
    }

    /**
     * 获取借据对应的竞技场世界<br>
     * 如果世界未加载，那么会自动加载它
     * */
    public @NonNull GetTokenWorldResult getTokenWorld(@NonNull ArenaToken token) {
        if (!validateToken(token)) return GetTokenWorldResult.failure(
                Component.translatable("void_air_race.arena.arena_manager.token_is_invalid")
        );
        loadArenaWorld(token.getArenaId());
        return GetTokenWorldResult.success(
                arenaState(token.getArenaId()).getLoadedWorld()
        );
    }

    // ---------- 状态查询 ----------

    /**
     * 获取最大竞技场数量
     * */
    public int getMaxArenas() {
        return maxArenas;
    }

    /**
     * 获取还未被借出的竞技场数量
     * */
    public int getFreesArena() {
        int result = 0;
        for (ArenaState arenaState : arenaStates.values()) {
            if (!arenaState.isBorrowed()) result++;
        }
        return result;
    }

    // ---------- 内部辅助方法 ----------

    /**
     * 加载一个竞技场（不是竞技场世界）<br>
     * 如果竞技场世界 已加载，那么会 重新加载 它；如果竞技场世界 未加载，那么会 加载 它
     *
     * @param arenaWorldId 要将竞技场加载到的竞技场世界 id
     * @param arenaName 要加载的竞技场名称（对应 {@code resources/arena/} 下的子目录名）
     *
     * @throws IOException 复制竞技场数据时出现 IO 错误则抛出
     * */
    private void loadArena(Integer arenaWorldId, String arenaName) throws IOException {
        // 如果该竞技场世界已加载，先卸载（不保存修改）
        if (arenaState(arenaWorldId).getLoadedWorld() != null) {
            unloadArenaWorld(arenaWorldId);
        }

        String worldName = arenaIdToWorldName(arenaWorldId);

        // 复制竞技场数据
        JarEntryUtil.copyFromJar(ResourcePath.ARENA.getPath() + "/" + arenaName, worldName);

        // 加载世界
        loadArenaWorld(arenaWorldId);
    }

    /**
     * 加载指定竞技场世界<br>
     * 如果尝试重复加载同一世界，那么不会执行任何操作
     *
     * @param arenaId 竞技场 ID（{@code 1} ~ {@code maxArenas}）
     *
     * @throws LoadArenaException 当竞技场复制失败或世界加载失败时抛出
     */
    private World loadArenaWorld(Integer arenaId) throws LoadArenaException {
        // 已加载直接返回，否则加载新世界
        World loadedWorld = arenaState(arenaId).getLoadedWorld();
        if (loadedWorld != null) return loadedWorld;

        String worldName = arenaIdToWorldName(arenaId);

        // 添加提示文件
        addTips(worldName);

        // 创建并加载世界
        World newWorld = WorldCreatorUtil.createVoidWorld(worldName);
        arenaState(arenaId).setLoadedWorld(newWorld);
        return newWorld;
    }

    /**
     * 卸载指定竞技场世界，并且不保存内存中的修改
     *
     * @param arenaId 竞技场 ID
     */
    private void unloadArenaWorld(Integer arenaId) {
        World world = arenaState(arenaId).getLoadedWorld();
        if (world == null) return;

        // 将世界内的所有玩家传送回出生点
        for (Player player : world.getPlayers()) {
            SpawnUtil.tpToSpawnPoint(player);
        }

        // 卸载世界
        Bukkit.unloadWorld(world, false);

        // 更新状态
        arenaState(arenaId).setLoadedWorld(null);
    }

    /**
     * @return  储存竞技场状态的对象
     * */
    private @NonNull ArenaState arenaState(Integer arenaId) {
        return arenaStates.get(arenaId);
    }

    /**
     * @return 空闲竞技场的 ID，如果没有空闲的竞技场则返回 {@code -1}
     */
    private Integer getFreeArena() {
        for (Integer id = 1; id <= maxArenas; id++) {
            if (!arenaState(id).isBorrowed()) {
                return id;
            }
        }
        return -1;
    }

    /**
     *根据竞技场 ID 生成世界文件夹名称
     * */
    private String arenaIdToWorldName(Integer arenaId) {
        return Namespace.namespace + ".arena." + arenaId;
    }

    /**
     * 在世界目录中添加提示文件
     * */
    private void addTips(String targetDir) {
        File targetDirFile = new File(targetDir);
        if (!targetDirFile.exists() && !targetDirFile.mkdirs()) {
            mainClass.getLogger().warning("无法创建世界目录：" + targetDir);
            return;
        }
        for (String tipText : tips) {
            File tipFile = new File(targetDirFile, tipText);
            try {
                tipFile.createNewFile();
            } catch (IOException e) {
                mainClass.getLogger().warning("创建提示语文件 '" + tipText + "' 失败");
            }
        }
    }

    /**
     * 检查借据是否有效
     *
     * @return {@code true} 表示有效，{@code false} 表示无效
     * */
    private boolean validateToken(@NonNull ArenaToken token) {
        Integer activeTokenUid = arenaState(token.getArenaId()).getActiveTokenUid();
        return token != null // 应对以后可能的更改
                && token.getUid().equals(activeTokenUid);
    }
}