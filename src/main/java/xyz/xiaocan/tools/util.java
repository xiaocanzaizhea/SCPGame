package xyz.xiaocan.tools;

import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP_173;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TabManager;
import xyz.xiaocan.scpmanager.TeamManager;

public class util {
    public static void clearAllInventory(Player player){
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);
    }

    //初始玩家状态
    public static void initPlayerDataAdven(Player joinPlayer){
        // 初始化玩家状态

        TabManager.getInstance().updatePlayerRole(joinPlayer, "");
        clearAllInventory(joinPlayer);

        joinPlayer.setWalkSpeed(0.2f);
        joinPlayer.setGameMode(GameMode.ADVENTURE);
        joinPlayer.setHealth(joinPlayer.getMaxHealth());
        joinPlayer.setFoodLevel(20);
        joinPlayer.setSaturation(20f);
        joinPlayer.setExhaustion(0f);
        joinPlayer.setExp(0f);
        joinPlayer.setLevel(0);
        joinPlayer.getActivePotionEffects().forEach(effect ->
                joinPlayer.removePotionEffect(effect.getType()));
    }

    public static void initPlayerDataToSpec(Player joinPlayer){
        // 初始化玩家状态

        TabManager.getInstance().updatePlayerRole(joinPlayer, "");
        clearAllInventory(joinPlayer);

        joinPlayer.setWalkSpeed(0.2f);
        joinPlayer.setGameMode(GameMode.SPECTATOR);
        joinPlayer.setHealth(joinPlayer.getMaxHealth());
        joinPlayer.setFoodLevel(20);
        joinPlayer.setSaturation(20f);
        joinPlayer.setExhaustion(0f);
        joinPlayer.setExp(0f);
        joinPlayer.setLevel(0);
        joinPlayer.getActivePotionEffects().forEach(effect ->
                joinPlayer.removePotionEffect(effect.getType()));
    }

    public static void playSoundEffects(Location location, Sound sound, float f, float f2){
        location.getWorld().playSound(location, sound,f,f2);
    }

    public static void applyPotionEffect(Player target, PotionEffectType type
            , int level, int time){
        target.addPotionEffect(new PotionEffect(
                type,
                time,
                level
        ));
    }

    public static void spawnPartic(Location location, Particle type){
        location.getWorld().spawnParticle(type,
                location, 10, 0.5, 0.5, 0.5, 0.1);
    }

    public static TextDisplay createTextDidPlay(Location location){
        // 创建 TextDisplay
        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);
        textDisplay.setText(ChatColor.RED + "♥");
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setBackgroundColor(Color.BLACK);
        textDisplay.setSeeThrough(true);
        textDisplay.setShadowed(true);

        textDisplay.setLineWidth(500);

        textDisplay.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f(0, 0, 0, 0),
                new Vector3f(5, 5, 5),
                new AxisAngle4f(0, 0, 0, 0)
        ));

        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            if (textDisplay.isValid()) {
                textDisplay.remove();
            }
        }, 16L); // 0.8秒 = 16 ticks (20 ticks = 1秒)

        textDisplay.setVisibleByDefault(false);

        return textDisplay;
    }

    public static Player findGazePlayer(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        double maxDistance = 100;

        //使用射线检测
        RayTraceResult result = player.getWorld().rayTrace(
                eyeLocation,
                direction,
                maxDistance,
                FluidCollisionMode.NEVER,
                true,
                0.5,
                entity -> {  //实体排除
                    //排除非玩家实体
                    if(!(entity instanceof Player)){
                        return false;
                    }

                    Player targetPlayer = (Player) entity;

                    //排除自己
                    if(targetPlayer.equals(player)){
                        return false;
                    }

                    return true;
                }
        );

        return result != null ? (Player) result.getHitEntity() : null;
    }
    //此方法只检测scp173是否在视线中
    public static Player findGazeScp173(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        double maxDistance = 100;

        //使用射线检测
        RayTraceResult result = player.getWorld().rayTrace(
                eyeLocation,
                direction,
                maxDistance,
                FluidCollisionMode.NEVER,
                true,
                0.5,
                entity -> {

                    if(!(entity instanceof Player)){
                        return false;
                    }

                    Player targetPlayer = (Player) entity;

                    //排除自己
                    if(targetPlayer.equals(player)){
                        return false;
                    }

                    //只检测scp173
                    SCPPlayer scpPlayer = TeamManager.getInstance().
                            getAllPlayersMapping().get(targetPlayer.getUniqueId());
                    if(scpPlayer==null){
                        return false;
                    }

                    if(!(scpPlayer.getEntity() instanceof SCP_173)){
                        return false;
                    }

                    return true;
                }
        );

        return result != null ? (Player) result.getHitEntity() : null;
    }
    public static Player findNearestPeople(Player player, double radius,
                                           Location teleportLocation){
        double nearest = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player otherPeoPle :Bukkit.getOnlinePlayers()) {
            if(otherPeoPle==player)continue;

            double distance = otherPeoPle.getLocation().distance(teleportLocation);
            if(distance <= nearest && distance <= radius){
                nearest = distance;
                nearestPlayer = otherPeoPle;
            }
        }

        return nearestPlayer;
    }
    // 工具方法
    public static Sound getSafeSound(String name, Sound defaultValue) {
        try {
            return name != null ? Sound.valueOf(name.toUpperCase()) : defaultValue;
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static Particle getSafeParticle(String name, Particle defaultValue) {
        try {
            return name != null ? Particle.valueOf(name.toUpperCase()) : defaultValue;
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
