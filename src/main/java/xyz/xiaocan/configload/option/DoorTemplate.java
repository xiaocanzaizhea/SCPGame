package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
/*
    处理从配置文件中读取到的数据
 */
public class DoorTemplate {
    private String id;
    private Sound openSoundEffect;
    private Sound closedSoundEffect;
    private Sound failSoundEffect;

    private List<Integer> permissionsLevel; //三类权限需要的最低等级

    //动画和碰撞盒时间，单位为tick
    private int animationTickTime;
    private int boxDisappearTickTime;
    private int boxAppearTickTime;

    public DoorTemplate(String id, String openSoundEffect, String closedSoundEffect,
                    String failSoundEffect, List<Integer> permissionsLevel, double animationTime,
                    Double boxDisappearTimeScale, Double boxAppearTimeScale) {
        this.id = id;
        this.openSoundEffect = Sound.valueOf(openSoundEffect.toUpperCase());
        this.closedSoundEffect = Sound.valueOf(closedSoundEffect.toUpperCase());
        this.failSoundEffect = Sound.valueOf(failSoundEffect.toUpperCase());
        this.permissionsLevel = permissionsLevel;
        this.animationTickTime = (int) (animationTime * 20);   //转化为tick单位
        this.boxDisappearTickTime = (int) (this.animationTickTime * boxDisappearTimeScale);
        this.boxAppearTickTime = (int) (this.animationTickTime * boxAppearTimeScale);
    }

    public ItemStack createDoorItemStack(){
        ItemStack itemStack = new ItemStack(Material.IRON_DOOR);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + id);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "gate permission: " + ChatColor.YELLOW + permissionsLevel.get(0));
        lore.add(ChatColor.GRAY + "door permission: " + ChatColor.YELLOW + permissionsLevel.get(1));
        lore.add(ChatColor.GRAY + "weapon permission: " + ChatColor.YELLOW + permissionsLevel.get(2));
        lore.add(ChatColor.GRAY + "OpenSoundEffect: " + ChatColor.YELLOW + openSoundEffect);
        lore.add(ChatColor.GRAY + "ClosedSoundEffect: " + ChatColor.YELLOW + closedSoundEffect);
        lore.add(ChatColor.GRAY + "FailSoundEffect: " + ChatColor.YELLOW + failSoundEffect);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public String toString() {
        return "DoorTemplate{" +
                "id='" + id + '\'' +
                ", openSoundEffect=" + openSoundEffect +
                ", closedSoundEffect=" + closedSoundEffect +
                ", failSoundEffect=" + failSoundEffect +
                ", permissionsLevel=" + permissionsLevel +
                ", animationTickTime=" + animationTickTime +
                ", boxDisappearTickTime=" + boxDisappearTickTime +
                ", boxAppearTickTime=" + boxAppearTickTime +
                '}';
    }
}
