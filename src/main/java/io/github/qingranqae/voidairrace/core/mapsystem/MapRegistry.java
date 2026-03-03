package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.VoidAirRace;
import io.github.qingranqae.voidairrace.util.ClassScanner;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class MapRegistry {
    private static MapRegistry instance;

    /**
     * 获取地图注册表实例
     *
     * @return 地图注册表实例
     *
     * @throws IllegalStateException 在此类初始化前尝试获取实例
     * */
    public static MapRegistry getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("地图注册表还未初始化，无法获取实例");
        }
        return instance;
    }

    /**
     * 初始化（实例化）地图注册表并获取实例
     *
     * @return 地图注册表实例
     * */
    public static MapRegistry getInstance(VoidAirRace mainClass) {
        if (instance == null) {
            instance = new MapRegistry(mainClass);
        }
        return instance;
    }

    // ------

    private final HashMap<String, Supplier<GameMap>> maps = new HashMap<>();
    private final HashMap<String, Supplier<GameMap>> playableMaps = new HashMap<>();
    private final Logger logger;

    private MapRegistry(VoidAirRace mainClass) {
        this.logger = mainClass.getLogger();

        // 自动注册地图
        List<Class<? extends GameMap>> scanResults = ClassScanner.scanSubclasses(mainClass, GameMap.class, "io.github.qingranqae.voidairrace.core.mapsystem.maps");
        GameMap mapInst;
        for (Class<? extends GameMap> mapMeta : scanResults) {
            try {
                // 先创建一个实例获取 id
                mapInst = mapMeta.getConstructor().newInstance();
                // 存储一个工厂函数，该函数返回新的实例
                maps.put(mapInst.getId(), () -> {
                    try {
                        return mapMeta.getConstructor().newInstance();
                    }
                    catch (Exception e) {
                        throw new RuntimeException("创建地图实例失败", e);
                    }
                });
            } catch (ReflectiveOperationException e) {
                logger.severe("注册地图 “" + mapMeta.getName() + "” 时发生异常：" + e.getMessage());
            }
        }

        // 筛选出可游玩地图
        for (Map.Entry<String, Supplier<GameMap>> i : maps.entrySet()) {
            if (i.getValue().get() instanceof PlayableGameMap) {
                playableMaps.put(i.getKey(), i.getValue());
            }
        }
    }

    /**
     * 获取所有地图
     *
     * @return 键为地图 ID，值为地图构造器
     * */
    public HashMap<String, Supplier<GameMap>> getAllMaps() {
        return new HashMap<>(maps);
    }

    /**
     * 获取所有地图
     *
     * @return 键为地图 ID，值为地图构造器
     * */
    public HashMap<String, Supplier<GameMap>> getAllPlayableMaps() {
        return new HashMap<>(playableMaps);
    }

    /**
     * 实例化一个 id 为 {@code id} 的地图
     *
     * @return 指定地图的新实例
     * @throws IllegalArgumentException 不存在 id 为 {@code id} 的地图时抛出
     * */
    @NonNull
    public GameMap getMapById(String id) throws IllegalArgumentException {
        Supplier<GameMap> Constructor = maps.get(id);
        if (Constructor == null) {
            throw new IllegalArgumentException("地图 '" + id + "' 不存在，无法实例化");
        }
        return Constructor.get();
    }

    /**
     * 检查指定 id 的地图是否已注册
     *
     * @return 地图注册状态。{@code true}表示已注册，{@code false}表示未注册
     * */
    public boolean containsMap(String id) {
        return maps.containsKey(id);
    }
}
