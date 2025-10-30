package xyz.xiaocan.scpListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP_173;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.tools.util;


public class Scp173Listener implements Listener {

    private boolean state = true; //这个state控制第一次点击（生成盔甲架） 第二次点击（传送）的转化
    private BukkitTask task;
    private ArmorStand currentArmorStand = null;

    /*
        烂泥生成技能
     */
    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();

        SCPPlayer scpPlayer = TeamManager.getInstance().
                getAllPlayersMapping().get(player.getUniqueId());
        if(scpPlayer == null || !(scpPlayer.getEntity() instanceof SCP_173)){
//            Bukkit.getLogger().info("该玩家不是scp173");
            return;
        }

        SCP_173 scp173 = (SCP_173) scpPlayer.getEntity();

        ItemStack item = event.getItem();
        if(item == null || !item.hasItemMeta() ||
                !item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "scp173道具")){
//            Bukkit.getLogger().warning("玩家手上不是scp173道具");
            return;
        }

        if(scp173.isCanTeleport()==false){
            double gazedTime = scp173.getGazedTime();
            double subTime = 3.0 - gazedTime;
            String timeStr = String.format("%.0f", subTime); // 格式化为整数
            String firstTwoDigits = timeStr.length() >= 2 ? timeStr.substring(0, 2) : timeStr;

            player.sendTitle(ChatColor.GRAY + "传送冷却",ChatColor.GRAY + firstTwoDigits ,5,5,10);
//            Bukkit.getLogger().warning("玩家还不能进行传送" + scp173.isCanTeleport());
//            Bukkit.getLogger().warning("被注释时间" + scp173.getGazedTime());
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    //这里为准备好后第一次按右键,开始生成盔甲架
                    if(state){
                        if(task==null || task.isCancelled()){
                            if(currentArmorStand != null && !currentArmorStand.isDead()){
                                currentArmorStand.remove();
                                currentArmorStand = null;
                            }

                            task = new BukkitRunnable(){
                                int t = 0;

                                @Override
                                public void run() {
                                    //生成一个随视线移动并且会朝向玩家的盔甲架
                                    t += 0.05;
                                    Location location = scp173.getGazeCollisionPoint(player);
                                    location.setDirection(scp173.getPlayer().getLocation().subtract(location).toVector());

                                    if(location==null)return;

                                    if(currentArmorStand == null || currentArmorStand.isDead()){
                                        // 第一次创建盔甲架
                                        currentArmorStand = scp173.createArmorStandMarked(location);
                                    } else {
                                        // 更新现有盔甲架位置
                                        currentArmorStand.teleport(location);
                                    }

                                    if(t>=5){  //超过时间后
                                        scp173.setCanTeleport(false);
                                        cleanup();
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(SCPMain.getInstance(),0l,1l);
                        }

                        state = false;  //切换状态
                    }else{

                        // 第二次右键 - 执行瞬移操作
                        if(task != null){
                            task.cancel();
                            task = null;
                        }

                        /**
                         * 非高速状态下杀人
                         */
                        if(!scp173.isHighSpeed()){
                            Player nearest = util.findNearestPeople(
                                    scpPlayer.getPlayer(), scp173.getRadius(),
                                    currentArmorStand.getLocation());
                            if(nearest!=null){
                                SCPPlayer nearestScpPlayer = TeamManager.
                                        getInstance().getAllPlayersMapping().get(nearest.getUniqueId());

                                if(nearestScpPlayer!=null){
                                    nearestScpPlayer.getEntity().
                                            damaged(scpPlayer.getPlayer(), scp173.getDamage());
                                }
                            }
                        }

                        // 执行瞬移到盔甲架位置
                        if(currentArmorStand != null && !currentArmorStand.isDead()){
                            Location targetLocation = currentArmorStand.getLocation();
                            targetLocation.setDirection(player.getEyeLocation().getDirection());
                            targetLocation.setPitch(player.getLocation().getPitch());
                            targetLocation.setYaw(player.getLocation().getYaw());
                            player.teleport(targetLocation);
                            player.sendMessage("§a你成功瞬移！");

                            // 移除盔甲架
                            currentArmorStand.remove();
                            currentArmorStand=null;
                        }

                        state = true; // 切换状态
                        scp173.setCanTeleport(false);
                    }
        }
    }

    // 清理方法
    private void cleanup() {
        if(currentArmorStand != null && !currentArmorStand.isDead()){
            currentArmorStand.remove();
            currentArmorStand = null;
        }
        if(task != null && !task.isCancelled()){
            task.cancel();
            task = null;
        }
        state = true;
    }

    @EventHandler
    public void OnPlayerSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();

        SCPPlayer scpPlayer = TeamManager.getInstance().
                getAllPlayersMapping().get(player.getUniqueId());
        if(scpPlayer == null || !(scpPlayer.getEntity() instanceof SCP_173)){
            return;
        }

        event.setCancelled(true);

        SCP_173 scp173 = (SCP_173) scpPlayer.getEntity();
        // 检查冷却时间
        long currentTime = System.currentTimeMillis();
        if (scp173.isOnCooldown(currentTime, scp173.getLastMudTime(), (long) (scp173.getCD_OF_MUD() * 1000))) {
            player.sendMessage(ChatColor.RED + "烂泥技能冷却中！");
            return;
        }
        scp173.setLastMudTime(currentTime);

        // 制造烂泥
        scp173.createMudPuddle();

        // 播放使用音效
        player.playSound(player.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 0.8f);
    }

    @EventHandler
    public void OnPlayerDamage(EntityDamageByEntityEvent event){
        Entity entity1 = event.getDamager();
        Entity entity2 = event.getEntity();

        if(entity1 instanceof Player && entity2 instanceof Player){
            event.setCancelled(true);

            Player source = (Player) entity1;
            Player target = (Player) entity2;

            Vector sourceDirection = source.getEyeLocation().getDirection();
            Vector targetDirection = target.getEyeLocation().getDirection();
            double dotProduct = sourceDirection.dot(targetDirection);
            double cos45 = Math.cos(Math.toRadians(45));

            if(dotProduct <= cos45){
                return;
            }

            SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(source.getUniqueId());
            SCPPlayer scpPlayer2 = TeamManager.getInstance().getAllPlayersMapping().get(target.getUniqueId());
            if(scpPlayer == null || scpPlayer2 == null){
                return;
            }

            if(!(scpPlayer.getEntity() instanceof SCP_173)){
                Bukkit.getLogger().warning(scpPlayer.getEntity().getDisplayName());
                return;
            }

            SCP_173 scp173 = (SCP_173) scpPlayer.getEntity();
            scp173.attack(scpPlayer2);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId());
        if(scpPlayer==null){
            return;
        }

        if(!(scpPlayer.getEntity() instanceof SCP_173)){
            return;
        }

        SCP_173 scp173 = (SCP_173) scpPlayer.getEntity();

        Location from = event.getFrom();
        Location to = event.getTo();
        boolean positionChange = from.getX() != to.getX() || from.getZ() != to.getZ();

        if (positionChange) {
            long currentTime = System.currentTimeMillis();
            scp173.setLastMoveTime(currentTime);
        }

        if(!scp173.isGazedBySomeBody()){
           return;
        }

        if (positionChange) {
            event.setTo(from);
        }
    }

    @EventHandler
    public void onPlayerSwap(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();

        SCPPlayer scpPlayer = TeamManager.getInstance().
                getAllPlayersMapping().get(player.getUniqueId());
        if(scpPlayer == null || !(scpPlayer.getEntity() instanceof SCP_173)){
            return;
        }

        SCP_173 scp173 = (SCP_173) scpPlayer.getEntity();

        ItemStack item = event.getOffHandItem();
        if(item == null || !item.hasItemMeta() ||
                !item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "scp173道具")){
            Bukkit.getLogger().warning("玩家手上不是scp173道具");
            return;
        }

        event.setCancelled(true);
        float origin = (float) scp173.getOriginSpeed();
        double originCD_OF_TELEPORT = scp173.getOriginCD_OF_TELEPORT();

        if(scp173.isHighSpeed()){
            player.sendMessage("提前结束了超高速");
            scp173.setHighSpeed(false);
            scp173.getPlayer().setWalkSpeed(origin);

            scp173.setCD_OF_TELEPORT(originCD_OF_TELEPORT);//恢复cd

            Bukkit.getLogger().info("玩家现在速度" + scp173.getPlayer().getWalkSpeed());
            updateCurrentArmorStand(scp173);
            return;
        }

        long current = System.currentTimeMillis();
        if(scp173.isOnCooldown(current, scp173.getLastHighSpeedTime(),
                (long) (scp173.getCD_OF_HIGHSPEED() * 1000))){
            player.sendMessage("超高速技能冷却中");
            return;
        }
        scp173.setLastHighSpeedTime(current);

        scp173.setCD_OF_TELEPORT(originCD_OF_TELEPORT * scp173.getPercentOfTeleportTime());

        scp173.getPlayer().setWalkSpeed((float) (origin + scp173.getHighSpeedAdd()));
        scp173.setHighSpeed(true);
        updateCurrentArmorStand(scp173);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(!scp173.isHighSpeed()){  // 玩家提前结束了超高速
                    this.cancel();
                }

                scp173.setHighSpeed(false);
                scp173.getPlayer().setWalkSpeed(origin);
                updateCurrentArmorStand(scp173);

                scp173.setCD_OF_TELEPORT(originCD_OF_TELEPORT * scp173.getPercentOfTeleportTime());

                scp173.getPlayer().sendMessage("超高速技能结束");
                Bukkit.getLogger().info("玩家现在速度" + scp173.getPlayer().getWalkSpeed());
                Bukkit.getLogger().info("玩家现在是否在超高速" + scp173.isHighSpeed());
            }
        }.runTaskLater(SCPMain.getInstance(),(long) (scp173.getHighSpeedDuringTime() * 20));
    }

    public void updateCurrentArmorStand(SCP_173 scp173){
        if(currentArmorStand!=null && !currentArmorStand.isDead()){
            Location location = currentArmorStand.getLocation();
            currentArmorStand.remove();
            Bukkit.getLogger().info("成功移除盔甲架");
            // 创建新的盔甲架
            currentArmorStand = scp173.createArmorStandMarked(location);
        }
    }
}
