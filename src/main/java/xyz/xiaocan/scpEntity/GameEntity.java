package xyz.xiaocan.scpEntity;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpmanager.ScoreManager;
import xyz.xiaocan.scpmanager.TabManager;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.RoleCategory;
import xyz.xiaocan.tools.util;


@Getter
public abstract class GameEntity {

    protected double shield;
    protected double maxShield;

    protected double hp;
    protected double maxHp;

    protected double armor;

    protected double originSpeed;
    protected double moveSpeed;

    protected Player player;
    protected RoleTemplate roleTemplate;

    // 添加经验条更新相关字段
    protected boolean showHealthBar = true;
    protected long lastHealthUpdate = 0;

    // 血量显示任务
    protected BukkitRunnable expUpdateTask;

    // 体力系统


    // 构造方法,核心属性都靠构造方法来赋值
    public GameEntity(Player player, RoleTemplate roleTemplate) {
        this.player = player;
        this.roleTemplate = roleTemplate;
        this.maxHp = roleTemplate.getMaxHp();
        this.hp = maxHp;
        this.armor = roleTemplate.getArmor();
        this.moveSpeed = roleTemplate.getMoveSpeed();

        this.originSpeed = moveSpeed;
        player.setWalkSpeed((float)moveSpeed);

        util.initPlayerDataAdven(player);
        spawn();
    }

    protected abstract void onSpawn(); // 生成后的特殊初始化逻辑
    public abstract String getDisplayName(); // 显示名称
    public abstract void update(); // 每tick更新

    public abstract void giveItems();
    public abstract void onDamaged();

    //具体方法
    public void spawn(){
        Location location = TeamManager.getInstance().
                getTeamSpawnPoints().get(roleTemplate.getRoleType());

        if(location!=null){
            player.teleport(location);
        }else{
            Bukkit.getLogger().warning("GameEntity传送出错" +
                    player.getName() + "角色为" + roleTemplate.getDisPlayName());
        }
    };

    public void despawn(){
//        if (this instanceof Player) {
//            Player player = (Player) this;
//            player.setExp(0f);
//            player.setLevel(0);
//        }
    };

    public void damaged(Player damager,double value){ //被伤害
        //damager.sendMessage("对玩家" + player.getName() + "造成" + value + "点伤害");

        double originalDamage = value;
        double shieldDamage = 0;
        double hpDamage = 0;

        if (shield > 0) {
            shieldDamage = Math.min(value, shield);
            value -= shieldDamage;
            setShield(shield - shieldDamage);
        }

        hpDamage = value;
        setHp(hp - hpDamage);

        if (hp <= 0) {
            dead();
        }

        if(roleTemplate.getCamp() == RoleCategory.SCP){ //记录受击时间
            onDamaged();
        }
    }

    public void dead(){

        //entity deathMethod
        TeamManager teamManager = TeamManager.getInstance();
        TabManager tabManager = TabManager.getInstance();
        ScoreManager scoreManager = ScoreManager.getInstance();

        util.clearAllInventory(player);

        if (expUpdateTask != null && !expUpdateTask.isCancelled()) {
            expUpdateTask.cancel();
            expUpdateTask = null;
        }

        player.setHealth(0);

        //add damager's score
//        scoreManager.addScore(damager, this.player);

        onDeathCleanup();

        //respawn player and handlePlayerData
        Location deathLocation = player.getLocation();
        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("你成为旁观者");
                player.teleport(deathLocation);

                //update TabVisual
                tabManager.updatePlayerRole(player, "spec");

                //remove player'data from dataManager
                teamManager.getAllPlayersMapping().remove(player.getUniqueId());
            }
        }, 5L);
    }

    protected void onDeathCleanup(){}

    public void setHp(double hp) {
        this.hp = Math.min(Math.max(0, hp), maxHp);
    }

    public void setShield(double shield){
        this.shield = Math.min(Math.max(0, shield), maxShield);
    }

    /**
     * 更新玩家健康显示
     */
    public void updateHealthDisplay() {
        // 经验条显示血量比例
        updateExpBar();

        // 等级数字显示护盾值
        updateLevelShield();
    }

    /**
     * 经验条显示血量比例
     */
    protected void updateExpBar() {
        float healthProgress = (float) (hp / maxHp);
        healthProgress = Math.max(0.0f, Math.min(1.0f, healthProgress));
        player.setExp(healthProgress);
    }

    /**
     * 等级数字显示护盾值
     */
    private void updateLevelShield() {
        // 等级显示护盾值（取整）
        int currentShield = (int) Math.ceil(shield);
        player.setLevel(currentShield);
    }
}
