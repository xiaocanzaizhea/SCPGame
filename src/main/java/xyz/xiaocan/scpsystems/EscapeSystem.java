package xyz.xiaocan.scpsystems;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF_NineTailedFox;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.HumanType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EscapeSystem {
    private static EscapeSystem instance;
    private Location escapeLocation;

    private EscapeSystem() {
        escapeLocation = ScpOption.getInstance().getEscapeLocation(); //这里最可能出问题
    }

    public void checkEscape(){
        if(escapeLocation==null)return;

        double radius = 5.0;
        List<Entity> nearbyEntitys = escapeLocation.getWorld()
                .getNearbyEntities(escapeLocation,radius,radius,radius)
                .stream()
                .collect(Collectors.toList());

        for (Entity entity : nearbyEntitys) {
            handleEscapeEntity(entity);
        }
    }

    private void handleEscapeEntity(Entity entity) {
        if (!(entity instanceof Player)) {
            return;
        }

        TeamManager teamManager = TeamManager.getInstance();
        Player player = (Player) entity;
        UUID uuid = player.getUniqueId();
        SCPPlayer scpPlayer = teamManager
                .getAllPlayersMapping().get(uuid);

        if(scpPlayer==null)return;

        switch (scpPlayer.getRoleType()){
            case HumanType.DCLASS:
                scpPlayer = new SCPPlayer(player,
                        new Chaos(player,
                                teamManager.getRolesTemplates().get(HumanType.CHAOSGUNNER)),
                        HumanType.CHAOSGUNNER);
                teamManager.getAllPlayersMapping().put(uuid, scpPlayer);
                break;

            case HumanType.SCIENTIST:
                scpPlayer = new SCPPlayer(player,
                        new MTF_NineTailedFox(player,
                                teamManager.getRolesTemplates().get(HumanType.MTFCAPTAIN)),
                        HumanType.MTFCAPTAIN);
                teamManager.getAllPlayersMapping().put(uuid, scpPlayer);
                break;
            default:
                Bukkit.getLogger().info("进入逃脱转化系统");
        }
    }

    public static EscapeSystem getInstance(){
        if(instance==null) instance = new EscapeSystem();
        return instance;
    }
}
