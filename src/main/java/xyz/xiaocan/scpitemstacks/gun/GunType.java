package xyz.xiaocan.scpitemstacks.gun;

import org.bukkit.Bukkit;

public enum GunType {

    COM15("com15"),
    COM18("com18"),
    CROSSVEC("crossvec"),
    COM45("com45");
    private String id;
    private GunType(String id){
        this.id = id;
    }

    public static GunType getByEnumName(String name) {
        try {
            return GunType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().info("cannot find guntype id:" + name);
            return null;
        }
    }
}
