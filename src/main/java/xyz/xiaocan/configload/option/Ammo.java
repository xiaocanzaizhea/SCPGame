package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.scpitemstacks.NeedCreateItem;
import xyz.xiaocan.scpitemstacks.gun.GunManager;

@Getter
@Setter
public class Ammo implements NeedCreateItem {
    private String id;
    private String displayName;
    private int maxAmmoTake;
    private double maxDistance;

    public Ammo(String id, String displayName,
                int maxAmmoTake, double maxDistance) {
        this.id = id;
        this.displayName = displayName;
        this.maxAmmoTake = maxAmmoTake;

        this.maxDistance = maxDistance;
    }

    @Override
    public String toString() {
        return "Ammo{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", maxAmmoTake=" + maxAmmoTake +
                ", maxDistance=" + maxDistance +
                '}';
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.OAK_BUTTON);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);



        return itemStack;
    }
}
