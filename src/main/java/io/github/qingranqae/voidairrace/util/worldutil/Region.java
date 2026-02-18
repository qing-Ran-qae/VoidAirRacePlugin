package io.github.qingranqae.voidairrace.util.worldutil;

import org.bukkit.Location;
import org.bukkit.World;

public class Region {
    private final World world;
    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;

    public Region(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.world = world;
        // 确保 min <= max
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public boolean contains(Location loc) {
        if (!loc.getWorld().getName().equals(world.getName())) return false;
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return (x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ);
    }

    @Override
    public String toString() {
        return String.format("[point1](%f, %f, %f) [point2](%f, %f, %f)", minX, minY, minZ, maxX, maxY, maxZ);
    }
}