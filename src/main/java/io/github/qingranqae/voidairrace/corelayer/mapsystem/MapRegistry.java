package io.github.qingranqae.voidairrace.corelayer.mapsystem;

import io.github.qingranqae.voidairrace.corelayer.mapsystem.maps.grassland.GrassLand;
import io.github.qingranqae.voidairrace.corelayer.mapsystem.maps.lobby.Lobby;
import io.github.qingranqae.voidairrace.exception.map.MapNotPlayableException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapRegistry {
    private static MapRegistry instance;

    public static MapRegistry getInstance() {
        if (instance == null) {
            instance = new MapRegistry();
        }
        return instance;
    }

    private final HashMap<String, Supplier<GameMap>> maps = new HashMap<>();
    private final HashMap<String, Supplier<GameMap>> playableMaps = new HashMap<>();

    private MapRegistry() {
        // 手动注册地图
        maps.put(new Lobby().getId(), Lobby::new);
        maps.put(new GrassLand().getId(), GrassLand::new);

        // 筛选出可游玩地图
        for (Map.Entry<String, Supplier<GameMap>> i : maps.entrySet()) {
            if (i.getValue().get().isPlayable()) {
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
     * 实例化一个 id 为 `id` 的地图
     *
     * @return 指定地图的新实例
     * */
    public GameMap getMapById(String id) throws MapNotPlayableException {
        Supplier<GameMap> Constructor = maps.get(id);
        if (Constructor == null) {
            throw new MapNotPlayableException("地图 '" + id + "' 不存在，无法实例化");
        }

        GameMap mapInst = Constructor.get();
        return mapInst;
    }

    public boolean containsMap(String id) {
        return maps.containsKey(id);
    }
}
