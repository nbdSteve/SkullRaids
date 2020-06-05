package gg.steve.mc.skullwars.raids.core.utils;

import java.util.ArrayList;
import java.util.List;

public class PointUtil {
    private int x, z;

    public PointUtil(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public List<PointUtil> getNeighbors() {
        List<PointUtil> neighbors = new ArrayList<>();
        neighbors.add(new PointUtil(x - 1, z));
        neighbors.add(new PointUtil(x + 1, z));
        neighbors.add(new PointUtil(x, z - 1));
        neighbors.add(new PointUtil(x, z + 1));
        return neighbors;
    }
}