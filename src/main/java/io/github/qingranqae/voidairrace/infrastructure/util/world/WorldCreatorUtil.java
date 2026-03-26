package io.github.qingranqae.voidairrace.infrastructure.util.world;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class WorldCreatorUtil {
    private WorldCreatorUtil() {}

    public static World createVoidWorld(String worldName) {
        return new WorldCreator(worldName)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generatorSettings("{\"layers\": [{\"height\": 0}]}")
                .generateStructures(false)
                .createWorld();
    }
}
