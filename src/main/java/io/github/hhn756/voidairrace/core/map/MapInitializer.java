package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.Categories;
import io.github.hhn756.voidairrace.infrastructure.config.Config;
import io.github.hhn756.voidairrace.infrastructure.config.YamlConfig;
import io.github.hhn756.voidairrace.infrastructure.config.files.PublicFiles;
import io.github.hhn756.voidairrace.infrastructure.registry.Registry;
import io.github.hhn756.voidairrace.infrastructure.util.schedulingutil.SchedulingUtil;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapInitializer {
    private static MapInitializer instance;

    static void load() {
        instance = new MapInitializer();
    }

    static void unload() {
        instance = null;
    }

    public static MapInitializer getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("地图初始化器实例不存在");
        return instance;
    }

    // ------

    private final VoidAirRace mainClass;
    private final YamlConfig flagConfig;
    private final Logger logger;

    private MapInitializer() {
        flagConfig = Config.getInstance().getYmlConfig(PublicFiles.FLAGS);
        this.mainClass = VoidAirRace.getInstance();
        logger = mainClass.getLogger();
    }

    /**
     * 异步初始化所有未初始化的地图（按顺序）<br>
     * 该方法立即返回，初始化在后台进行
     */
    public void initAllMapsAsync() {
        // 获取要初始化的地图
        Collection<MapEntry> maps = Registry.getInstance().category(Categories.MAP).list();

        // 初始化任务
        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

        for (MapEntry meta : maps) {
            NamespacedKey mapId = meta.getElementMeta().id();
            if (isInited(mapId)) continue;

            // 链接初始化任务，确保顺序执行
            chain = chain.thenCompose(ignored -> initOneMap(mapId));
        }

        chain.thenRun(() -> logger.fine("所有地图初始化流程结束"));
    }

    /**
     * 重新初始化指定地图（异步）
     *
     * @param mapId 目标地图的 ID
     *
     * @return 表示初始化完成的 future
     *
     * @throws IllegalArgumentException 当指定地图不存在时抛出
     */
    public CompletableFuture<Void> reinitMap(NamespacedKey mapId) throws IllegalArgumentException {
        GameMap mapInst = Registry.getInstance().category(Categories.MAP).get(mapId).newInstance();

        // 如果地图之前已初始化，先执行同步清理
        if (isInited(mapId)) {
            mapInst.unInit();
            // 立即清除标志（即使 unInit 可能耗时，但它是同步的）
            setInitFlag(mapId, false);
        }

        // 执行异步初始化并返回 future
        return initOneMap(mapId);
    }

    /**
     * 初始化单张地图（异步），不检查当前是否已初始化<br>
     * 调用者应确保适当的时机调用（例如在 initAllMapsAsync 中已过滤未初始化地图在 reinitMap 中已清除标志）
     *
     * @param mapId 地图 ID
     *
     * @return 表示初始化完成的 future
     */
    private CompletableFuture<Void> initOneMap(@NonNull NamespacedKey mapId) {
        GameMap mapInst = Registry.getInstance().category(Categories.MAP).get(mapId).newInstance();
        try {
            return mapInst.initAsync(mainClass)
                    .thenRunAsync(() -> {
                                setInitFlag(mapId, true);
                                logger.fine("地图 " + mapId + " 初始化完成");
                            },
                            SchedulingUtil::runOnMainThread
                    )
                    .exceptionally(throwable -> {
                        logger.log(
                                Level.SEVERE,
                                "地图 " + mapId + " 初始化失败: " + throwable.getMessage(),
                                throwable);
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
    public boolean isInited(NamespacedKey mapId) {
        return flagConfig.getBoolean(mapIdToFlagPath(mapId), false);
    }

    /**
     * 修改地图初始化状态
     * 注：这只会修改标记而不会进行初始化操作
     */
    public void setInitFlag(NamespacedKey mapId, boolean initFlag) {
        flagConfig.set(mapIdToFlagPath(mapId), initFlag);
    }

    /**
     * @param mapId 指定地图的ID
     *
     * @return 用于标识指定地图初始化状态的配置键的路径
     * */
    private String mapIdToFlagPath(NamespacedKey mapId) {
        return "mapInitState." + mapId;
    }
}
