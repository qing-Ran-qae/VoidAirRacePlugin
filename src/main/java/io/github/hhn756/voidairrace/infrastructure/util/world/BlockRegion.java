package io.github.hhn756.voidairrace.infrastructure.util.world;

import org.bukkit.Location;

public class BlockRegion {
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    public BlockRegion(int x1, int y1, int z1, int x2, int y2, int z2) {
        // 确保 min <= max
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public boolean contains(Location loc) {
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        return (x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ);
    }

    @Override
    public String toString() {
        return String.format("[point1](%s, %s, %s) [point2](%s, %s, %s)", minX, minY, minZ, maxX, maxY, maxZ);
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }
}
