package xyz.xiaocan.visual;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.configload.option.Card;
import xyz.xiaocan.configload.option.DoorTemplate;
import xyz.xiaocan.doorsystem.Door;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.scpitemstacks.ItemManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum Menu{
    MAIN("main", createMainMenu(), null, null),
    CARDS("cards", createCardsMenu(), MAIN, null),
    CARDSTYPE("cardstype", createCardsTypeMenu(), CARDS, null),
    DOORS("doors", createDoorMenu(), MAIN, null),
    DOORSTYPE("doorstype", createDoorTemplateMenu(), DOORS, null),
    DOORSINSTANCE("doorsinstance", createDoorInstanceMenu(), DOORS, null),
    HELPS("helps", createHelpsMenu(), MAIN, null),
    HELPTIPS("helptips", createHelpTipsMenu(), HELPS, null);

    private String name;
    private Inventory inventory;
    private Menu last;
    private List<Menu> next;

    Menu(String name, Inventory inventory, Menu last, List<Menu> next) {
        this.name = name;
        this.inventory = inventory;
        this.last = last;
        this.next = next;
    }

    static
    {
        MAIN.next = Arrays.asList(CARDS,DOORS,HELPS);
        CARDS.next = Arrays.asList(CARDSTYPE);
        DOORS.next = Arrays.asList(DOORSTYPE);
    }

    private static Inventory createMainMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§l调试棒菜单"));
        setupGlassBorder(menu);

        setMenuItem(menu, 20, Material.PAPER, "§b§l卡片列表", Arrays.asList("§7关于卡片的信息查看", "§a点击打开"), true);
        setMenuItem(menu, 22, Material.IRON_DOOR, "§c§l铁门列表", Arrays.asList("§7关于门信息的查看", "§a点击打开"), true);
        setMenuItem(menu, 24, Material.BOOK, "§d§l使用信息", Arrays.asList("§7查看指令等", "§a点击打开"), true);

        return menu;
    }
    private static Inventory createCardsMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§l卡片菜单"));
        setupGlassBorder(menu);

        setMenuItem(menu, 20, Material.PAPER, "§b§l卡片类型列表", Arrays.asList("§7关于卡片类型", "§a点击打开"), false);

        return menu;
    }
    public static Inventory createCardsTypeMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§l卡片类型列表"));
        setupGlassBorder(menu);

        for (Map.Entry<String, Card> entry : ItemManager.getInstance().getAllCards().entrySet()) {
            Card card = entry.getValue();
            menu.addItem(card.createCardItemStack());
        }

        return menu;
    }
    private static Inventory createDoorMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§l门菜单"));
        setupGlassBorder(menu);

        setMenuItem(menu, 20, Material.IRON_DOOR, "§c§l铁门列表", Arrays.asList("§7关于门类别信息的查看", "§a点击打开"), true);
        setMenuItem(menu, 22, Material.IRON_DOOR, "§c§l铁门实例", Arrays.asList("§7关于门实例的列表", "§a点击打开"), true);

        return menu;
    }
    public static Inventory createDoorTemplateMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§l门类型菜单"));
        setupGlassBorder(menu);

        for (Map.Entry<String, DoorTemplate> entry : DoorManager.getInstance().getDoorTemplates().entrySet()) {
            DoorTemplate doorType = entry.getValue();
            menu.addItem(doorType.createDoorItemStack());
        }
        return menu;
    }
    public static Inventory createDoorInstanceMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§l门实例菜单"));
        setupGlassBorder(menu);

        for (Map.Entry<String, Door> entry : DoorManager.getInstance().getDoors().entrySet()) {
            Door door = entry.getValue();
            ItemStack doorItemStack = door.createDoorItemStack();
            menu.addItem(doorItemStack);
        }

        return menu;
    }
    private static Inventory createHelpsMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§l帮助菜单"));
        setupGlassBorder(menu);

        setMenuItem(menu, 24, Material.BOOK, "§d§l使用信息", Arrays.asList("§7查看指令等,注意事项等", "§a点击打开"), true);

        return menu;
    }
    private static Inventory createHelpTipsMenu() {
        Inventory menu = Bukkit.createInventory(null, 54, new String("§6§lTips菜单"));
        setupGlassBorder(menu);

        setMenuItem(menu, 20, Material.BOOK, "§d§l指令大全", Arrays.asList("                    §6=== SCP门管理命令 ===",
                "§6/scpdoor create <name> <宽> <高> <深> <移动距离> <门的类型> - 创建新门",
                "§6/scpdoor reloaddoor - 重新加载配置文件",
                "§6/scpdoor getcards - 获取配置文件中的所有卡片",
                "§6/scpdoor toggle <id> - 切换门状态",
                "§6/scpdoor delete <id> - 删除门",
                "§6/scpdoor list - 列出所有门",
                "§6/scpdoor link A B LinkType - 以LinkType连接A B 两扇门",
                "§6/scpdoor getstick - 获取调试棒"), true);
        setMenuItem(menu, 22, Material.BOOK, "§d§lTips", Arrays.asList("            §6=== 注意事项 ===   ",
                "§61、使用创建指令或者是调试棒，面前是深度，右边是开门方向", "§62、LinkType包括NONE,BOTH,EITHER三者", "§63、当你的调试棒和门不起作用，很可能是你刚刚修改的地方有误"), true);

        return menu;
    }
    private static void setMenuItem(Inventory menu, int slot, Material material, String name, List<String> lore, boolean enchantment) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(enchantment){
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA,1,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }
    private static void setupGlassBorder(Inventory menu) {
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                menu.setItem(i, glass);
            }
        }
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}