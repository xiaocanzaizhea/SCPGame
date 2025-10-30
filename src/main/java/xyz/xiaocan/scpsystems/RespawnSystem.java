package xyz.xiaocan.scpsystems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF_NineTailedFox;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpsystems.messageSystem.MessageManager;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.HumanType;
import xyz.xiaocan.teams.roletypes.RoleCategory;
import xyz.xiaocan.teams.roletypes.RoleType;

import java.util.Map;

/**
 * 负责玩家重生为九尾狐和混沌
 */
public class RespawnSystem {
    private static RespawnSystem instance;
    private long lastSpawnTime;
    public RespawnSystem() {
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void reSpawnCheck(){
        long current = System.currentTimeMillis();
        long respawnTime = (long) (ScpOption.getInstance().getRespawnTime() * 1000);

//        long sub = (lastSpawnTime + respawnTime - current) / 1000;
//        if(sub <= 1){
//            String s = new String(ChatColor.GRAY + "重生时间剩余" +  sub);
//            MessageManager.boardCast(s);
//        }

        if(current - lastSpawnTime >= respawnTime){
            reSpawn();
            lastSpawnTime = current;
        }
    }

    public void reSpawn(){
        TeamManager teamManager = TeamManager.getInstance();

        boolean spawnMTF = checkSpawnCategory();
        int reSpawnCnt = 0;
        for (Player player: Bukkit.getOnlinePlayers()) {
            if(player.getGameMode()!= GameMode.SPECTATOR){
                continue;
            }

            reSpawnCnt++;
            SCPPlayer scpPlayer = null;

            if(spawnMTF){
                RoleType roleType = HumanType.MTFSOLDIER;
                scpPlayer = new SCPPlayer(player,
                        new MTF_NineTailedFox(player,
                                TeamManager.getInstance().getRolesTemplates().get(roleType)),
                                                 roleType);

            }else{
                RoleType roleType = HumanType.CHAOSGUNNER;
                scpPlayer = new SCPPlayer(player,
                        new Chaos(player,
                                TeamManager.getInstance().getRolesTemplates().get(roleType)),
                                                roleType);
            }

            teamManager.getAllPlayersMapping().put(player.getUniqueId(), scpPlayer);
        }

        if(reSpawnCnt!=0){
            String s = new String((spawnMTF?ChatColor.BLUE:ChatColor.GREEN)
                    + "检测到大量" + (spawnMTF?"九尾狐特遣队":"混沌分裂者") + "行动");
            MessageManager.boardCast(s);
        }
    }

    public boolean checkSpawnCategory(){
        TeamManager teamManager = TeamManager.getInstance();
        Map<RoleCategory, Integer> roleCategoryCount
                = teamManager.getRoleCategoryCount();

        int mtfCount = roleCategoryCount.getOrDefault(RoleCategory.MTF, 0);
        int chaosCount = roleCategoryCount.getOrDefault(RoleCategory.CHAOS, 0);

        return mtfCount < chaosCount;
    }

    public static RespawnSystem getInstance(){
        if(instance==null) instance = new RespawnSystem();
        return instance;
    }
}
