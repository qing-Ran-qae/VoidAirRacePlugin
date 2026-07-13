package io.github.hhn756.voidairrace.core.arena;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.Namespace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.result.base.OperationResult;
import io.github.hhn756.voidairrace.core.result.base.ValueResult;
import io.github.hhn756.voidairrace.exception.ArenaException;
import io.github.hhn756.voidairrace.infrastructure.util.JarEntryUtil;
import io.github.hhn756.voidairrace.infrastructure.util.world.WorldCreatorUtil;
import io.github.hhn756.voidairrace.service.config.Config;
import io.github.hhn756.voidairrace.service.config.YamlConfig;
import io.github.hhn756.voidairrace.service.config.files.GameSettingKeys;
import io.github.hhn756.voidairrace.service.config.files.GlobalSettingKeys;
import io.github.hhn756.voidairrace.service.config.files.PublicFiles;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 管理多个竞技场的加载/卸载，每个竞技场用数字 ID 标识（{@code 1} ~ {@code maxArenas}）
 */
public class ArenaManager {
    private static ArenaManager instance;

    static void load() {
        instance = new ArenaManager();
    }

    static void unload() {
        instance = null;
    }

    public static ArenaManager getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("竞技场管理器实例不存在");
        return instance;
    }

    // ----------

    /**
     * 最大竞技场数量
     * */
    private Integer maxArenas;

    /**
     * 提示语列表
     * */
    private static final String[] tips = {
            "Don't put your files here!",
            "别把你的文件放在这里！",
            "別把你的文件放在這裡！",
            "ここにあなたのファイルを置かないでください！",
            "Не клади свои документы сюда!"
    };

    private final HashMap<Integer, ArenaState> arenaStates = new HashMap<>();

    /**
     * 借据 uid，递增
     * */
    private Integer nextTokenUid = 1;

    /**
     * 未借出的竞技场数量
     * */
    private Integer freeCount;

    private ArenaManager() {
        VoidAirRace mainClass = VoidAirRace.getInstance();

        // 读取最大竞技场数量，若配置项不存在或无效则使用默认值
        YamlConfig gameSettings = Config.getInstance().getYmlConfig(PublicFiles.GAME_SETTINGS);
        this.maxArenas = gameSettings.get(GameSettingKeys.MAX_ARENAS, 16);
        if (maxArenas < 1) {
            mainClass.getLogger().warning("配置中的最大竞技场数量小于 1，已强制改为 1");
            maxArenas = 1;
        }

        // 初始化
        for (Integer id = 1; id <= maxArenas; id++) {
            arenaStates.put(id, new ArenaState());
        }
        freeCount = maxArenas;
    }

    // ------ 借用竞技场世界 ------

    /**
     * 借用一个竞技场世界
     * */
    public @NonNull BorrowArenaResult borrow() {
        Integer freeArenaId = getFreeArena();
        if (freeArenaId == -1) return BorrowArenaResult.failure(
                Component.translatable(TranslateKeys.Arena.ArenaManager.NO_FREE_ARENA)
        );

        // 更新状态
        ArenaState arenaState = arenaState(freeArenaId);
        arenaState.setBorrowed(true);
        Integer tokenUid = nextTokenUid++;
        arenaState.setActiveTokenUid(tokenUid);
        freeCount--;

        return BorrowArenaResult.success(
                new ArenaToken(freeArenaId, tokenUid)
        );
    }

    /**
     * 凭借据归还一个竞技场世界
     *
     * @param token 借据
     *
     * @return 如果成功归还将返回成功的结果，如果借据无效则返回失败的结果
     * */
    public @NonNull ReturnArenaResult returnArena(@NonNull ArenaToken token) {
        if (!validateToken(token)) return ReturnArenaResult.failure(
                Component.translatable(TranslateKeys.Arena.ArenaManager.TOKEN_IS_INVALID)
        );

        // 卸载世界
        unloadArenaWorld(token.getArenaId());

        // 更新状态
        ArenaState arenaState = arenaState(token.getArenaId());
        arenaState.setBorrowed(false);
        arenaState.setActiveTokenUid(null);
        freeCount++;

        return ReturnArenaResult.success();
    }

    // ------ 对竞技场的操作 ------

    /**
     * 将指定竞技场数据加载到竞技场世界中
     *
     * @param token 此借据都应的竞技场世界将要承载竞技场数据
     * @param arenaPath 要加载的竞技场世界数据路径（{@code resource/<arenaPath>/}）
     * */
    public @NonNull LoadArenaResult loadArena(ArenaToken token, String arenaPath) {
        if (!validateToken(token)) return LoadArenaResult.failure(
                Component.translatable(TranslateKeys.Arena.ArenaManager.TOKEN_IS_INVALID)
        );

        try {
            loadArena(token.getArenaId(), arenaPath);
        } catch (IOException e) {
            return LoadArenaResult.failure(
                    Component.translatable(TranslateKeys.Arena.ArenaManager.IO_EXCEPTION)
            );
        }
        return LoadArenaResult.success();
    }

    /**
     * 加载竞技场世界
     *
     * @param token 加载此借据对应的竞技场世界
     *
     * @see ArenaException
     * */
    public @NonNull LoadArenaResult loadArenaWorld(ArenaToken token) {
        if (!validateToken(token)) return LoadArenaResult.failure(
                Component.translatable(TranslateKeys.Arena.ArenaManager.TOKEN_IS_INVALID)
        );

        try {
            loadArenaWorld(token.getArenaId());
        } catch (ArenaException e) {
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
                Component.translatable(TranslateKeys.Arena.ArenaManager.TOKEN_IS_INVALID)
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
                Component.translatable(TranslateKeys.Arena.ArenaManager.TOKEN_IS_INVALID)
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
    public Integer getMaxArenas() {
        return maxArenas;
    }

    /**
     * 获取还未被借出的竞技场数量
     * */
    public Integer getFreeCount() {
        return freeCount;
    }

    // ---------- 内部辅助方法 ----------

    /**
     * 加载一个竞技场（不是竞技场世界）<br>
     * 如果竞技场世界 已加载，那么会 重新加载 它；如果竞技场世界 未加载，那么会 加载 它
     *
     * @param arenaWorldId 要将竞技场加载到的竞技场世界
     * @param arenaPath 要加载的竞技场世界数据路径（{@code resource/<arenaPath>/}）
     *
     * @throws IOException 复制竞技场数据时出现 IO 错误则抛出
     * */
    private void loadArena(Integer arenaWorldId, String arenaPath) throws IOException {
        // 如果该竞技场世界已加载，先卸载（不保存修改）
        if (arenaState(arenaWorldId).getLoadedWorld() != null) {
            unloadArenaWorld(arenaWorldId);
        }

        String worldName = arenaIdToWorldName(arenaWorldId);

        // 复制竞技场数据
        JarEntryUtil.copyFromJar(arenaPath, worldName);

        // 加载世界
        loadArenaWorld(arenaWorldId);
    }

    /**
     * 加载指定竞技场世界<br>
     * 如果尝试重复加载同一世界，那么不会执行任何操作
     *
     * @param arenaId 竞技场 ID（{@code 1} ~ {@code maxArenas}）
     *
     * @throws ArenaException 当竞技场复制失败或世界加载失败时抛出
     */
    private World loadArenaWorld(Integer arenaId) throws ArenaException {
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
        Location spawnLoc = Config.getInstance()
                .getYmlConfig(PublicFiles.GLOBAL_SETTINGS)
                .get(GlobalSettingKeys.SPAWN_LOCATION);
        for (Player player : world.getPlayers()) {
            player.teleport(spawnLoc);
        }

        // 卸载世界
        Bukkit.unloadWorld(world, false);

        // 更新状态
        arenaState(arenaId).setLoadedWorld(null);
    }

    /**
     * @return 储存竞技场状态的对象
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
        return Namespace.str + ".arena." + arenaId;
    }

    /**
     * 在世界目录中添加提示文件
     * */
    private void addTips(String targetDir) {
        VoidAirRace mainClass = VoidAirRace.getInstance();
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
    private Boolean validateToken(@NonNull ArenaToken token) {
        Integer activeTokenUid = arenaState(token.getArenaId()).getActiveTokenUid();
        return token.getUid().equals(activeTokenUid);
    }

    // ------ 结果类型 ------

    public static class BorrowArenaResult extends ValueResult<ArenaToken> {
        public BorrowArenaResult(boolean success, @Nullable ArenaToken value, @Nullable Component displayMessage) {
            super(success, displayMessage, value);
        }

        public static BorrowArenaResult success(ArenaToken token) {
            return new BorrowArenaResult(true, token, null);
        }

        public static BorrowArenaResult failure(Component displayMessage) {
            return new BorrowArenaResult(false, null, displayMessage);
        }
    }

    public static class GetTokenWorldResult extends ValueResult<World> {
        public GetTokenWorldResult(boolean success, @Nullable World value, @Nullable Component displayMessage) {
            super(success, displayMessage, value);
        }

        public static GetTokenWorldResult success(World world) {
            return new GetTokenWorldResult(true, world, null);
        }

        public static GetTokenWorldResult failure(Component displayMessage) {
            return new GetTokenWorldResult(false, null, displayMessage);
        }
    }

    public static class LoadArenaResult extends OperationResult {
        public LoadArenaResult(boolean success, @org.jetbrains.annotations.Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static LoadArenaResult success() {
            return new LoadArenaResult(true, null);
        }

        public static LoadArenaResult failure(Component displayMessage) {
            return new LoadArenaResult(false, displayMessage);
        }
    }

    public static class LoadArenaWorldResult extends OperationResult {
        public LoadArenaWorldResult(boolean success, @org.jetbrains.annotations.Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static LoadArenaWorldResult success() {
            return new LoadArenaWorldResult(true, null);
        }

        public static LoadArenaWorldResult failure(Component displayMessage) {
            return new LoadArenaWorldResult(false, displayMessage);
        }
    }

    public static class ReturnArenaResult extends OperationResult {
        public ReturnArenaResult(boolean success, @org.jetbrains.annotations.Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static ReturnArenaResult success() {
            return new ReturnArenaResult(true, null);
        }

        public static ReturnArenaResult failure(Component displayMessage) {
            return new ReturnArenaResult(false, displayMessage);
        }
    }

    public static class UnloadArenaWorldResult extends OperationResult {
        public UnloadArenaWorldResult(boolean success, @org.jetbrains.annotations.Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static UnloadArenaWorldResult success() {
            return new UnloadArenaWorldResult(true, null);
        }

        public static UnloadArenaWorldResult failure(Component displayMessage) {
            return new UnloadArenaWorldResult(false, displayMessage);
        }
    }
}
