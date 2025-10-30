package xyz.xiaocan.dropitemsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class DropManager {
    private static DropManager instance;
    private final Map<UUID, DropItem> dropItemMap = new HashMap<>(); // Interaction UUID -> DropItem

    public static DropManager getInstance() {
        if (instance == null) {
            instance = new DropManager();
        }
        return instance;
    }
    public Map<UUID, DropItem> getDropItemMap() {
        return dropItemMap;
    }
    /**
     * 创建掉落物品
     */
    public void createDropItem(ItemStack item, Location location) {
        DropItem dropItem = DropItem.create(item, location);
        dropItemMap.put(dropItem.getInteractionUUID(), dropItem);
    }
    /**
     * 处理物品拾取
     */
    public void handleItemPickup(Player player, Interaction interaction) {
        DropItem dropItem = dropItemMap.get(interaction.getUniqueId());
        if (dropItem != null) {
            player.getInventory().addItem(dropItem.getItemStack().clone());

            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);

            removeDropItem(dropItem);
        }
    }
    private void removeDropItem(DropItem dropItem) {
        dropItemMap.remove(dropItem.getInteractionUUID());
        dropItem.remove();
    }
    public void removeAllDropItem(){
        for (DropItem dropItem: dropItemMap.values()) {
            dropItem.remove();
        }
        dropItemMap.clear();
    }
    public void test(){
        Bukkit.getLogger().info("当前掉落物数量: " + dropItemMap.size());
        for (DropItem dropItem: dropItemMap.values()) {
            Bukkit.getLogger().info(dropItem.toString());
        }
    }
}
