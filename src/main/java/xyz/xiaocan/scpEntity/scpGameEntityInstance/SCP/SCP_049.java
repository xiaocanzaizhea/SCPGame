package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.SCP049SpiecalSetting;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.tools.util;

import java.util.*;

@Getter
@Setter
public class SCP_049 extends SCPEntity {
    //049物品，右键救人，左键攻击，蹲下放技能R，交换释放标记技能F
    private SCP049SpiecalSetting scp049SpiecalSetting;

    private Map<UUID, Integer> attackCount = new HashMap<>();
    private Map<UUID, BukkitTask> markedPlayers = new HashMap<>();

    private double sourceSpeed;

    private Map<UUID, Integer> particleTasks = new HashMap<>();
    private long lastLeftHitTime = -1;
    private long lastFSkillTime = -1;
    private long shiftLastTime = -1;

    private long LEFT_HIT_COOLDOWN;
    private long F_SKILL_COOLDOWN;
    private long SHIFT_KEY_COOLDOWN;

    public SCP_049(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
        giveItems();
        update();

        ArmorManager.getInstance().createSCP049Suit(player);

        scp049SpiecalSetting = SCP049SpiecalSetting.getInstance();
        LEFT_HIT_COOLDOWN = (long) (scp049SpiecalSetting.getAttackCooldown() * 1000);
        F_SKILL_COOLDOWN = (long) (scp049SpiecalSetting.getFSkillCooldown() * 1000);
        SHIFT_KEY_COOLDOWN = (long) (scp049SpiecalSetting.getRSkillColldown() * 1000);

        sourceSpeed = 0.2f;
    }

    @Override
    protected void onSpawn() {
        player.sendMessage(ChatColor.GOLD + "你已经是scp049了");
    }

    @Override
    public String getDisplayName() {
        return "scp049";
    }

    //更新049特有逻辑
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
            }
        };
        expUpdateTask.runTaskTimer(SCPMain.getInstance(), 0L, 20L);
    }

    @Override
    public void giveItems() {

        super.giveSCPCardsItem();

        player.getInventory().addItem(createEntityItems());
    }

    public ItemStack createEntityItems(){

        //手持物品
        ItemStack item2 = new ItemStack(Material.RED_DYE);
        ItemMeta meta2 = item2.getItemMeta();
        List<String> lore2 = new ArrayList<>();
        lore2.add(ChatColor.GOLD + "左键: " + ChatColor.GRAY + "治疗敌人");
        lore2.add(ChatColor.GOLD + "右键: " + ChatColor.GRAY + "复活死亡的敌人 (暂时在写)");
        lore2.add(ChatColor.GOLD + "F键: " + ChatColor.GRAY + "标记敌人");
        lore2.add(ChatColor.GOLD + "Shift键: " + ChatColor.GRAY + "为周围049-2回复护盾 (暂时在写)");
        meta2.setLore(lore2);
        meta2.setDisplayName(ChatColor.GREEN + "“医疗”器");
        item2.setItemMeta(meta2);

        return item2;
    }

    public void attack(SCPPlayer target){
        UUID targetUUID = target.getPlayer().getUniqueId();

        if(!attackCount.containsKey(targetUUID)){
            //第一次攻击到
            attackCount.put(targetUUID, 1);
            //一段持续的伤害
            new BukkitRunnable(){
                long t=0;
                double damage = scp049SpiecalSetting.getDamage() / scp049SpiecalSetting.getDamageDuringTime();
                @Override
                public void run() {
                    t++;
                    if(t<scp049SpiecalSetting.getDamageDuringTime()){
                        if(target.getPlayer().isOnline()  //增加判断条件防止出bug
                                && player.isOnline()
                                && target.getPlayer().getGameMode()==GameMode.ADVENTURE){
                            target.getEntity().damaged(player, damage);
                        }else{
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimer(SCPMain.getInstance(),0l,20l);  //1s运行一次
        }else{
            //第二次攻击到
            attackCount.remove(targetUUID);
            //秒杀
            target.getEntity().damaged(this.player,
                    target.getEntity().getMaxHp() + target.getEntity().getShield());
        }
    }

    public void fKeyMark(Player target){
        UUID targetUUID = target.getUniqueId();

        //第一次标记
        if(!markedPlayers.containsKey(targetUUID)){

            float addSpeed =(float) scp049SpiecalSetting.getFSkillSpeedAdd();
            player.setWalkSpeed((float)(sourceSpeed + addSpeed));
//            player.sendMessage("速度获得了提升，原速度" + soureSpeed);
//            player.sendMessage("速度获得了提升，相加速度为" + addSpeed);
//            player.sendMessage("速度获得了提升，现在速度为" + soureSpeed + addSpeed);
            markedPlayers.put(targetUUID,
                    new BukkitRunnable() {
                        int t = 0;
                        @Override
                        public void run() {
                            t++;
                            if(t<=F_SKILL_COOLDOWN){
                                if(target.getPlayer().isOnline()  //增加判断条件防止出bug
                                        && player.isOnline()
                                            && target.getPlayer().getGameMode()==GameMode.ADVENTURE){

                                    //在目标头部生成
                                    util.createTextDidPlay(
                                            target.getLocation().clone().
                                                        add(0, 2.3, 0));
                                }
                            }else{
                                //速度恢复
                                player.setWalkSpeed((float)sourceSpeed);
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(SCPMain.getInstance(),0l,20l));
        }else{
            this.player.sendMessage("你无法二次标记同一玩家");
            markedPlayers.remove(targetUUID);
        }
    }

    public void rKeySkill(){
        double radius = scp049SpiecalSetting.getRSkillRadius();
        double duration = scp049SpiecalSetting.getRSkillDuringTime();
        double shieldPerSecond = scp049SpiecalSetting.getTotalShield()
                / duration;


        // 开始持续治疗
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = (int) (duration * 20); // 转换为tick

            @Override
            public void run() {
                if (ticks >= maxTicks || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                if (ticks % 10 == 0) {
                    healPlayerInRadius(radius,shieldPerSecond * 0.5);
                }

                ticks++;
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 1L);
    }

    private void healPlayerInRadius(double radius, double shieldAmount){
        Location center = player.getLocation();

        for (Player otherPlayer:Bukkit.getOnlinePlayers()) {
            if(otherPlayer.getLocation().distance(center) <= radius){
                if(otherPlayer.isOnline()
                        && !otherPlayer.isDead()
                        && otherPlayer.getGameMode()==GameMode.SURVIVAL){

                    SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(otherPlayer.getUniqueId());
                    if(scpPlayer!=null){
                        scpPlayer.getEntity().setShield(scpPlayer.getEntity().getShield() + shieldAmount);
                    }
                }
            }
        }
    }
}
