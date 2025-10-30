package xyz.xiaocan.scpmanager;

import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.KillScore;
import xyz.xiaocan.teams.SCPPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//得分管理
public class ScoreManager {
    private static ScoreManager instance;
    private ScoreManager(){}
    // 得分
    private final Map<UUID, Double> playerTotalScore = new HashMap<>();

    public static ScoreManager getInstance(){
        if(instance==null)instance = new ScoreManager();
        return instance;
    }

    public void addScore(Player damager, Player target){
        TeamManager teamManager = TeamManager.getInstance();

        //data
        SCPPlayer targetPlayer = teamManager.getAllPlayersMapping().get(target.getUniqueId());
        SCPPlayer damagerPlayer = teamManager.getAllPlayersMapping().get(damager.getUniqueId());

        //伤害发起者的击杀分数列表
        KillScore score = teamManager.getAllKillScore().get(damagerPlayer.getRoleType());

        double getScore = score.getScore(targetPlayer.getRoleType().getId());

        playerTotalScore.put(damager.getUniqueId(),
                playerTotalScore.getOrDefault(damager.getUniqueId(), 0.0) + getScore);
    }
}
