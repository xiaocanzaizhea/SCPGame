package xyz.xiaocan.scpmanager;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.xiaocan.configload.option.KillScore;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.teams.roletypes.RoleCategory;
import xyz.xiaocan.teams.roletypes.RoleType;

import java.util.*;

@Getter
@Setter
/*
 存储角色模版，和所有玩家(主要用于读取后的数据存储)
 */
public class TeamManager {
    private static TeamManager instance = new TeamManager();

    private TeamManager(){}

    public void init(){
        generateSpawnPoint();
    }

    // 这里根据枚举类型来存储配置文件读取的角色模版
    // 由配置文件中读取得到
    private Map<RoleType, RoleTemplate> RolesTemplates = new HashMap<>();

    private final Map<RoleType, Location> TeamSpawnPoints = new HashMap<>();
    //各个角色击杀获得的分数
    private Map<RoleType , KillScore> allKillScore = new HashMap<>();

    // 所有玩家对应的SCPPlayer      ,死亡后会消失
    private final Map<UUID, SCPPlayer> allPlayersMapping = new HashMap<>();

    //自动生成每个角色的生成点
    public void generateSpawnPoint(){
        for (Map.Entry<RoleType, RoleTemplate> entry : RolesTemplates.entrySet() ) {
            RoleType key = entry.getKey();
            RoleTemplate value = entry.getValue();

            if(!TeamSpawnPoints.containsKey(key)){
                TeamSpawnPoints.put(key,value.getLocation());
            }
        }
    }

    public static TeamManager getInstance(){
        return instance;
    }

    public Map<RoleType, Integer> getTeamsCount(){
        Map<RoleType, Integer> map = new HashMap<>();

        for (Map.Entry<UUID, SCPPlayer> entry: allPlayersMapping.entrySet()) {
            SCPPlayer scpPlayer = entry.getValue();

            RoleType roleType = scpPlayer.getRoleType();
            map.put(roleType, map.getOrDefault(roleType,0) + 1);
        }

        return map;
    }

    public Map<RoleCategory, Integer> getRoleCategoryCount(){
        Map<RoleCategory, Integer> map = new HashMap<>();

        for (Map.Entry<UUID, SCPPlayer> entry:allPlayersMapping.entrySet()) {
            SCPPlayer scpPlayer = entry.getValue();

            RoleTemplate roleTemplate = RolesTemplates.get(scpPlayer.getRoleType());
            RoleCategory roleCategory = roleTemplate.getCamp();

            map.put(roleCategory, map.getOrDefault(roleCategory,0) + 1);
        }

        return map;
    }

    public void test(){
        for (SCPPlayer scpPlayer:allPlayersMapping.values()) {
            Bukkit.getLogger().info(scpPlayer.toString());
        }
    }
}
