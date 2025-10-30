package xyz.xiaocan.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.configload.option.Card;
import xyz.xiaocan.configload.option.DoorTemplate;
import xyz.xiaocan.doorsystem.DoorLinkType;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.doorsystem.Door;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.visual.Menu;
import xyz.xiaocan.visual.StickSelectLocation;

import java.util.*;

public class DoorCommand implements CommandExecutor, TabExecutor {
    private final DoorManager doorManager;
    private final ItemManager itemManager;
    private SCPMain plugin;
    public DoorCommand() {
        this.doorManager = DoorManager.getInstance();
        this.itemManager = ItemManager.getInstance();
        this.plugin = SCPMain.getInstance();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用此命令！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreateCommand(player, args);
                break;

            case "reloaddoor":
                handleReload(player, args);
                break;

            case "getcards":
                handleGetCards(player, args);
                break;

            case "getstick":
                handleGetStick(player, args);
                break;

            case "toggle":
                handleToggleCommand(player, args);
                break;

            case "delete": //测试方法
                handleDeleteCommand(player, args);
                break;

            case "list":  //所有门
                handleListCommand(player);
                break;

            case "link":
                handleLinkDoor(player, args);
                break;

            case "test":
                handleTestCommand(player);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleLinkDoor(Player player, String[] args) {
        if(args.length==4){
            String name = args[1];
            String linkedname = args[2];
            String linktype = args[3].toUpperCase();

            try{
                DoorLinkType linkType = DoorLinkType.valueOf(linktype);
                Door door = doorManager.idGetDoor(name);
                Door linkeddoor = doorManager.idGetDoor(linkedname);

                if(door == null || linkeddoor == null){
                    player.sendMessage(ChatColor.RED + "错误：找不到指定的门！请检查门ID是否正确。");
                    return;
                }

                // 检查是否连接到自己
                if(name.equals(linkedname)) {
                    player.sendMessage(ChatColor.RED + "错误：不能将门连接到自身！");
                    return;
                }

                // 检查是否已经连接
                if(door.getLinkDoor() != null && door.getLinkDoor().getId().equals(linkedname)) {
                    player.sendMessage(ChatColor.YELLOW + "这两个门已经连接了！");
                    return;
                }

                // 设置双向连接
                door.setLinkEachOther(linkeddoor, linkType);

                // 保存连接信息
                doorManager.saveDoors();

                // 更新菜单显示
                updateDoorMenu();

                handleReload(player, new String[]{"1"});
                player.sendMessage(ChatColor.GREEN + "成功连接门: " +
                        ChatColor.AQUA + name + ChatColor.GREEN + " ↔ " +
                        ChatColor.AQUA + linkedname + ChatColor.GREEN + " (" +
                        ChatColor.YELLOW + linktype + ChatColor.GREEN + ")");

                // 在控制台输出详细信息
                Bukkit.getLogger().info("[DoorLink] 玩家 " + player.getName() +
                        " 连接了门: " + name + " ↔ " + linkedname + " (" + linktype + ")");

            }catch (IllegalArgumentException e){
                player.sendMessage(ChatColor.RED + "[DoorCommand] wrong: " +
                        Arrays.toString(DoorLinkType.values()));
            }

        }else{
            sendHelp(player);
        }
    }
    private void handleTestCommand(Player player) {  //测试一些东西的方法
        Location location = player.getEyeLocation();
        String doorName = "doorname";
        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);

        textDisplay.setText("§f§l" + doorName);

        textDisplay.setSeeThrough(true);

        textDisplay.setBillboard(Display.Billboard.CENTER); // 始终面向玩家
        textDisplay.setDefaultBackground(false); // 无背景
        textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(0, 0, 0, 0)); // 完全透明背景
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

        textDisplay.setTextOpacity((byte) 255); // 最大不透明度
    }
    private void handleGetStick(Player player, String[] args) {
        if(args.length==1){
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_PURPLE + "调试棒(๑•̀ㅂ•́)و✧");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "用于快速查看和管理scp的门和卡片系统");
            lore.add(ChatColor.YELLOW + "左键: 定义第一个选取点");
            lore.add(ChatColor.YELLOW + "右键: 定义第二个选取点");
            lore.add(ChatColor.YELLOW + "q: 打开调试棒菜单");
            meta.setLore(lore);
            item.setItemMeta(meta);

            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.GREEN + "成功获取到了调试棒");
        }else{
            sendHelp(player);
        }
    }
    private void handleGetCards(Player player, String[] args) {
        if(args.length==1){
            Set<String> allCardIds = itemManager.getAllCards().keySet();
            if (allCardIds.isEmpty()) {
                player.sendMessage("§c配置文件中没有定义任何卡片！");
                return;
            }

            for (String cardId : allCardIds) {
                Card cardData = itemManager.getAllCards().get(cardId);
                ItemStack card = cardData.createCardItemStack();
                player.getInventory().addItem(card);
            }
            player.sendMessage("§a已获得 " + allCardIds.size() + " 张SCP卡片！");

        }else if(args.length==2){

            String cardId = args[1];
            if (itemManager.getAllCards().containsKey(cardId)) {
                Card cardData = itemManager.getAllCards().get(cardId);
                ItemStack card = cardData.createCardItemStack();
                player.getInventory().addItem(card);
                player.sendMessage("§a已获得卡片: " + cardData.getDisPlayName());
            }
        }else{
            sendHelp(player);
        }
    }
    private void handleReload(Player player, String[] args) {
        if(args.length==1){
            //todo
            /*ItemManager instance = ItemManager.getInstance();
            plugin.saveResource("config.yml",false);

            plugin.reloadConfig();
            FileConfiguration newconfig = plugin.getConfig();

            instance.loadCards(newconfig, true);
            doorManager.loadDoorType(newconfig, true);
            player.sendMessage(ChatColor.GREEN + "插件重载成功");

            //调试信息
            Map<String, DoorType> doorTypesMap = doorManager.getDoorTypesMap();
            player.sendMessage("DoorTypesMap 引用: " + doorTypesMap);
            player.sendMessage("DoorTypesMap 大小: " + doorTypesMap.size());
            player.sendMessage("DoorTypesMap 是否为空: " + doorTypesMap.isEmpty());
            player.sendMessage("DoorTypesMap 键集合: " + doorTypesMap.keySet());

            player.sendMessage("CardTypeMap 大小: " + cardManager.getCardType().size());*/

        }else{
            sendHelp(player);
        }
    }
    private void handleCreateCommand(Player player, String[] args) {
        //scpdoor create id width height depth distance doorTypeId (dir)
        //scpdoor create 1 2 3 4 0.8 doortemp
        if (args.length == 7) {
            try {
                String id = args[1];
                int width = Integer.parseInt(args[2]);
                int height = Integer.parseInt(args[3]);
                int depth = Integer.parseInt(args[4]);
                double distance = Double.parseDouble(args[5]);
                String doorTypeId = args[6];

                DoorTemplate doorType = doorManager.getDoorTemplate(doorTypeId);
                if(doorType == null){
                    player.sendMessage("你输入的key为" + doorTypeId);
                    player.sendMessage(ChatColor.RED + "无法获取到门类型，请去配置文件中配置");
                    return;
                }

                if(doorManager == null){
                    Bukkit.getLogger().info("doormangaer is null");
                    return;
                }

                //scpdoor create id distance doorType player
                //scpdoor create 1 0.8 doortemp
                doorManager.registerDoor(id, player.getLocation(), width, height,
                        depth, distance, doorType, player);

                player.sendMessage("§a成功创建SCP门: " + id);
            } catch (NumberFormatException e) {
                player.sendMessage("§c参数必须是数字！");
            }
        }
        //todo
        //调试棒创建门
        //第二种创建门的方法（推荐）
        //scpdoor create id distance doorTypeId
        else if(args.length==4){
            String doorTypeId = args[1];
            double distance = Double.parseDouble(args[2]);
            String id = args[3];

            DoorTemplate doorType = doorManager.getDoorTemplate(doorTypeId);
            if(doorType == null){
                player.sendMessage("你输入的key为" + doorTypeId);
                player.sendMessage(ChatColor.RED + "无法获取到门类型，请去配置文件中配置");
                return;
            }

            if(doorManager == null){
                Bukkit.getLogger().info("doormangaer is null");
                return;
            }

            Location first = StickSelectLocation.firstLocation;
            Location second = StickSelectLocation.secondLocation;

            if(first==null||second==null){
                player.sendMessage(ChatColor.RED + "先用调试棒定义两个点");
                return;
            }

            Bukkit.getLogger().info(ChatColor.RED+ "[DoorCommand] first point:" + first + " second point: " + second);
            int width,height,depth;
            double absX = Math.abs(player.getLocation().getDirection().getX());
            double absZ = Math.abs(player.getLocation().getDirection().getZ());

            height = Math.abs(first.getBlockY() - second.getBlockY()) + 1;
            if(absX>absZ){ //玩家视线在X轴上
                depth = Math.abs(first.getBlockX()-second.getBlockX()) + 1;
                width = Math.abs(first.getBlockZ()-second.getBlockZ()) + 1;

                if(player.getLocation().getDirection().getX()>0){
                    first.add(-1,0,0);
                }else{
                    first.add(1,0,0);
                }

            }else{ //z轴上
                width = Math.abs(first.getBlockX()-second.getBlockX()) + 1;
                depth = Math.abs(first.getBlockZ()-second.getBlockZ()) + 1;

                if(player.getLocation().getDirection().getZ()>0){
                    first.add(0,0,-1);
                }else{
                    first.add(0,0,1);
                }
            }

            //第二种创造门的指令
            doorManager.registerDoor(id,first, width, height, depth,
                    distance, doorType, player);
        }
        else {
            player.sendMessage("§c用法:       /scpdoor create <id> <width> <height> <depth> <distance> <doorType>");
            player.sendMessage("§c调试棒用法:  /scpdoor create <id> <distance> <doorType>");
        }
    }
    private void handleToggleCommand(Player player, String[] args) {
        if (args.length == 2) {
            doorManager.toggleDoor(args[1], player);
        } else {
            player.sendMessage("§c用法: /scpdoor toggle <id>");
        }
    }
    private void handleDeleteCommand(Player player, String[] args) {
        if (args.length == 2) {
            if (doorManager.removeDoor(args[1], player)) {
                player.sendMessage("§a已删除门: " + args[1]);
            } else {
                player.sendMessage("§c找不到门: " + args[1]);
            }
        } else {
            player.sendMessage("§c用法: /scpdoor delete <id>");
        }
    }
    private void handleListCommand(Player player) {
        List<String> doorIds = doorManager.getAllDoorId();
        if (doorIds.isEmpty()) {
            player.sendMessage("§7没有已创建的SCP门");
        } else {
            player.sendMessage("§6已创建的SCP门:");
            for (String id : doorIds) {
                player.sendMessage("§7- " + id);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("create");
            completions.add("toggle");
            completions.add("delete");
            completions.add("list");
            completions.add("getcards");
            completions.add("reloaddoor");
            completions.add("getstick");
            completions.add("link");
            completions.add("test");
        } else if (args.length == 2 &&
                (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("delete"))) {
            completions.addAll(doorManager.getAllDoorId());
        }

        return completions;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6=== SCP门管理命令 ===");
        player.sendMessage("§6/scpdoor create <name> <宽> <高> <深> <移动距离> <门的类型> - 创建新门");
        player.sendMessage("§6/scpdoor reloaddoor - 重新加载配置文件");
        player.sendMessage("§6/scpdoor getcards - 获取配置文件中的所有卡片");
        player.sendMessage("§6/scpdoor toggle <id> - 切换门状态");
        player.sendMessage("§6/scpdoor delete <id> - 删除门");
        player.sendMessage("§6/scpdoor list - 列出所有门");
        player.sendMessage("§6/scpdoor link A B LinkType - 以LinkType连接A B 两扇门");
        player.sendMessage("§6/scpdoor getstick - 获取调试棒");
    }

    private void updateDoorMenu() {
        Menu.DOORSINSTANCE.setInventory(Menu.createDoorInstanceMenu());

        Bukkit.getLogger().info("[DoorMenu] 门菜单已更新");
    }
}
