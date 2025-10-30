package xyz.xiaocan.teams;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpmanager.TabManager;
import xyz.xiaocan.teams.roletypes.RoleType;

@Getter
public class SCPPlayer {

    private final Player player;
    private GameEntity entity;   //后续，加血和减血
    private RoleType roleType;

    public SCPPlayer(Player bukkitPlayer, GameEntity entity, RoleType roleType) {
        this.player = bukkitPlayer;
        this.entity = entity;
        this.roleType = roleType;

        TabManager.getInstance().updatePlayerRole(player, roleType.getId());
    }

    @Override
    public String toString() {
        return "SCPPlayer{" +
                "player=" + player +
                ", entity=" + entity +
                ", roleType=" + roleType +
                '}';
    }
}
