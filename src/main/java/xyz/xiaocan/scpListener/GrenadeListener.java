package xyz.xiaocan.scpListener;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.configload.option.Grenade;
import xyz.xiaocan.configload.option.SmokeGrenade;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;

import java.util.ArrayList;
import java.util.List;

public class GrenadeListener implements Listener {
    @EventHandler //检测破片手雷
    public void onGrenadeHit(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();

        if (!(entity instanceof Snowball)) {   //偷懒，这里检测是否是雪球
            return;
        }

        ProjectileSource shooter = entity.getShooter();
        Player damager;

        if (shooter instanceof Player) {
            damager = (Player) shooter;
        } else{
            damager = null;
            Bukkit.getLogger().warning(ChatColor.RED + "未知的手雷投掷者");
            return;
        }

        Location hitLocation;
        if (event.getHitBlock() != null) {
            hitLocation = event.getHitBlock().getLocation();
        } else if (event.getHitEntity() != null) {
            hitLocation = event.getHitEntity().getLocation();
        } else {
            hitLocation = entity.getLocation();
        }

//        hitLocation.getWorld().createExplosion(hitLocation, 0.0f, false, false);

        // 创建 BlockDisplay 显示手雷
        BlockDisplay blockDisplay = createGrenadeDisplay(hitLocation, Material.TNT);

        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            if (blockDisplay != null && !blockDisplay.isDead()) {
                blockDisplay.remove();
            }
            applyGrenadeDamage(damager, hitLocation);
        }, (long) (Grenade.getInstance().getExplosionTime() * 20));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler //检测烟雾弹
    public void onSmokeGrenadeHit(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();

        if (!(entity instanceof Egg)) {   //偷懒，这里检测是否是鸡蛋
            return;
        }

        Location hitLocation;
        if (event.getHitBlock() != null) {
            hitLocation = event.getHitBlock().getLocation();
        } else if (event.getHitEntity() != null) {
            hitLocation = event.getHitEntity().getLocation();
        } else {
            hitLocation = entity.getLocation();
        }

        BlockDisplay blockDisplay = createGrenadeDisplay(hitLocation, Material.SNOW_BLOCK);

        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            if (blockDisplay != null && !blockDisplay.isDead()) {
                blockDisplay.remove();
            }
            createSmoke(hitLocation);
        }, (long) (Grenade.getInstance().getExplosionTime() * 20));
    }

    private BlockDisplay createGrenadeDisplay(Location location, Material material) {
        World world = location.getWorld();

        Location displayLocation = location.clone().add(0.5, 0.5, 0.5);

        BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(displayLocation, EntityType.BLOCK_DISPLAY);

        blockDisplay.setBlock(Bukkit.createBlockData(material));

        blockDisplay.setDisplayWidth(0.8f);
        blockDisplay.setDisplayHeight(0.8f);

        blockDisplay.setGlowing(true);
        blockDisplay.setGlowColorOverride(org.bukkit.Color.RED);

        return blockDisplay;
    }
    private void applyGrenadeDamage(Player damager, Location center) {
        double damage = Grenade.getInstance().getDamage();
        double radius = Grenade.getInstance().getRadius();

        for (Player player : Bukkit.getOnlinePlayers()) {
            SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId());

            if(scpPlayer==null){
                continue;
            }

            GameEntity entity = scpPlayer.getEntity();
            double distance = player.getLocation().distance(center);
            double finalDamage = calculateDamage(damage, distance, radius);

            entity.damaged(damager,finalDamage);
        }

        createExplosionEffects(center, radius);
    }
    private double calculateDamage(double baseDamage, double distance, double radius) {
        if (distance > radius) {
            return 0;
        }

        double damageMultiplier = 1.0 - (distance / radius);
        return baseDamage * damageMultiplier;
    }
    private void createExplosionEffects(Location center, double radius) {
        World world = center.getWorld();

        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 5, 1, 1, 1, 0.1);
        world.spawnParticle(Particle.SMOKE, center, 20, radius, radius, radius, 0.1);
        world.spawnParticle(Particle.FLAME, center, 15, radius/2, radius/2, radius/2, 0.05);

        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
    }





    private void createSmoke(Location location) {
        World world = location.getWorld();
        double duration = SmokeGrenade.getInstance().getSmokeDuration();
        double radius = SmokeGrenade.getInstance().getRadius();

        // 初始效果
        world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 1.5f, 0.3f);
        world.playSound(location, Sound.BLOCK_SAND_BREAK, 1.2f, 0.6f);

        // 创建超大范围棕色粉末烟雾
        createBrownPowderSmoke(location, radius, duration);
    }

    private void createBrownPowderSmoke(Location center, double radius, double duration) {
        World world = center.getWorld();
        int totalTicks = (int) (duration * 20);

        // 使用浅灰色混凝土
        BlockData lightGrayConcreteData = Material.GRAY_CONCRETE.createBlockData();

        // 生成大量粒子位置
        List<Location> particleLocations = generateHugeParticleCloud(center, radius, 600);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= totalTicks) {
                    this.cancel();
                    return;
                }

                // 只使用浅灰色混凝土
                for (Location particleLoc : particleLocations) {
                    world.spawnParticle(
                            Particle.BLOCK_MARKER,
                            particleLoc,
                            4,
                            0.3, 0.2, 0.3,
                            0.02,
                            lightGrayConcreteData
                    );
                }

                // 添加一些动态扩散粒子
                if (ticks % 4 == 0) {
                    addExpandingBlockParticles(center, radius, world, lightGrayConcreteData);
                }

                ticks++;
            }
        }.runTaskTimer(SCPMain.getInstance(), 0, 1);
    }

    private List<Location> generateHugeParticleCloud(Location center, double radius, int count) {
        List<Location> locations = new ArrayList<>();

        // 核心密集区域（半径的60%）
        double coreRadius = radius * 0.6;
        for (int i = 0; i < count * 0.7; i++) {
            double randomRadius = Math.random() * coreRadius;
            double theta = Math.acos(2 * Math.random() - 1);
            double phi = 2 * Math.PI * Math.random();

            double x = randomRadius * Math.sin(theta) * Math.cos(phi);
            double y = randomRadius * Math.cos(theta);
            double z = randomRadius * Math.sin(theta) * Math.sin(phi);

            locations.add(center.clone().add(x, y, z));
        }

        // 外层扩散区域（半径的60%-100%）
        double outerMinRadius = radius * 0.6;
        double outerMaxRadius = radius;
        for (int i = 0; i < count * 0.3; i++) {
            double randomRadius = outerMinRadius + Math.random() * (outerMaxRadius - outerMinRadius);
            double theta = Math.acos(2 * Math.random() - 1);
            double phi = 2 * Math.PI * Math.random();

            double x = randomRadius * Math.sin(theta) * Math.cos(phi);
            double y = randomRadius * Math.cos(theta);
            double z = randomRadius * Math.sin(theta) * Math.sin(phi);

            locations.add(center.clone().add(x, y, z));
        }

        return locations;
    }

    private void addExpandingBlockParticles(Location center, double radius, World world, BlockData blockData) {
        // 在边缘添加扩散的BLOCK_MARKER粒子
        for (int i = 0; i < 25; i++) {
            double randomRadius = radius - 1 + Math.random() * 3;
            double theta = Math.acos(2 * Math.random() - 1);
            double phi = 2 * Math.PI * Math.random();

            double x = randomRadius * Math.sin(theta) * Math.cos(phi);
            double y = randomRadius * Math.cos(theta);
            double z = randomRadius * Math.sin(theta) * Math.sin(phi);

            Location edgeLoc = center.clone().add(x, y, z);

            // 向外扩散的效果
            double dirX = (Math.random() - 0.5) * 0.15;
            double dirY = Math.random() * 0.1;
            double dirZ = (Math.random() - 0.5) * 0.15;

            world.spawnParticle(
                    Particle.BLOCK_MARKER,
                    edgeLoc,
                    2,
                    dirX, dirY, dirZ,
                    0.08,
                    blockData
            );
        }
    }
}
