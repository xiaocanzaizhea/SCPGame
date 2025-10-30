package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.scpitemstacks.grenade.AbstractGrenade;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Grenade extends AbstractGrenade {
    private static Grenade instance;

    private double damage;
    public Grenade(double damage, double radius, double explosionTime, String id, String displayName) {
        super(radius, explosionTime, id ,displayName);
        this.damage = damage;
    }
    @Override
    public void use() {
        System.out.println("Grenade exploded with " + damage + " damage!");
    }

    @Override
    public String getItemType() {
        return "DamageGrenade";
    }
    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.SNOWBALL);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.RED + "手雷");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "高爆杀伤装置");
        lore.add("");
        lore.add(ChatColor.YELLOW + "特性:");
        lore.add(ChatColor.WHITE + "• 高爆伤害");
        lore.add(ChatColor.WHITE + "• 范围杀伤");
        lore.add(ChatColor.WHITE + "• 延时引爆");

        meta.setLore(lore);

        meta.addEnchant(Enchantment.DENSITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public String toString() {
        return "Grenade{" +
                "damage=" + damage +
                ", id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", radius=" + radius +
                ", explosionTime=" + explosionTime +
                '}';
    }

    public static Grenade getInstance(){
        if(instance ==null){
            Bukkit.getLogger().warning("手雷为空");
            return null;
        }
        return instance;
    }

    public static void setInstance(Grenade grenade){
        instance = grenade;
    }
}
