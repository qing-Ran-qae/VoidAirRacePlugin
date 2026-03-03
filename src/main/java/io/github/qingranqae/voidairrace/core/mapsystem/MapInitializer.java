package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.core.config.ConfigFiles;
import io.github.qingranqae.voidairrace.core.config.ObservableYamlConfiguration;

import java.util.HashMap;
import java.util.function.Supplier;

public class MapInitializer {
    private static MapInitializer instance;

    public static MapInitializer getInstance() {
        if (instance == null) {
            instance = new MapInitializer();
        }
        return instance;
    }

    // ------

    private final ObservableYamlConfiguration flagConfig;

    private MapInitializer() {
        flagConfig = Config.getInstance().getConfig(ConfigFiles.FLAGS);
    }

    /**
     * 初始化所有已注册且未初始化的地图
     * */
    public void initAllMaps() {
        HashMap<String, Supplier<GameMap>> maps = MapRegistry.getInstance().getAllMaps();

        for (HashMap.Entry<String, Supplier<GameMap>> entry : maps.entrySet()) {
            GameMap mapInst = entry.getValue().get();

            if (!isInited(mapInst.getId())) {
                mapInst.init();
                setInitFlag(mapInst.getId(), true);
            }
        }
    }

    /**
     * 重新初始化指定地图
     * */
    public void reinitMap(String mapId) {
        GameMap mapInst = MapRegistry.getInstance().getMapById(mapId);
        if (isInited(mapId)) {
            mapInst.unInit();
        }
        mapInst.init();
        setInitFlag(mapInst.getId(), true);
    }

    /**
     * 获取地图初始化状态
     *
     * @return `true`表示已初始化，`false`表示未初始化
     */
    public boolean isInited(String mapId) {
        return flagConfig.getBoolean(mapIdToFlagPath(mapId), false); // 初始化过的地图会设置对应的键值
    }

    /**
     * 修改地图初始化状态
     * 注：这只会修改标记而不会进行初始化操作
     * */
    public void setInitFlag(String mapId, boolean initFlag) {
        flagConfig.set(mapIdToFlagPath(mapId), initFlag);
    }

    private String mapIdToFlagPath(String mapId) {
        return "mapInitState." + mapId;
    }
}
