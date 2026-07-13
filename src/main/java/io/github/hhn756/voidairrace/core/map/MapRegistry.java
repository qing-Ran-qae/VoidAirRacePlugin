package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.infrastructure.util.ClassScanner;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class MapRegistry {
    private static @Nullable MapRegistry instance;

    static void load() {
        instance = new MapRegistry();
    }

    static void unload() {
        instance = null;
    }

    /**
     * 获取地图注册表实例
     *
     * @return 地图注册表实例
     *
     * @throws NullPointerException 如果地图注册表实例不存在
     * */
    public static @NonNull MapRegistry getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("地图注册表实例不存在");
        return instance;
    }

    // ------

    private final @NonNull HashMap<NamespacedKey, Supplier<GameMap>> maps = new HashMap<>();
    private final @NonNull HashMap<NamespacedKey, Supplier<GameMap>> playableMaps = new HashMap<>();

    private MapRegistry() {
        Logger logger = VoidAirRace.getInstance().getLogger();

        // 注册地图
        Collection<Class<GameMap>> scanResults = ClassScanner.scanSubclasses(
                GameMap.class,
                "io.github.hhn756.voidairrace.core.map.maps"
        );
        GameMap mapInst;
        for (Class<GameMap> mapMeta : scanResults) {
            try {
                // 先创建一个实例获取 id
                mapInst = mapMeta.getConstructor().newInstance();
                // 存储一个工厂函数，该函数返回新的实例
                maps.put(
                        mapInst.getElementMeta().id(),
                        () -> {
                            try {
                                return mapMeta.getConstructor().newInstance();
                            } catch (Exception e) {
                                throw new RuntimeException("创建地图实例失败", e);
                            }
                        });
            } catch (ReflectiveOperationException e) {
                logger.severe("注册地图 “" + mapMeta.getName() + "” 时发生异常：" + e.getMessage());
            }
        }

        // 筛选出可游玩地图
        for (Map.Entry<NamespacedKey, Supplier<GameMap>> i : maps.entrySet()) {
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
    public @NonNull HashMap<NamespacedKey, Supplier<GameMap>> getAllMaps() {
        return new HashMap<>(maps);
    }

    /**
     * 获取所有地图
     *
     * @return 键为地图 ID，值为地图构造器
     * */
    public @NonNull HashMap<NamespacedKey, Supplier<GameMap>> getAllPlayableMaps() {
        return new HashMap<>(playableMaps);
    }

    /**
     * 实例化一个指定ID的地图
     *
     * @param id 指定地图的ID
     *
     * @return 指定地图的新实例，如果不存在指定 ID 的地图那么返回将{@code null}
     * */
    public @Nullable GameMap CreateMapInstance(NamespacedKey id) {
        Supplier<GameMap> constructor = maps.get(id);
        if (constructor == null) return null;
        return constructor.get();
    }

    /**
     * 检查指定 id 的地图是否已注册
     *
     * @return 地图注册状态。{@code true}表示已注册，{@code false}表示未注册
     * */
    public boolean containsMap(NamespacedKey id) {
        return maps.containsKey(id);
    }
}
