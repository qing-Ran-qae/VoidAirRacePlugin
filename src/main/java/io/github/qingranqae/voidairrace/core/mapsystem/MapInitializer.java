package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.infrastructure.util.schedulingutil.SchedulingUtil;
import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapInitializer {
    private static MapInitializer instance;

    public static MapInitializer getInstance(JavaPlugin mainClass) {
        if (instance == null) instance = new MapInitializer(mainClass);
        return instance;
    }

    public static MapInitializer getInstance() {
        if (instance == null) throw new IllegalStateException("地图初始化器 还未初始化，无法获取实例！");
        return instance;
    }

    // ------

    private final ObservableYamlConfiguration flagConfig;
    private final JavaPlugin mainClass;
    private final Logger logger;

    private MapInitializer(JavaPlugin mainClass) {
        flagConfig = Config.getInstance().getConfig(PublicFiles.FLAGS);
        this.mainClass = mainClass;
        logger = mainClass.getLogger();
    }

    /**
     * 异步初始化所有未初始化的地图（按顺序）。
     * 该方法立即返回，初始化在后台进行。
     */
    public void initAllMapsAsync() {
        HashMap<String, Supplier<GameMap>> maps = MapRegistry.getInstance().getAllMaps();
        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

        for (Map.Entry<String, Supplier<GameMap>> entry : maps.entrySet()) {
            String mapId = entry.getKey();
            if (isInited(mapId)) continue;

            // 链接初始化任务，确保顺序执行
            chain = chain.thenCompose(ignored -> initOneMap(mapId));
        }

        chain.thenRun(() -> logger.fine("所有地图初始化流程结束"));
    }

    /**
     * 重新初始化指定地图（异步）。
     *
     * @param mapId 目标地图的 ID
     * @return 表示初始化完成的 future
     * @throws IllegalArgumentException 当指定地图不存在时抛出
     */
    public CompletableFuture<Void> reinitMap(String mapId) throws IllegalArgumentException {
        GameMap mapInst = MapRegistry.getInstance().getMapById(mapId);
        if (mapInst == null) {
            throw new IllegalArgumentException("地图 " + mapId + " 不存在");
        }

        // 如果地图之前已初始化，先执行同步清理
        if (isInited(mapId)) {
            mapInst.unInit(mainClass);
            // 立即清除标志（即使 unInit 可能耗时，但它是同步的）
            setInitFlag(mapId, false);
        }

        // 执行异步初始化并返回 future
        return initOneMap(mapId);
    }

    /**
     * 初始化单张地图（异步），不检查当前是否已初始化。
     * 调用者应确保适当的时机调用（例如在 initAllMapsAsync 中已过滤未初始化地图，
     * 在 reinitMap 中已清除标志）。
     *
     * @param mapId 地图 ID
     * @return 表示初始化完成的 future
     */
    private CompletableFuture<Void> initOneMap(String mapId) {
        GameMap mapInst = MapRegistry.getInstance().getMapById(mapId);
        if (mapInst == null) {
            // 这种情况理论上不会发生，因为调用前已检查
            return CompletableFuture.failedFuture(new IllegalArgumentException("地图 " + mapId + " 不存在"));
        }

        try {
            return mapInst.initAsync(mainClass)
                    .thenRunAsync(() -> {
                                setInitFlag(mapId, true);
                                logger.fine("地图 " + mapId + " 初始化完成");
                            },
                            SchedulingUtil::runOnMainThread
                    )
                    .exceptionally(throwable -> {
                        logger.log(Level.SEVERE, "地图 " + mapId + " 初始化失败: " + throwable.getMessage(), throwable);
                        // 失败时不设置标志，下次重启或手动重试会再次尝试
                        return null;
                    });
        } catch (NullPointerException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 获取地图初始化状态
     *
     * @return `true`表示已初始化，`false`表示未初始化
     */
    public boolean isInited(String mapId) {
        return flagConfig.getBoolean(mapIdToFlagPath(mapId), false);
    }

    /**
     * 修改地图初始化状态
     * 注：这只会修改标记而不会进行初始化操作
     */
    public void setInitFlag(String mapId, boolean initFlag) {
        flagConfig.set(mapIdToFlagPath(mapId), initFlag);
    }

    private String mapIdToFlagPath(String mapId) {
        return "mapInitState." + mapId;
    }
}