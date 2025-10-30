package xyz.xiaocan.scpListener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.Ammo;
import xyz.xiaocan.configload.option.Gun;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.gun.GunManager;
import xyz.xiaocan.scpitemstacks.gun.GunType;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.teams.roletypes.RoleType;
import xyz.xiaocan.tools.progressBar;

import java.util.*;

import static xyz.xiaocan.configload.option.Gun.getGunId;

public class GunListener implements Listener {

    private final Map<UUID, BukkitTask> reloadTasks = new HashMap<>();
    private final Map<UUID, Long> reloadStartTime = new HashMap<>();
    private final Map<UUID, Float> originSpeed = new HashMap<>();
//    private final Map<UUID, Boolean> aimingTask = new HashMap();
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        List<Gun> guns = GunManager.getInstance().getPlayersGuns().get(player.getUniqueId());
        String gunId = getGunId(item);

        if (item != null && gunId!=null) {
            event.setCancelled(true);
            // 直接通过gunId在玩家的枪列表中查找
            Gun gunInstance = null;

            for (Gun gun : guns) {
                if (gunId.equals(gun.getId())) {  // 直接比较ID
                    gunInstance = gun;
                    break;
                }
            }
            if (gunInstance == null) return;
            long currentTime = System.currentTimeMillis();

            if (event.getAction() == Action.RIGHT_CLICK_AIR
              || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                if(gunInstance.getCurrentAmmo()>0){
                    // 检查开火冷却
                    if(currentTime - gunInstance.getLastShootTime() >= (long)(gunInstance.getRateOfFire()*1000)){
                        gunInstance.setAmmo(gunInstance.getCurrentAmmo() - 1);
                        gunInstance.setLastShootTime(currentTime);

                        shootGun(player, gunInstance);
                    }
                }
            }
            else if(event.getAction() == Action.LEFT_CLICK_AIR ||
                            event.getAction() == Action.LEFT_CLICK_BLOCK){
                event.setCancelled(true);

                gunInstance.setSetUpGun(!gunInstance.isSetUpGun());

                ItemStack newItem = gunInstance.crossBowState();
                player.getInventory().setItemInHand(newItem);
                player.updateInventory();

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);

                float orDefault = originSpeed.getOrDefault(player.getUniqueId(), 0.2f);
                if(gunInstance.isSetUpGun()){
                    player.setWalkSpeed((float)(orDefault*0.3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,
                            999999, 2, false, false));
                }else{
                    player.setWalkSpeed(orDefault);
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerSwap(PlayerSwapHandItemsEvent event){ //换弹
        Player player = event.getPlayer();
        ItemStack item = event.getOffHandItem();
        String gunId = getGunId(item);

        if(gunId==null) return;

        Gun gunInstance = null;

        List<Gun> guns = GunManager.getInstance().getPlayersGuns().get(player.getUniqueId());
        for (Gun gun : guns) {
            if (gunId.equals(gun.getId())) {  // 直接比较ID
                gunInstance = gun;
                break;
            }
        }
        if (gunInstance == null) return;

        event.setCancelled(true);

        if(gunInstance.getCurrentAmmo()==gunInstance.getMaxAmmo())  return;

        if(reloadTasks.containsKey(player.getUniqueId())) return;

        startReload(gunInstance, player);

    }
//    @EventHandler
//    public void onPlayerSneak(PlayerToggleSneakEvent event){
//        Player player = event.getPlayer();
//
//        if (!event.isSneaking()) {
//            return;
//        }
//
//        ItemStack item = event.getPlayer().getItemInHand();
//        String gunId = getGunId(item);
//
//        if(gunId==null) return;
//
//        Gun gunInstance = null;
//
//        List<Gun> guns = GunManager.getInstance().getPlayersGuns().get(player.getUniqueId());
//        for (Gun gun : guns) {
//            if (gunId.equals(gun.getId())) {  // 直接比较ID
//                gunInstance = gun;
//                break;
//            }
//        }
//        if (gunInstance == null) return;
//
//        event.setCancelled(true);
//        gunInstance.setSetUpGun(!gunInstance.isSetUpGun());
//
//        ItemStack newItem = gunInstance.crossBowState();
//        player.getInventory().setItemInHand(newItem);
//
//        player.updateInventory();
//    }
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        cancelGunReload(event.getPlayer());
    }
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        cancelGunReload(player);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){ //init originSpeed
        originSpeed.put(event.getPlayer().getUniqueId(),event.getPlayer().getWalkSpeed());
    }
    private void startReload(Gun gun, Player player){//handle visual
        UUID playerId = player.getUniqueId();

        reloadStartTime.put(playerId, System.currentTimeMillis());

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                long startTime = reloadStartTime.getOrDefault(playerId, 0L);
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - startTime;

                float progress = (float) (elapsed / (gun.getReloadTime() * 1000.0f));

                progress = Math.max(0, Math.min(1, progress));

                progressBar.updateUseProgress(player, progress, gun.getDisPlayName() + "装弹进度");
                player.setWalkSpeed((float)(originSpeed.get(playerId) * 0.7));

                if (progress >= 1.0f) {
                    reloadComplete(gun, player);
                    cancelGunReload(player);
                }
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 2L);

        reloadTasks.put(playerId, task);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
    }
    public void cancelGunReload(Player player) {
        UUID playerId = player.getUniqueId();

        BukkitTask task = reloadTasks.remove(playerId);

        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        reloadStartTime.remove(playerId);

        Float orDefault = originSpeed.getOrDefault(playerId, (float) 0.2);
        player.setWalkSpeed(orDefault);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(""));
    }
    private void reloadComplete(Gun gun, Player player){ //hanlde over
        gun.reload(player);
    }
    private void shootGun(Player player, Gun gun) {
        player.getWorld().playSound(player.getLocation(),
                Sound.ITEM_CROSSBOW_SHOOT, 1.5f, 1.2f);
        performRaycast(player, gun);
    }
    //绘制轨迹
    private void performRaycast(Player player, Gun gun) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection();

        Vector finalDirection = calculateDynamicSpread(direction, gun, player);

        World world = player.getWorld();
        double maxDistance;
        AmmoType ammoType = AmmoType.getByEnumName(gun.getId());
        Ammo ammo = GunManager.getInstance().getAllAmmo().get(ammoType);

        if(ammo==null){
            maxDistance=50;
        }else{
            maxDistance=ammo.getMaxDistance();
        }

        for (double distance = 0; distance <= maxDistance; distance += 1.0) {

            Location particleLoc = start.clone().add(finalDirection.clone().multiply(distance));
            if((distance / maxDistance) >= ScpOption.getInstance().getGunParticalStart()){
                world.spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
            }

            if (checkCollision(player, particleLoc, gun)) {
                break;
            }
        }
    }
    private Vector calculateDynamicSpread(Vector direction, Gun gun, Player player){
        Vector worldUp = new Vector(0,1,0);

        double x = direction.getY() * worldUp.getZ() - direction.getZ() * worldUp.getY();
        double y = direction.getZ() * worldUp.getX() - direction.getX() * worldUp.getZ();
        double z = direction.getX() * worldUp.getY() - direction.getY() * worldUp.getX();

        Vector right = new Vector(x,y,z).normalize();
        Vector up = right.crossProduct(direction).normalize();

        Random random = new Random();

        double baseSpread = gun.getAccuracy();

        // 获取玩家速度（只考虑水平移动）
        Vector velocity = player.getVelocity();
        double horizontalSpeed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());

        // 速度影响系数
        double speedMultiplier = 1.0 + (horizontalSpeed * 3.0); // 每米/秒增加300%散布

        // 状态影响
        double stateMultiplier = 1.0;

        if (player.isSprinting()) {
            stateMultiplier *= 1.8; // 奔跑增加80%散布
        } else if (player.isSneaking()) {
            stateMultiplier *= 0.6; // 蹲下减少40%散布
        }

        if (!player.isOnGround()) {
            stateMultiplier *= 1.5; // 空中增加50%散布
        }

//        if (gun.getConsecutiveShots() > 0) {
//            double recoilMultiplier = 1.0 + (gun.getConsecutiveShots() * 0.1);
//            stateMultiplier *= recoilMultiplier;
//        }

        double v = baseSpread * speedMultiplier * stateMultiplier;

        double horizontalOffset = (random.nextDouble() - 0.5) * v;
        double verticalOffset = (random.nextDouble() - 0.5) * v;

        // 调试信息
//        Bukkit.getLogger().info("基础散布: " + baseSpread);
//        Bukkit.getLogger().info("水平速度: " + horizontalSpeed);
//        Bukkit.getLogger().info("速度系数: " + speedMultiplier);
//        Bukkit.getLogger().info("状态系数: " + stateMultiplier);
//        Bukkit.getLogger().info("总散布值: " + v);
//        Bukkit.getLogger().info("水平偏移: " + horizontalOffset);
//        Bukkit.getLogger().info("垂直偏移: " + verticalOffset);
//        Bukkit.getLogger().info("右向量: " + right);
//        Bukkit.getLogger().info("上向量: " + up);

        Vector result = direction.clone()
                .add(right.multiply(horizontalOffset))
                .add(up.multiply(verticalOffset))
                .normalize();

//        Bukkit.getLogger().info("原本的向量" + direction);
//        Bukkit.getLogger().info("新向量" + result);

        return result;
    }
    private boolean checkCollision(Player shooter, Location location, Gun gun) {
        TeamManager teamManager = TeamManager.getInstance();

        SCPPlayer shooterScpPlayer = teamManager.getAllPlayersMapping().get(shooter.getUniqueId());
        if(shooterScpPlayer==null)return false;
        for (Entity entity : location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5)) {
            if (entity instanceof Player && entity != shooter && !entity.isDead()) {
                Player targetPlayer = (Player) entity;

                if(teamManager.getAllPlayersMapping().containsKey(targetPlayer.getUniqueId())){
                    SCPPlayer targetScpPlayer = teamManager.getAllPlayersMapping().get(targetPlayer.getUniqueId());

                    if(targetScpPlayer==null) continue;

                    if(isSameCamp(shooterScpPlayer, targetScpPlayer)){ //同一阵营取消攻击
                        continue;
                    }

                    Vector originalVelocity = targetPlayer.getVelocity();

                    SCPPlayer scpPlayer = teamManager.getAllPlayersMapping().get(targetPlayer.getUniqueId());
                    scpPlayer.getEntity().damaged(shooter, gun.getDamage());

                    createTextDisPlay(location, gun.getDamage());

                    // 立即恢复速度（移除击退）
                    Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(),
                            () -> {
                        if (targetPlayer.isValid() && !targetPlayer.isDead()) {
                            targetPlayer.setVelocity(originalVelocity);
                        }
                    }, 1L);

                    location.getWorld().playSound(location, gun.getSound(), 1.0f, 1.0f);
                    location.getWorld().spawnParticle(Particle.SMOKE, location, 10, 0.3, 0.3, 0.3, 0.1);

                    return true;
                }
            }
        }

        if (!location.getBlock().isPassable()) {
            location.getWorld().playSound(location, Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 0.8f, 1.2f);
            location.getWorld().spawnParticle(Particle.FLAME, location, 8, 0.2, 0.2, 0.2, 0.1);
            return true;
        }

        return false;
    }
    private void createTextDisPlay(Location location, double damage){
        // 创建 TextDisplay
        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);
        textDisplay.setText(ChatColor.RED + "♥ " + damage);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setBackgroundColor(Color.fromARGB(0,0,0,0));
        textDisplay.setSeeThrough(true);
        textDisplay.setShadowed(true);

        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            if (textDisplay.isValid()) {
                textDisplay.remove();
            }
        }, 16L); // 0.8秒 = 16 ticks (20 ticks = 1秒)

    }
    private boolean isSameCamp(SCPPlayer scpPlayer, SCPPlayer scpPlayer2){
        if(scpPlayer==null || scpPlayer2==null) return false;

        Map<RoleType, RoleTemplate> rolesTemplates = TeamManager.getInstance().getRolesTemplates();
        RoleType roleType = scpPlayer.getRoleType();
        RoleType roleType2 = scpPlayer2.getRoleType();

        RoleTemplate roleTemplate = rolesTemplates.get(roleType);
        RoleTemplate roleTemplate1 = rolesTemplates.get(roleType2);

        return roleTemplate == roleTemplate1;
    }
}
