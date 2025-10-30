package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.scpitemstacks.grenade.AbstractGrenade;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SmokeGrenade extends AbstractGrenade {
    private static SmokeGrenade instance;
    private double smokeDuration;

    public SmokeGrenade(double smokeDuration, double radius, double explosionTime, String id, String displayName) {
        super(radius, explosionTime,id,displayName);
        this.smokeDuration = smokeDuration;
    }

    @Override
    public void use() {
        System.out.println("Smoke grenade released smoke for " + smokeDuration + " seconds!");
        // 具体烟雾逻辑
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.EGG);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "烟雾弹");

        List<String> lore = Arrays.asList(
                ChatColor.WHITE + "用于视线遮蔽的战术装备",
                ChatColor.BLUE + "投掷后产生持续" + this.smokeDuration + "秒的烟雾"
        );
        meta.setLore(lore);

//        meta.setCustomModelData(2001); // 配合资源包使用

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public String getItemType() {
        return "SmokeGrenade";
    }

    @Override
    public String toString() {
        return "SmokeGrenade{" +
                "smokeDuration=" + smokeDuration +
                ", id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", radius=" + radius +
                ", explosionTime=" + explosionTime +
                '}';
    }

    public static SmokeGrenade getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("烟雾弹设置为空");
            return null;
        }
        return instance;
    }

    public static void setInstance(SmokeGrenade smokeGrenade){
        instance = smokeGrenade;
    }
}
