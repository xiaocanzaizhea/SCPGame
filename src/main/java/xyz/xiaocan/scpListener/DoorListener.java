package xyz.xiaocan.scpListener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import xyz.xiaocan.doorsystem.Door;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.visual.Menu;
import xyz.xiaocan.visual.StickSelectLocation;
import xyz.xiaocan.visual.VisualStick;


public class DoorListener implements Listener {

    private final DoorManager doorManager;
    private final VisualStick debugStickContainer;

    public DoorListener() {
        this.doorManager = DoorManager.getInstance();
        this.debugStickContainer = VisualStick.getInstance();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();

        //检测开门逻辑
        if(event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK){
            //右键方块或者空气

            Door clickedDoor = tryGetTargetDoor(player);
            if(clickedDoor!=null){
                clickedDoor.toggle(player);

            }else{
                //未查询到碰撞盒id，尝试获取方块
                Block block = event.getClickedBlock();
                if(block != null){

                    clickedDoor = doorManager.isPartOfAnyDoor(block);
                    if(clickedDoor!=null){
                        clickedDoor.toggle(player);
                    }
                }
            }
        }
    }

    /**
     * 获取范围以寻找碰撞盒
     */
    private Door tryGetTargetDoor(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        double step = 0.1f;
        double total = 3f;
        int steps = (int) (total / step);
        for (int i = 0; i <= steps; i++) {
            double distance = step * i;
            Location checkLocation = eyeLocation.clone().
                    add(direction.clone().multiply(distance));

            String id = doorManager.isInBoundingBox(checkLocation);
            if(id!=null){
                return doorManager.idGetDoor(id);
            }
        }

        return null;
    }

    @EventHandler
    public void onPlayerClickBlock(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(event.getHand()!= EquipmentSlot.HAND){
            return;
        }

        if(!isStick(event.getPlayer().getInventory().getItemInMainHand())){
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null){
            return;
        }

        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            StickSelectLocation.firstLocation = clickedBlock.getLocation();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "第一个点设置成功" + StickSelectLocation.firstLocation);

        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            StickSelectLocation.secondLocation = clickedBlock.getLocation();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "第二个点设置成功" + StickSelectLocation.secondLocation);
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        ItemStack itemDrop = event.getItemDrop().getItemStack();
        Player player = event.getPlayer();

        if(!isStick(itemDrop)){
            return;
        }

        event.setCancelled(true);

        Menu main = debugStickContainer.tryGetMenu("main");  //尝试获取主菜单进入
        if(main==null){
            player.sendMessage("§c菜单未初始化，请联系管理员");
            return;
        }

        player.sendMessage(ChatColor.BLUE + "MenuSize: " + ChatColor.GRAY + Menu.values().length);
        player.openInventory(main.getInventory());
    }

    /**
     * 这个事件处理我们在菜单点击的操作
     */
    @EventHandler
    public void onPlayerClickInInventory(InventoryClickEvent event){

        Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory==null){
            return;
        }

        Menu menu = debugStickContainer.tryGetMenu(clickedInventory);
        if(menu==null){ //菜单列表不包含这个菜单
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

//        player.sendMessage(menu.getName());
        debugStickContainer.handleMenuClick(player, menu, slot);   //处理打开菜单逻辑
    }

    public boolean isStick(ItemStack item){
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getDisplayName().equals(ChatColor.DARK_PURPLE + "调试棒(๑•̀ㅂ•́)و✧");
    }

}
