package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.Categories;
import io.github.hhn756.voidairrace.infrastructure.registry.Registry;
import io.github.hhn756.voidairrace.infrastructure.util.ClassScanner;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * 用于自动注册所有内置地图
 * */
public class MapRegistrar {
    private MapRegistrar() {}

    static void registerMaps() {
        Logger logger = VoidAirRace.getInstance().getLogger();
        Registry registry = Registry.getInstance();
        String mapPackage = "io.github.hhn756.voidairrace.core.map.maps";

        // 定义类别
        registry.createCategory(Categories.MAP);

        // 扫描地图
        Collection<Class<GameMap>> scanResults = ClassScanner.scanSubclasses(GameMap.class, mapPackage);

        // 注册地图
        for (Class<GameMap> mapMeta : scanResults) {
            try {
                //获取构造器
                Constructor<GameMap> constructor = mapMeta.getConstructor();
                // 创建一个实例以便获取元数据
                GameMap mapInst = constructor.newInstance();

                // 注册
                boolean playable = mapInst instanceof PlayableGameMap;
                PlayableGameMap playableMap = playable ? (PlayableGameMap) mapInst : null;
                registry.add(
                        Categories.MAP,
                        new MapMeta(
                                mapInst.getElementMeta(),
                                constructor,
                                playable,
                                playableMap == null ? null : playableMap.maxTeams()
                        )
                );
            } catch (ReflectiveOperationException e) {
                logger.severe("注册地图 “" + mapMeta.getName() + "” 时发生异常：" + e.getMessage());
            }
        }
    }
}
