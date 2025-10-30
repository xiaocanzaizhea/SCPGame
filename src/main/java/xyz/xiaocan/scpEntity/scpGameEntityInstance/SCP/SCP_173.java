package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.SCP173SpiecalSetting;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.util;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SCP_173 extends SCPEntity{
    //F键，超高速，左键掐人，shift产生泥巴，右键按住瞬移
    //超高速不可以掐人
    //被看有cd，过了cd就可以瞬移掐人
    private SCP173SpiecalSetting scp173SpiecalSetting;

    private List<BukkitTask> MudTasks = new ArrayList<>();

    private double damage;
    private double radius;  //这个是传送杀人的距离

    private long lastTeleportTime = -1;
    private long lastHighSpeedTime = -1;
    private long lastMudTime = -1;

    private double gazedTime;

    private double highSpeedAdd;
    private double highSpeedDistanceAdd;

    private boolean isGazedBySomeBody;   //用于确定是否可以移动
    private boolean canTeleport;   //用于确定是否可以传送
    private boolean isHighSpeed;

    private double mudDuringTime;
    private double highSpeedDuringTime;

    private double originCD_OF_TELEPORT;
    private double percentOfTeleportTime;

    private double CD_OF_TELEPORT;
    private double CD_OF_MUD;
    private double CD_OF_HIGHSPEED;
    private double MAX_DISTANCE_OF_TELEPORT;

    public SCP_173(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
        giveItems();
        update();

        ArmorManager.getInstance().createSCP173Suit(player);

        scp173SpiecalSetting = SCP173SpiecalSetting.getInstance();

        damage = scp173SpiecalSetting.getDamage();
        radius = scp173SpiecalSetting.getRadius();

        mudDuringTime = scp173SpiecalSetting.getMudDuringTime();

        isGazedBySomeBody = false;
        canTeleport = false;
        isHighSpeed = false;

        highSpeedAdd = scp173SpiecalSetting.getHighSpeedAdd();
        highSpeedDistanceAdd = scp173SpiecalSetting.getHighSpeedDistanceAdd();

        highSpeedDuringTime = scp173SpiecalSetting.getHighSpeedDuringTime();

        CD_OF_MUD = scp173SpiecalSetting.getCdOfMud();
        CD_OF_TELEPORT = scp173SpiecalSetting.getCdOfTeleport();
        CD_OF_HIGHSPEED = scp173SpiecalSetting.getCdOfHighSpeed();
        MAX_DISTANCE_OF_TELEPORT = scp173SpiecalSetting.getTeleportDistance();

        originCD_OF_TELEPORT = CD_OF_TELEPORT;
        percentOfTeleportTime = scp173SpiecalSetting.getPercentOfTeleportTime();
    }

    @Override
    protected void onSpawn() {

    }

    @Override
    public String getDisplayName() {
        return "Scp-173";
    }

    @Override
    public void update() {
        super.update();

        if (expUpdateTask != null) {
            expUpdateTask.cancel();
        }

        expUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateHealthDisplay();
                tryGetPlayerGaze();
            }
        };
        expUpdateTask.runTaskTimer(SCPMain.getInstance(), 0L, 1L);
    }

    @Override
    public void giveItems() {

        super.giveSCPCardsItem();

        player.getInventory().addItem(createEntityItems());
    }

    public ItemStack createEntityItems(){
        ItemStack item2 = new ItemStack(Material.BROWN_MUSHROOM);
        ItemMeta meta2 = item2.getItemMeta();
        List<String> lore2 = new ArrayList<>();
        lore2.add(ChatColor.GOLD + "F键: " + ChatColor.GRAY + "持续一段时间的超高速");
        lore2.add(ChatColor.GOLD + "左键: " + ChatColor.GRAY + "在敌人背后拧断脖子");
        lore2.add(ChatColor.GOLD + "Shift键: " + ChatColor.GRAY + "生成一摊烂泥");
        lore2.add(ChatColor.GOLD + "右键: " + ChatColor.GRAY + "被注释一段时间后瞬移击杀敌人");
        meta2.setLore(lore2);
        meta2.setDisplayName(ChatColor.GREEN + "scp173道具");
        item2.setItemMeta(meta2);

        return item2;
    }

    //此方法处理背后攻击玩家
    public void attack(SCPPlayer scpPlayer){
        GameEntity entity = scpPlayer.getEntity();
        entity.damaged(this.player,damage);
    }

    public void tryGetPlayerGaze(){
        for (Player otherPlayer: Bukkit.getOnlinePlayers()) {
            GameMode gameMode = otherPlayer.getGameMode();
            if(gameMode!=GameMode.ADVENTURE){
                continue;
            }

            Player target = util.findGazeScp173(otherPlayer);

            //只要一个玩家注释scp173,就结束
            if(target==player){
                isGazedBySomeBody = true;    //确定为不可移动

                if(canTeleport==false){
                    gazedTime+=0.05;  //tick转化为秒
                }

                if(gazedTime>=CD_OF_TELEPORT){
                    gazedTime = 0;
                    canTeleport = true;
                }
                return;
            }
        }
        isGazedBySomeBody = false;
    }

    public void createMudPuddle(){
        Location mudLocation = player.getLocation().clone().add(0, 0.1, 0); // 在脚下稍微上方

        util.playSoundEffects(player.getLocation(),Sound.BLOCK_SLIME_BLOCK_PLACE, 1.0f, 0.8f);

        // 创建持续的粒子效果
        BukkitTask particleTask = new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                if (t >= mudDuringTime) {
                    this.cancel();
                    return;
                }

                // 生成棕色粒子（使用BLOCK_CRACK粒子显示棕色混凝土粉末）
                spawnMudParticles(mudLocation);

                // 检测范围内的玩家并给予缓慢效果
                affectPlayersInRange(mudLocation);

                t+=0.25;
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 5L); // 每tick执行一次

        // 存储任务以便后续管理
        MudTasks.add(particleTask);

        player.sendMessage(ChatColor.GREEN + "你在脚下生成了一摊烂泥！");
    }

    /**
        生成圆形粒子
     */
    private void spawnMudParticles(Location location) {
        // 创建圆形区域的粒子效果
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10;
            double x = Math.cos(angle) * 2.5; // 2.5格半径
            double z = Math.sin(angle) * 2.5;

            Location particleLoc = location.clone().add(x, 0, z);

            // 使用BLOCK_CRACK粒子显示棕色混凝土粉末
            location.getWorld().spawnParticle(
                    Particle.BLOCK_MARKER,
                    particleLoc,
                    3, // 数量
                    0.2, 0.1, 0.2, // 偏移
                    0.05, // 速度
                    Material.BROWN_CONCRETE_POWDER.createBlockData()
            );

            // 添加一些水滴粒子增强效果
            location.getWorld().spawnParticle(
                    Particle.DRIPPING_WATER,
                    particleLoc,
                    1,
                    0.1, 0.1, 0.1,
                    0.1
            );
        }

        // 中心区域也生成一些粒子
        location.getWorld().spawnParticle(
                Particle.BLOCK_MARKER,
                location,
                5,
                0.5, 0.1, 0.5,
                0.1,
                Material.BROWN_CONCRETE_POWDER.createBlockData()
        );
    }

    /**
        在粒子范围内的玩家受到减速效果
     */
    private void affectPlayersInRange(Location mudLocation) {
        double radius = 3.0; // 影响范围3格

        for (Player nearbyPlayer : mudLocation.getWorld().getPlayers()) {
            if (nearbyPlayer.equals(player)) {
                continue;
            }

            if (nearbyPlayer.getLocation().distance(mudLocation) <= radius) {
                nearbyPlayer.addPotionEffect(new PotionEffect(
                        PotionEffectType.SLOWNESS,
                        80, // 2秒 * 20 ticks
                        3,  // 等级I
                        true,
                        true,
                        true
                ));

                nearbyPlayer.spawnParticle(
                        Particle.ANGRY_VILLAGER,
                        nearbyPlayer.getLocation().add(0, 1, 0),
                        1
                );
            }
        }
    }

    public Location getGazeCollisionPoint(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        double maxDistance = MAX_DISTANCE_OF_TELEPORT;
        if(isHighSpeed){
            maxDistance += highSpeedDistanceAdd;
        }

        RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(
                eyeLocation,
                direction,
                maxDistance,
                FluidCollisionMode.NEVER,
                true
        );

        if (rayTrace != null && rayTrace.getHitBlock() != null) {
            Location hitLocation = rayTrace.getHitPosition().toLocation(player.getWorld());
            BlockFace hitFace = rayTrace.getHitBlockFace();

            return adjustLocationOutsideBlock(hitLocation, hitFace);
        } else {
            Location location = eyeLocation.add(direction.multiply(maxDistance - 1));
            location = changeY(location,0);
            return location;  //未击中方块
        }
    }

    private Location adjustLocationOutsideBlock(Location hitLocation, BlockFace hitFace) {
        Location adjustedLocation = hitLocation.clone();

        switch (hitFace) {
            case UP:
                adjustedLocation.add(0, 0.1, 0);
                break;
            case DOWN:
                adjustedLocation.subtract(0, 2, 0);
                break;
            case NORTH:
                adjustedLocation.add(0, 0, -0.5);
                break;
            case SOUTH:
                adjustedLocation.add(0, 0, 0.5);
                break;
            case EAST:
                adjustedLocation.add(0.5, 0, 0);
                break;
            case WEST:
                adjustedLocation.add(-0.5, 0, 0);
                break;
            default:
        }

        return adjustedLocation;
    }

    /**
     * 递归查找地面方块，限制递归层数，防止崩溃
     * @param location
     * @param depth
     */
    public Location changeY(Location location, int depth){

        // 检查当前方块坐标位置
        Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        // 如果找到非空气方块或达到最大深度
        if((!block.getType().isAir()) || depth >= 8){
            return new Location(location.getWorld(), location.getX(), location.getBlockY() + 1, location.getZ());
        }

        // 向下移动一个方块
        Location newLocation = new Location(location.getWorld(), location.getX(),
                location.getBlockY() - 1, location.getZ());
        return changeY(newLocation, depth + 1);
    }

    //创建一个只有scp173可以看见的盔甲架
    public ArmorStand createArmorStandMarked(Location location) {
        World world = location.getWorld();

        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location,
                EntityType.ARMOR_STAND);

        armorStand.setVisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setCustomNameVisible(false);

        armorStand.setBasePlate(false);
        armorStand.setArms(true);
        armorStand.setSmall(false);
        armorStand.setMarker(false);

        armorStand.setCanPickupItems(false);

        armorStand.setMarker(true);

        armorStand.setCollidable(false);
        armorStand.setAI(false);
        armorStand.setPersistent(false);

        armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING);

        armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.REMOVING_OR_CHANGING);

        // 给盔甲架穿上全套皮革装备,超高速为白色，普通为红色
        equipLeatherArmor(armorStand);

        for (Player onlinePlayer:Bukkit.getOnlinePlayers()) {
            onlinePlayer.hideEntity(SCPMain.getInstance(), armorStand);
        }

        this.player.showEntity(SCPMain.getInstance(),armorStand);
        Bukkit.getLogger().info("生成了一个盔甲架");
        return armorStand;
    }

    private void equipLeatherArmor(ArmorStand armorStand) {
        Color redColor = Color.RED;
        Color whiteColor = Color.WHITE;

        // 创建红色皮革头盔
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        if(isHighSpeed==false){
            helmetMeta.setColor(redColor);
        }else{
            helmetMeta.setColor(whiteColor);
        }
        helmet.setItemMeta(helmetMeta);

        // 创建红色皮革胸甲
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        if(isHighSpeed==false){
            chestplateMeta.setColor(redColor);
        }else{
            chestplateMeta.setColor(whiteColor);
        }
        chestplate.setItemMeta(chestplateMeta);

        // 创建红色皮革护腿
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        if(isHighSpeed==false){
            leggingsMeta.setColor(redColor);
        }else{
            leggingsMeta.setColor(whiteColor);
        }
        leggings.setItemMeta(leggingsMeta);

        // 创建红色皮革靴子
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        if(isHighSpeed==false){
            bootsMeta.setColor(redColor);
        }else{
            bootsMeta.setColor(whiteColor);
        }
        boots.setItemMeta(bootsMeta);

        // 给盔甲架装备上
        armorStand.getEquipment().setHelmet(helmet);
        armorStand.getEquipment().setChestplate(chestplate);
        armorStand.getEquipment().setLeggings(leggings);
        armorStand.getEquipment().setBoots(boots);
    }

//    private void playTeleportEffects(Location from, Location to) {
//        World world = from.getWorld();
//
//        world.spawnParticle(Particle.SMOKE, from, 20, 0.5, 1, 0.5, 0.1);
//        world.spawnParticle(Particle.PORTAL, from, 15, 0.5, 1, 0.5, 0.2);
//
//        world.spawnParticle(Particle.SMOKE, to, 20, 0.5, 1, 0.5, 0.1);
//        world.spawnParticle(Particle.PORTAL, to, 15, 0.5, 1, 0.5, 0.2);
//
//        world.playSound(from, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
//        world.playSound(to, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
//    }
}
