package xyz.xiaocan.doorsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import xyz.xiaocan.configload.option.Card;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.configload.option.DoorTemplate;
import xyz.xiaocan.scpitemstacks.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class Door {

    private final String id;
    private Location origin;
    private final List<Block> doorBlocks;
    private DoorState state;
    private int width, height, depth;
    private DoorTemplate DoorTemplate;
    private double moveDistance;
    private BoundingBox collisionBox;
    private final Material doorMaterial = Material.IRON_BLOCK;
    private final List<Location> currentDisplayLocations = new ArrayList<>(); // 记录每个BlockDisplay的当前位置
    private final Vector direction;
    private Door linkDoor;
    private DoorLinkType DoorLinkType;
    private TextDisplay textDisplay;

    //可以不用序列化的数据
    private List<BlockDisplay> movingDisplays = new ArrayList<>();  //默认状态为关闭，没有displays
    private Vector moveDirection;
    private int animationTime;      //tick
    private int disappearTime;        //tick
    private int appearTime;       //tick
    private List<Integer> permissionLevels;

    public Door(String id, Location origin, int width, int height,
                int depth, double moveDistance, DoorTemplate DoorTemplate, Vector direction) {
        this.id = id;
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.state = DoorState.CLOSED;
        this.depth = depth;
        this.doorBlocks = new ArrayList<>();
        this.moveDistance = moveDistance;
        this.DoorTemplate = DoorTemplate;
        this.direction = direction;

        this.animationTime = DoorTemplate.getAnimationTickTime();   //秒转化为tick
        this.disappearTime = DoorTemplate.getBoxDisappearTickTime();  //转化为tick
        this.appearTime = DoorTemplate.getBoxAppearTickTime();       //转化为tick
        this.permissionLevels = DoorTemplate.getPermissionsLevel();
        this.DoorLinkType = DoorLinkType.NONE;
        this.linkDoor = null;

        if(ScpOption.getInstance().isDebug()){
            this.textDisplay = createTextDisplay();
        }

        initializeDoorBlocksAndBoundbox(direction);
    }

    private void initializeDoorBlocksAndBoundbox(Vector direction) {

        double absX = Math.abs(direction.getX());
        double absZ = Math.abs(direction.getZ());

        Location location = origin.clone();
        Location first = null;
        Location second = null;

        if(absX>absZ){  //X轴为深度

            if(direction.getX()>0){ //正方向 +x +z
                //模版
                location.add(1,0,0);
                first = location.clone();
                for (int x=0;x<depth;x++){
                    for (int y=0;y<height;y++){
                        for (int z=0;z<width;z++){
                            Location blockLoc = location.clone().add(x, y, z);
                            doorBlocks.add(blockLoc.getBlock());
                        }
                    }
                }

                moveDirection = new Vector(0,0,1);
                second = location.clone().add(depth, height, width);

            }else{ //负方向  -x,-z

                location.add(0,0,1);
                first = location.clone();
                for (int x=depth;x>0;x--){
                    for (int y=0;y<height;y++){
                        for (int z=width;z>0;z--){
                            Location blockLoc = location.clone().add(-x, y, -z);
                            doorBlocks.add(blockLoc.getBlock());
                        }
                    }
                }

                moveDirection = new Vector(0,0,-1);
                second = location.clone().add(-depth, height, -width);
            }
        }else{ //Z轴为深度

            if(direction.getZ()>0){ //正方向 +z -x
                location.add(1,0,1);
                first = location.clone();
                for (int z=0;z<depth;z++){
                    for (int y=0;y<height;y++){
                        for (int x=width;x>0;x--){
                            Location blockLoc = location.clone().add(-x, y, z);
                            doorBlocks.add(blockLoc.getBlock());
                        }
                    }
                }

                moveDirection = new Vector(-1,0,0);
                second = location.clone().add(-width, height, depth);

            }else{ //负方向 -z +x

                location.add(0,0,0);
                first = location.clone();
                for (int z=depth;z>0;z--){
                    for (int y=0;y<height;y++){
                        for (int x=0;x<width;x++){
                            Location blockLoc = location.clone().add(x, y, -z);
                            doorBlocks.add(blockLoc.getBlock());
                        }
                    }
                }

                moveDirection = new Vector(1,0,0);
                second = location.clone().add(width, height, -depth);
            }
        }

        Bukkit.getLogger().info("first point:" + first + " second point: " + second);
        collisionBox = BoundingBox.of(first, second);
    }
    /**
     *切换门的状态
     */
    public void toggle(Player player) {
        if (state == DoorState.OPENING || state == DoorState.CLOSING) {
            return;
        }

        if(!hasAccess(player)){//没有权限 进行失败的逻辑
            player.sendMessage( ChatColor.RED + "权限不足!");

            try{
                Sound sound = DoorTemplate.getFailSoundEffect();
                origin.getWorld().playSound(origin, sound,
                        2.0f, 0.7f);
            }catch (IllegalArgumentException e){
                origin.getWorld().playSound(origin, Sound.BLOCK_NOTE_BLOCK_BIT, 2.0f, 2f);
                Bukkit.getLogger().warning("门类型 " + DoorTemplate.getId() + " 的声音配置错误: " + DoorTemplate.getFailSoundEffect());
            }

            return;
        }

        if (state == DoorState.CLOSED) {
            open();
            if(linkDoor!=null){
                if(DoorLinkType==DoorLinkType.BOTH){
                    linkDoor.open();
                }else{
                    linkDoor.close();
                }
            }
        } else {
            close();
            if(linkDoor!=null){
                if(DoorLinkType==DoorLinkType.BOTH){
                    linkDoor.close();
                }else{
                    linkDoor.open();
                }
            }
        }

    }
    public void open() {
        if(state==DoorState.OPEN)return;

        try{
            Sound sound = DoorTemplate.getOpenSoundEffect();
            origin.getWorld().playSound(origin, sound,
                    1.5f, 1.2f);
        }catch (IllegalArgumentException e){
            origin.getWorld().playSound(origin, Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 2f);
            Bukkit.getLogger().warning("门类型 " + DoorTemplate.getId() + " 的声音配置错误: " + DoorTemplate.getOpenSoundEffect());
        }

        state = DoorState.OPENING;
        createMovingDisplays();
        animateDoor(true);
    }
    public void close() {
        if(state==DoorState.CLOSED)return;

        try{
            Sound sound = DoorTemplate.getClosedSoundEffect();
            origin.getWorld().playSound(origin, sound,
                    1.5f, 0.8f);
        }catch (IllegalArgumentException e){
            origin.getWorld().playSound(origin, Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 2f);
            Bukkit.getLogger().warning("门类型 " + DoorTemplate.getId() + " 的声音配置错误: " + DoorTemplate.getClosedSoundEffect());
        }

        state = DoorState.CLOSING;
        animateDoor(false);
    }
    private void createMovingDisplays() {
        clearMovingDisplays();
        currentDisplayLocations.clear();

        //在每一个组成大门的方块下面生成一个DisPlay实体
        for (Block block : doorBlocks) {
            Location spawnLoc = block.getLocation();
            BlockDisplay display = block.getWorld().spawn(spawnLoc, BlockDisplay.class);
            display.setBlock(block.getBlockData());
            movingDisplays.add(display);
            currentDisplayLocations.add(spawnLoc.clone());

            block.setType(Material.BARRIER); //原来位置设置为屏障，玩家看不到但是有碰撞盒
        }
    }
    public void showDoorBlocks() {
        for (Block block : doorBlocks) {
            block.setType(doorMaterial);
        }
    }
    public void showDoorBoundBox(){
        for (Block block : doorBlocks) {
            block.setType(Material.BARRIER);
        }
    }
    public void clearDoorBoundBox(){
        for (Block block : doorBlocks) {
            block.setType(Material.AIR);
        }
    }
    public void clearDoorBlocks() {
        for (Block block : doorBlocks) {
            block.setType(Material.AIR);
        }
        clearMovingDisplays();
    }

    // 序列化为 Map
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", this.id);
        data.put("origin", this.origin);
        data.put("width", this.width);
        data.put("height", this.height);
        data.put("depth", this.depth);
        data.put("distance", this.moveDistance);
        data.put("DoorTemplateId", this.DoorTemplate.getId());
        data.put("direction", this.direction);
        data.put("linkdoor", this.linkDoor != null ? this.linkDoor.getId() : null);
        data.put("DoorLinkType", this.DoorLinkType != null ? this.DoorLinkType.name() : null);
        return data;
    }
    // 从 Map 反序列化
    public static Door deserialize(Map<String, Object> data, DoorManager doorManager) {
        String id = (String) data.get("name");
        Location origin = (Location) data.get("origin");
        int width = (int) data.get("width");
        int height = (int) data.get("height");
        int depth = (int) data.get("depth");
        double distance = (double) data.get("distance");
        String DoorTemplateId = (String) data.get("DoorTemplateId");
        Vector direction = (Vector) data.get("direction");

        String DoorLinkTypeStr = (String) data.get("DoorLinkType");
        DoorLinkType DoorLinkType = null;
        if(DoorLinkTypeStr != null){
            try{
                DoorLinkType = DoorLinkType.valueOf(DoorLinkTypeStr.toUpperCase());
            }catch (IllegalArgumentException e){
                Bukkit.getLogger().warning("无效的链接类型: " + DoorLinkTypeStr);
            }
        }

        String linkDoorStr = (String) data.get("linkdoor");
        Door linkDoor = doorManager.idGetDoor(linkDoorStr);

        DoorTemplate DoorTemplate = doorManager.getDoorTemplate(DoorTemplateId);
        if (DoorTemplate == null) {
            Bukkit.getLogger().warning("无法找到门类型: " + DoorTemplateId + "，门 " + id + " 加载失败");
            return null;
        }

        Door Door = new Door(id, origin, width, height, depth, distance, DoorTemplate, direction);

        //如果linkDoor为null，就不进行链接
        if(linkDoor!=null && DoorLinkType!=DoorLinkType.NONE){
            Door.setLinkEachOther(linkDoor, DoorLinkType);
            linkDoor.setLinkEachOther(Door, DoorLinkType);
        }else{
            Bukkit.getLogger().info("[DoorManager]: door link fail");
        }

        return Door;
    }

    /**
     * 控制门的动画，开启或者关闭
     */
    private void animateDoor(boolean opening) {
        double distance = moveDistance;

        new BukkitRunnable() {
            int step = 0;
            final int totalSteps = animationTime;  //总时间/tick

            @Override
            public void run() {
                if (step >= totalSteps) { //完成移动到最终为止
                    completeAnimation(opening);
                    cancel();
                    return;
                }

                if(step == disappearTime && opening){
                    clearDoorBoundBox();
                }else if(step == appearTime && !opening){
                    showDoorBoundBox();
                }

                double progress = (double) step / totalSteps;

                for (int i = 0; i < movingDisplays.size(); i++) {
                    BlockDisplay display = movingDisplays.get(i);
                    Location originalLoc = doorBlocks.get(i).getLocation();

                    Location targetLoc;
                    if (opening) {
                        targetLoc = originalLoc.clone().add(moveDirection.clone().multiply(distance));
                    } else {
                        targetLoc = originalLoc.clone();
                    }

                    // 平滑插值移动
                    Location currentLoc = currentDisplayLocations.get(i);
                    Location newLoc = interpolateLocation(currentLoc, targetLoc, progress);
                    display.teleport(newLoc);

                    Transformation transformation = display.getTransformation();
                    transformation.getScale().set(0.999f, 1.0f, 0.999f);
                    display.setTransformation(transformation);

                    currentDisplayLocations.set(i, newLoc.clone());
                }

                step++;
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 1L);
    }

    /**
     * 平滑插值
     */
    private Location interpolateLocation(Location start, Location end, double progress) {
        double x = start.getX() + (end.getX() - start.getX()) * progress;
        double y = start.getY() + (end.getY() - start.getY()) * progress;
        double z = start.getZ() + (end.getZ() - start.getZ()) * progress;

        return new Location(start.getWorld(), x, y, z, start.getYaw(), start.getPitch());
    }
    /**
     *  在碰撞盒中心显示粒子效果,可视化，方便点击
     */
    public void displayCollisionBoxParticles(BoundingBox box) {
        World world = origin.getWorld();

        int minX = (int) Math.floor(box.getMinX());
        int minY = (int) Math.floor(box.getMinY());
        int minZ = (int) Math.floor(box.getMinZ());
        int maxX = (int) Math.floor(box.getMaxX());
        int maxY = (int) Math.floor(box.getMaxY());
        int maxZ = (int) Math.floor(box.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location particleLoc = new Location(world, x, y, z);
                    try {
                        world.spawnParticle(Particle.LAVA, particleLoc, 1,
                                0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(0, 0, 255), 1));
                    } catch (Exception e) {
                        world.spawnParticle(Particle.HAPPY_VILLAGER, particleLoc, 1,
                                0, 0, 0, 0);
                    }
                }
            }
        }
    }

    /**
     * 完成动画后的状态转换
     */
    private void completeAnimation(boolean opening) {
        if (opening) {
            state = DoorState.OPEN;
        } else {
            state = DoorState.CLOSED;
            clearMovingDisplays();
            showDoorBlocks();
            currentDisplayLocations.clear();
        }
    }

    //清除BlockDisplay实体
    public void clearMovingDisplays() {
        for (BlockDisplay display : movingDisplays) {
            display.remove();
        }
        movingDisplays.clear();
    }

    //检测是否在碰撞盒内
    public boolean isInDoorBoundingBox(Location location) {
        return collisionBox.contains(location.getX(), location.getY(), location.getZ());
    }

    // 检测玩家是否有权限
    public boolean hasAccess(Player player) {

        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (Card.isCard(handItem)) {
            String cardId = Card.getCardId(handItem);
            if (cardId == null) {
                return false;
            }

            Card card = ItemManager.getInstance().getCard(cardId);
            if (card == null) {
                player.sendMessage(ChatColor.RED + "无效的门禁卡！" + cardId);
                return false;
            }

            List<Integer> cardPermissionsLevels = card.getPermissionsLevel();
            List<Integer> doorPermissionLevels = getPermissionLevels();

            if (cardPermissionsLevels == null || cardPermissionsLevels.size() < 3) {
                player.sendMessage(ChatColor.RED + "门禁卡权限数据不完整！");
                return false;
            }

            if (doorPermissionLevels == null || doorPermissionLevels.size() < 3) {
                player.sendMessage(ChatColor.RED + "门权限数据配置错误！");
                return false;
            }

            return cardPermissionsLevels.get(0) >= doorPermissionLevels.get(0) &&
                    cardPermissionsLevels.get(1) >= doorPermissionLevels.get(1) &&
                    cardPermissionsLevels.get(2) >= doorPermissionLevels.get(2);
        }

        return false;
    }

    //检测是否是大门的一部分
    public boolean isPartOfDoor(Block block) {
        return doorBlocks.contains(block);
    }

    public BoundingBox getCollisionBox(){
        return collisionBox;
    }
    public List<Integer> getPermissionLevels() {
        return permissionLevels;
    }

    public ItemStack createDoorItemStack(){
        ItemStack itemStack = new ItemStack(Material.IRON_DOOR);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + id);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "位置: " + ChatColor.YELLOW + "[" + origin.getBlockX() + "," + origin.getBlockY() + "," + origin.getBlockZ() + "]");
        lore.add(ChatColor.GRAY + "门包含的方块数量: " + ChatColor.YELLOW +doorBlocks.size());
        lore.add(ChatColor.GRAY + "状态: " + ChatColor.YELLOW + state);
        lore.add(ChatColor.GRAY + "宽,高,深度: " + ChatColor.YELLOW + "[" + width + "," + height + "," + depth + "]");
        lore.add(ChatColor.GRAY + "门类型: " + ChatColor.YELLOW +DoorTemplate.getId());
        lore.add(ChatColor.GRAY + "移动距离: " + ChatColor.YELLOW +moveDistance);

        String d = null;
        double x = Math.abs(direction.getX());
        double z = Math.abs(direction.getZ());
        if(x>z){
            d = direction.getX()>0? "+x" : "-x";
        }else{
            d = direction.getZ()>0? "+z" : "-z";
        }

        lore.add(ChatColor.GRAY + "门的打开方向：" + ChatColor.YELLOW + d);
        lore.add(ChatColor.GRAY + "门的权限：" + ChatColor.YELLOW + "[" + permissionLevels.get(0) + "," + permissionLevels.get(1) + "," + permissionLevels.get(2) + "]");
        lore.add(ChatColor.GRAY + "连接的门：" + ChatColor.YELLOW + (linkDoor != null ? linkDoor.getId() : "NONE"));
        lore.add(ChatColor.GRAY + "连接类型：" + ChatColor.YELLOW + DoorLinkType);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    public void setLinkEachOther(Door door2, DoorLinkType DoorLinkType) {
        // 防止自连接
        if (this.equals(door2)) {
            Bukkit.getLogger().warning("[DoorLink] 警告：尝试将门连接到自身: " + this.getId());
            return;
        }

        setLinkDoor(door2);
        setDoorLinkType(DoorLinkType);

        door2.setLinkDoor(this);
        door2.setDoorLinkType(DoorLinkType);

        Bukkit.getLogger().info("[DoorLink] " + this.getId() + " ↔ " + door2.getId() + " (" + DoorLinkType + ")");
    }

    public TextDisplay createTextDisplay(){
        TextDisplay textDisplay = origin.getWorld().spawn(origin.clone().add(0,2,0), TextDisplay.class);
        textDisplay.setText("§f§l" + id);
        textDisplay.setSeeThrough(true);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setDefaultBackground(false);
        textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(150, 0, 0, 0));
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        textDisplay.setTextOpacity((byte) 255);

        return textDisplay;
    }

    public void removeTextDisplay(){
        if(textDisplay==null){
            return;
        }

        textDisplay.remove();
        this.textDisplay = null;
    }

    @Override
    public String toString() {
        return "Door{" +
                "name='" + id + '\'' +
                ", state=" + state +
                ", width=" + width +
                ", height=" + height +
                ", depth=" + depth +
                ", moveDistance=" + moveDistance +
                ", direction=" + direction +
                ", linkDoor=" + linkDoor +
                '}';
    }
}