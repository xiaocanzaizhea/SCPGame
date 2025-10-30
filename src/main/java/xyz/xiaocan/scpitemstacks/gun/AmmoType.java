package xyz.xiaocan.scpitemstacks.gun;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import xyz.xiaocan.configload.option.Ammo;
import xyz.xiaocan.scpitemstacks.ItemManager;

@Getter
public enum AmmoType {

    A919("a919"),
    A556("a556"),
    A762("a762"),
    A12("a12"),
    A444("a444");

    private final String id;

    AmmoType(String id) {
        this.id = id;
    }

    public static AmmoType getByEnumName(String name) {
        try {
            return AmmoType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
