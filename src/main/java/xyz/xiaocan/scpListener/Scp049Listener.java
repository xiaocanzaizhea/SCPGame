package xyz.xiaocan.scpListener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP_049;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.tools.util;

/**
 * 专注处理scp049逻辑的监听器
 */

public class Scp049Listener implements Listener {
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event){
        if(!event.isSneaking()){ //判断是否在蹲
            return;
        }

        Player sneakPlayer = event.getPlayer();
        SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(sneakPlayer.getUniqueId());
        if(scpPlayer==null){
            return;
        }

        GameEntity gameEntity = scpPlayer.getEntity();
        if(!(gameEntity instanceof SCP_049)){
            return;
        }

        SCP_049 scp049 = (SCP_049) gameEntity;

        long currentTime = System.currentTimeMillis();
        if(scp049.isOnCooldown(currentTime, scp049.getShiftLastTime(), scp049.getSHIFT_KEY_COOLDOWN())){
            sneakPlayer.sendMessage(ChatColor.RED + "Shift技能冷却中！");
            return;
        }
        scp049.setShiftLastTime(currentTime);

        scp049.rKeySkill();
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event){

        event.setCancelled(true);

        if(!(event.getEntity() instanceof Player) //只对玩家攻击玩家进行处理
                || !(event.getDamager() instanceof Player)){
            return;
        }

        Player target = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        SCPPlayer attackScpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(attacker.getUniqueId());
        if(attackScpPlayer==null){
            return;
        }
        SCPPlayer targetScpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(target.getUniqueId());
        if(targetScpPlayer==null){
            return;
        }

        if(!(attackScpPlayer.getEntity() instanceof SCP_049)){
            return;
        }

        SCP_049 scp049 = (SCP_049) attackScpPlayer.getEntity();


        ItemStack item = attacker.getInventory().getItemInMainHand();
        if(!item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "“医疗”器")){
            return;
        }

        long current = System.currentTimeMillis();
        boolean onCooldown = scp049.isOnCooldown(current,
                scp049.getLastLeftHitTime(), scp049.getLEFT_HIT_COOLDOWN());

        if(scp049.isOnCooldown(current,
                scp049.getLastLeftHitTime(), scp049.getLEFT_HIT_COOLDOWN())){
            attacker.sendTitle(ChatColor.GRAY + "攻击冷却中", ChatColor.GRAY + "请稍等", 5,5,10);
            return;
        }
        scp049.setLastLeftHitTime(current);

        //写攻击的逻辑
        scp049.attack(targetScpPlayer);
    }
    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();

        SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId());
        if(scpPlayer==null){
            return;
        }
        if(!(scpPlayer.getEntity() instanceof SCP_049)){
            return;
        }

        SCP_049 scp049 = (SCP_049) scpPlayer.getEntity();

        ItemStack item = player.getInventory().getItemInMainHand();
        if(item==null || !item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "“医疗”器")){
            return;
        }

        event.setCancelled(true);

        Player findPlayer = util.findGazePlayer(player);
        if(findPlayer==null){
            return;
        }

        //更新冷却
        long currentTime = System.currentTimeMillis();
        if(scp049.isOnCooldown(currentTime,
                scp049.getLastFSkillTime(), scp049.getF_SKILL_COOLDOWN())){
            player.sendTitle(ChatColor.GRAY + "F技能冷却中",ChatColor.GRAY + "稍等片刻",5,5,10);
            return;
        }
        scp049.setLastFSkillTime(currentTime);

        scp049.fKeyMark(findPlayer);
    }
}
