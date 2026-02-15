package io.github.qingranqae.voidairrace.mapregistry;

import io.github.qingranqae.voidairrace.mapregistry.maps.GrassLand;
import io.github.qingranqae.voidairrace.mapregistry.maps.Lobby;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MapRegistry {
    private static final HashMap<String, Class<? extends GameMap>> maps = new HashMap<>();
    private static final HashMap<String, Class<? extends GameMap>> playableMaps = new HashMap<>();

    static {
        // 手动注册地图
        maps.put(new Lobby().getId(), Lobby.class);
        maps.put(new GrassLand().getId(), GrassLand.class);

        // 筛选出可游玩地图
        for (Map.Entry<String, Class<? extends GameMap>> i : maps.entrySet()) {
            try {
                if (i.getValue().getDeclaredConstructor().newInstance().isPlayable()) {
                    playableMaps.put(i.getKey(), i.getValue());
                }
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ignored) {
                // 默认不可游玩
            }
        }
    }

    /**
     * 获取所有地图
     *
     * @return `HashMap&lt;String, Class&lt;? extends GameMap&gt;&gt;`键为地图id、值为地图类元数据
     * */
    public static Object getAllMaps() {
        return maps.clone();
    }

    /**
     * 获取所有可作为游戏场地的地图
     *
     * @return `HashMap&lt;String, Class&lt;? extends GameMap&gt;&gt;`键为地图id、值为地图类元数据
     * */
    public static Object getAllPlayableMaps() {
        return playableMaps.clone();
    }

    public static Class<? extends GameMap> getMapById(String id) {
        return maps.get(id);
    }
}
