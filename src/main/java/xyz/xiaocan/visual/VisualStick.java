package xyz.xiaocan.visual;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.scpitemstacks.ItemManager;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class VisualStick {
    private static VisualStick instance;
    private static List<Menu> menus;

    private VisualStick(){
        menus = Arrays.asList(Menu.values());
    }

    public void handleMenuClick(Player player, Menu menu, int slot) {

        if(menu.equals(Menu.MAIN)){
            handleMainMenuClick(player,slot);
        }else if(menu.equals(Menu.CARDS)){
            handleCardsMenuClick(player, Menu.CARDSTYPE);
        }else if(menu.equals(Menu.DOORS)){
            handleDoorsMenuClick(player, slot);
        }else if(menu.equals(Menu.HELPS)){
            handleHelpsMenuClick(player, Menu.HELPS);
        }

    }

    private void handleHelpsMenuClick(Player player, Menu menu) {
        openSubMenu(player, Menu.HELPTIPS);
    }

    private void handleDoorsMenuClick(Player player, int slot) {

        Menu menu = null;
        if(slot==20){
            menu = Menu.DOORSTYPE;
        }else if(slot==22){
            menu = Menu.DOORSINSTANCE;
        }else{
            return;
        }

        openSubMenu(player, menu);
    }

    private void handleCardsMenuClick(Player player, Menu menu) {
        openSubMenu(player, menu);
    }

    private void handleMainMenuClick(Player player, int slot) {
        switch (slot) {
            case 20: //打开卡片列表
                openSubMenu(player, Menu.CARDS);
                break;
            case 22:
                openSubMenu(player, Menu.DOORS);
                break;
            case 24:
                openSubMenu(player, Menu.HELPS);
                break;
        }
    }

    private void openSubMenu(Player player, Menu subMenu) {

        player.openInventory(subMenu.getInventory());
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

    }

    public Menu tryGetMenu(String name){
        for (Menu menu : menus) {
            if(menu.getName().equals(name)){
                return menu;
            }
        }
        return null;
    }

    public Menu tryGetMenu(Inventory inventory){
        for (Menu menu : menus) {
            if(menu.getInventory().equals(inventory)){
                return menu;
            }
        }
        return null;
    }

    /**
     * 尝试获取下级菜单列表
     */
    public List<Menu> tryGetNextMenuList(String name){
        for (Menu menu : menus) {
            //todo
            return null;
        }
        return null;
    }

    /**
     * 尝试获取上一级菜单，做返回用
     */
    public Inventory tryGetLastInventory(String name){
        for(Menu menu: menus){
            if(menu.getName().equals(name) && menu.getLast()!=null){
                return menu.getLast().getInventory();
            }
        }
        return null;
    }

    public static VisualStick getInstance() {
        if(instance==null)instance = new VisualStick();
        return instance;
    }
}
