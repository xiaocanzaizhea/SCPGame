package xyz.xiaocan.doorsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import xyz.xiaocan.configload.option.DoorTemplate;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.visual.Menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DoorManager {
    private static Map<String, Door> doors = new HashMap<>(); //存储所有的门的实例
    private Map<String, DoorTemplate> DoorTemplates = new HashMap<>(); //存储门的类型模版
    private Map<String, BukkitTask> doorParticleTask = new HashMap<>();  //碰撞盒可视化任务
    private static Map<String, BoundingBox> boundingBoxMap = new HashMap<>(); //门实例id对应的boundbox
    private static DoorManager instance;
    private DoorManager() {}

    public static DoorManager getInstance(){
        if(instance==null)instance = new DoorManager();
        return instance;
    }

    public void test(){
        Bukkit.getLogger().info("============门实例============");
        Bukkit.getLogger().info("门实例大小" + doors.size());
        for (Door door:doors.values()) {
            Bukkit.getLogger().info(door.toString());
        }
    }

    /**
     * 加载门的种类,然后存放进map  ,方法暂时弃用
     * config为全局配置文件
     */

    public void loadDoorTemplate(FileConfiguration config, boolean isCommand){
        DoorTemplates.clear();

        if(!config.contains("door")){
            Bukkit.getServer().getLogger().info( ChatColor.RED + "[DoorSystem] " + ": can not find doorSetting");
            return;
        }

        for (String name : config.getConfigurationSection("door").getKeys(false)) {

            String path = "door." + name + ".";
            String soundPath = path + "soundeffect.";

            String openSoundEffect = config.getString(soundPath + "open");
            String closedSoundEffect = config.getString(soundPath + "closed");
            String failSoundEffect = config.getString(soundPath + "fail");

            List<Integer> permissionLevels = config.getIntegerList(path + "permissions");
            Double animationTime = config.getDouble(path + "animationtime");
            Double disappearTimeScale = config.getDouble(path + "boxdisappeartime_scale");
            Double appearTimeScale = config.getDouble(path + "boxappeartime_scale");

            DoorTemplate DoorTemplate = new DoorTemplate(name, openSoundEffect,
                    closedSoundEffect, failSoundEffect, permissionLevels, animationTime,
                    disappearTimeScale, appearTimeScale);

            DoorTemplates.put(name, DoorTemplate);
            if(isCommand){
                Menu.DOORSTYPE.setInventory(Menu.createDoorTemplateMenu());  //重新加载门
            }
        }

    }

    public void registerDoor(String id, Location origin, int width, int height, int depth,  //此处id是门的实例唯一标识符,由玩家输入
                             double distance, DoorTemplate DoorTemplate, Player player) {

        Door door = new Door(id, origin, width, height
                , depth, distance, DoorTemplate, player.getLocation().getDirection());

        doors.put(id, door);
        boundingBoxMap.put(id, door.getCollisionBox());
        Menu.DOORSINSTANCE.
                getInventory().addItem(door.createDoorItemStack());//添加进门类实例菜单
        // 立即显示门的方块
        door.showDoorBlocks();

        if(ScpOption.getInstance().isDebug()){
            BukkitTask task = new BukkitRunnable(){
                @Override
                public void run() {
                    door.displayCollisionBoxParticles(door.getCollisionBox());

                }
            }.runTaskTimer(SCPMain.getInstance(),0L, 20L);

            doorParticleTask.put(id, task); //存储任务
            player.sendMessage("碰撞盒可视化开启");
        }

        saveDoors();
        SCPMain.getInstance().getLogger().info("register a new door: " + id);
    }

    // 删除门
    public boolean removeDoor(String id, Player player) {
        Door door = doors.get(id);
        if (door != null) {

            handleDoorConnectionsBeforeRemoval(door);

            // 清除门的所有方块
            door.clearDoorBlocks();
            doors.remove(id);

            if(ScpOption.getInstance().isDebug()){
                if (doorParticleTask.containsKey(id)) {
                    doorParticleTask.get(id).cancel();
                    doorParticleTask.remove(id);
                }
            }

            Menu.DOORSINSTANCE.setInventory(Menu.createDoorInstanceMenu()); //移除门实例菜单
            saveDoors();
            loadDoorsInstance();
            player.sendMessage("§a已删除SCP门: " + id);
            return true;
        }
        return false;
    }

    private void handleDoorConnectionsBeforeRemoval(Door doorToRemove) {
        String doorIdToRemove = doorToRemove.getId();

        // 遍历所有门，检查是否有门连接到要删除的门
        for (Door otherDoor : doors.values()) {
            if (otherDoor.getLinkDoor() != null &&
                    otherDoor.getLinkDoor().getId().equals(doorIdToRemove)) {

                // 断开连接
                otherDoor.setLinkDoor(null);
                otherDoor.setDoorLinkType(DoorLinkType.NONE);

            }
        }
    }

    // 获取所有门ID
    public List<String> getAllDoorId() {
        return new ArrayList<>(doors.keySet());
    }

    public void toggleDoor(String id, Player player) {
        Door door = doors.get(id);
        if (door != null) {
            door.toggle(player);
        }
    }

    //是否是某个门的一部分
    public Door isPartOfAnyDoor(Block block) {
        if(block.getType() != Material.IRON_BLOCK){
            return null;
        }

        for (Door door : doors.values()) {
            if (door.isPartOfDoor(block)) {
                return door;
            }
        }
        return null;
    }

    public Door idGetDoor(String id){
        if(id==null)return null;
        return doors.get(id);
    }

    public String isInBoundingBox(Location location){
        for (Map.Entry<String, BoundingBox> entry : boundingBoxMap.entrySet()) {
            String key = entry.getKey();
            BoundingBox box = entry.getValue();

            if(box.contains(location.getX(), location.getY(),
                    location.getZ())){
                return key;
            }
        }

        return null;
    }

    public void saveDoors() {   //改
        FileConfiguration config = YamlConfiguration.loadConfiguration(
                new File(SCPMain.getInstance().getDataFolder(), "allDoorData.yml"));

        config.set("doors", null);
        for (Map.Entry<String, Door> entry : doors.entrySet()) {
            String path = "doors." + entry.getKey() + ".";
            Door door = entry.getValue();

            // 保存基本门信息
            Map<String, Object> doorData = door.serialize();
            for (Map.Entry<String, Object> dataEntry : doorData.entrySet()) {
                config.set(path + dataEntry.getKey(), dataEntry.getValue());
            }
        }

        try {
            File dateFolder = new File(SCPMain.getInstance().getDataFolder(), "doordata");
            if(!dateFolder.exists()){
                dateFolder.mkdir();
            }

            config.save(new File(dateFolder, "doordata.yml"));
            SCPMain.getInstance().getLogger().info("成功保存 " + doors.size() + " 个门到 allDoorData.yml");
        } catch (IOException e) {
            SCPMain.getInstance().getLogger().severe("保存门数据失败: " + e.getMessage());
        }
    }

    /**
     * 此方法读取文件然后存储门实例
     */
    public void loadDoorsInstance() {
        FileConfiguration config = tryGetAllDoorDataYML();
        if(config==null)return;

        int loadedCount = 0;
        int failedCount = 0;

        for (String name : config.getConfigurationSection("doors").getKeys(false)) {
            String path = "doors." + name + ".";

            try {
                Map<String, Object> doorData = new HashMap<>();
                doorData.put("name", name);
                doorData.put("origin", config.get(path + "origin"));
                doorData.put("width", config.getInt(path + "width"));
                doorData.put("height", config.getInt(path + "height"));
                doorData.put("depth", config.getInt(path + "depth"));
                doorData.put("distance", config.getDouble(path + "distance"));
                doorData.put("DoorTemplateId", config.getString(path + "DoorTemplateId"));
                doorData.put("direction", config.getVector(path + "direction"));
                doorData.put("linkdoor", config.get(path + "linkdoor"));
                doorData.put("DoorLinkType", config.get(path + "DoorLinkType"));

                Door door = Door.deserialize(doorData, this);
                if (door != null) {
                    doors.put(name, door);
                    boundingBoxMap.put(name, door.getCollisionBox());

                    // 重新显示门的方块
                    door.showDoorBlocks();

                    if(ScpOption.getInstance().isDebug()){
                        // 重新启动粒子效果任务
                        BukkitTask task = new BukkitRunnable(){
                            @Override
                            public void run() {
                                door.displayCollisionBoxParticles(door.getCollisionBox());
                            }
                        }.runTaskTimer(SCPMain.getInstance(), 0L, 20L);

                        doorParticleTask.put(name, task);
                    }

                    loadedCount++;
                } else {
                    failedCount++;
                }
            } catch (Exception e) {
                SCPMain.getInstance().getLogger().warning("door load " + name + " fail: " + e.getMessage() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                failedCount++;
            }
        }

        SCPMain.getInstance().getLogger().info("[Main] door load complete: " + loadedCount + " doors were load success, " + failedCount + " doors were load fail" + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public FileConfiguration tryGetAllDoorDataYML(){
        File dateFolder = new File(SCPMain.getInstance().getDataFolder(), "doordata");
        if(!dateFolder.exists()){
            dateFolder.mkdir();
        }

        File doorFile = new File(dateFolder, "doordata.yml");
        if (!doorFile.exists()) {
            SCPMain.getInstance().getLogger().info("门数据文件不存在，跳过加载");
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(doorFile);
        if (!config.contains("doors")) {
            SCPMain.getInstance().getLogger().info("没有找到门数据");
            return null;
        }

        return config;
    }

    public DoorTemplate getDoorTemplate(String DoorTemplateId){
        if(DoorTemplates.containsKey(DoorTemplateId)){
            return DoorTemplates.get(DoorTemplateId);
        }
        return null;
    }

    public Map<String, DoorTemplate> getDoorTemplatesMap() {
        return DoorTemplates;
    }

    public Map<String, Door> getDoors() {
        return doors;
    }

    public void removeAllDoorData(){
        Map<String, Door> doors = getDoors();
        for (Door door: doors.values()) {
            if(door.getState()!= DoorState.CLOSED){
                door.clearMovingDisplays();

            }
            door.removeTextDisplay(); //remove textdisplay
        }

        Bukkit.getLogger().info("门的display模型清理完毕");
    }
}
