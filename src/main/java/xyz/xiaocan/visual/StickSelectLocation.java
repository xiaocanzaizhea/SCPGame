package xyz.xiaocan.visual;

import org.bukkit.Location;

public class StickSelectLocation{
    public static Location firstLocation;
    public static Location secondLocation;

    @Override
    public String toString() {
        return new String( "[" + firstLocation.getBlockX() + firstLocation.getBlockY() + firstLocation.getBlockZ() + "]" + "    " +
                "[" + secondLocation.getBlockX() + secondLocation.getBlockY() + secondLocation.getBlockZ() + "]");
    }
}