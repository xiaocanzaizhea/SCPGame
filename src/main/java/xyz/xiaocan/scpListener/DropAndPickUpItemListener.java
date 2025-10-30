package xyz.xiaocan.scpListener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xiaocan.dropitemsystem.DropItem;
import xyz.xiaocan.dropitemsystem.DropManager;

import java.util.Map;
import java.util.UUID;

public class DropAndPickUpItemListener implements Listener {

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        Bukkit.getLogger().info("检测到玩家丢弃物品，生成可视化");
        Item itemDrop = event.getItemDrop();
        Player player = event.getPlayer();

        itemDrop.remove();
        Location location = changeY(player.getLocation(),0);
        DropManager.getInstance().createDropItem(itemDrop.getItemStack(), location);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction)) return;

        Interaction interaction = (Interaction) event.getRightClicked();
        Player player = event.getPlayer();

        DropManager.getInstance().handleItemPickup(player, interaction);
        event.setCancelled(true);
    }

    public Location changeY(Location location, int depth){

        Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if((!block.getType().isAir()) || depth >= 8){
            return new Location(location.getWorld(), location.getX(), location.getBlockY() + 1, location.getZ());
        }

        Location newLocation = new Location(location.getWorld(), location.getX(),
                location.getBlockY() - 1, location.getZ());
        return changeY(newLocation, depth + 1);
    }
}
