package xyz.xiaocan.scpsystems;

import org.bukkit.entity.Player;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.*;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP_049;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP_173;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.HumanType;
import xyz.xiaocan.teams.roletypes.ScpType;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TeamAssignmentSystem {
    private static TeamAssignmentSystem instance;
    private TeamManager teamManager;
    private final Random random = new Random();

    private TeamAssignmentSystem(){
        this.teamManager = TeamManager.getInstance();
    }

    /**
        自动处理玩家身份
     */
    public void autoAssignTeams(List<Player> players){
        int totalPlayers = players.size();

        // 计算各队伍分配比例
        int scpCount = Math.max(1, totalPlayers / 6); // SCP占总人数的1/6
        int dClassCount = totalPlayers / 3;           // D级人员占1/3
        int scientistCount = totalPlayers / 6;        // 科学家占1/6
        int guardCount = totalPlayers / 6;            // 警卫占1/6

        // 剩余为MTF和混沌（根据游戏阶段）
        Collections.shuffle(players);

        int index = 0;

        // 分配SCP
        for (int i = 0; i < scpCount && index < players.size(); i++, index++) {
            assignSCPRole(players.get(index));
        }

        // 分配D级人员
        for (int i = 0; i < dClassCount && index < players.size(); i++, index++) {
            assignRole(players.get(index), HumanType.DCLASS);
        }

        // 分配科学家
        for (int i = 0; i < scientistCount && index < players.size(); i++, index++) {
            assignRole(players.get(index), HumanType.SCIENTIST);
        }

        // 分配警卫
        for (int i = 0; i < guardCount && index < players.size(); i++, index++) {
            assignRole(players.get(index), HumanType.GUARD);
        }

        // 剩余玩家随机分配为MTF或混沌（初始状态）
        while (index < players.size()) {
            HumanType team = random.nextBoolean() ? HumanType.MTFSOLDIER : HumanType.CHAOSGUNNER2;
            assignRole(players.get(index), team);
            index++;
        }
    }

    /**
        分配SCP角色
    */
    private void assignSCPRole(Player player) {
        ScpType[] scpTypes = ScpType.values();  //随机生成

        //暂时只写SCP173
        ScpType scpType = scpTypes[random.nextInt(scpTypes.length)];
        RoleTemplate roleTemplate = teamManager.getRolesTemplates().get(scpType);

        UUID uuid = player.getUniqueId();
        SCPPlayer scpPlayer = null;


        scpPlayer = new SCPPlayer(player,
                new SCP_173(player, roleTemplate), scpType);
//        switch (scpType) {
//            case SCP173:
//                scpPlayer = new SCPPlayer(player,
//                        new SCP_173(player, roleTemplate), scpType);
//                break;
//            case SCP049:
//                scpPlayer = new SCPPlayer(player,
//                        new SCP_049(player,roleTemplate), scpType);
//                break;
//        }

        teamManager.getAllPlayersMapping().put(uuid, scpPlayer);
    }

    /**
     * 分配人类角色
    */
    private void assignRole(Player player, HumanType human) {
        RoleTemplate roleTemplate = teamManager.getRolesTemplates().get(human);

        UUID uuid = player.getUniqueId();
        SCPPlayer scpPlayer = null;

        switch (human) {
            case DCLASS:
                scpPlayer = new SCPPlayer(player,
                        new DCLASS(player, roleTemplate),
                        human);
                break;
            case SCIENTIST:
                scpPlayer = new SCPPlayer(player,
                        new Scientist(player,roleTemplate),
                        human);
                break;
            case GUARD:
                scpPlayer = new SCPPlayer(player,
                        new Guard(player,roleTemplate),
                        human);
                break;
            case MTFSOLDIER:
                scpPlayer = new SCPPlayer(player,
                        new MTF_NineTailedFox(player,roleTemplate),
                        human);
                break;
            case CHAOSGUNNER2:
                scpPlayer = new SCPPlayer(player,
                        new Chaos(player,roleTemplate),
                        human);
                break;
            default:
        }

        teamManager.getAllPlayersMapping().put(uuid, scpPlayer);
    }

    public static TeamAssignmentSystem getInstance(){
        if(instance==null){
            instance = new TeamAssignmentSystem();
        }
        return instance;
    }
}
