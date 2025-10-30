package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import xyz.xiaocan.scpgame.SCPMain;

@Getter
@Setter
public class ScpOption {
    private static ScpOption instance;

    private String arenaName;
    private int minPlayers;
    private int maxPlayers;
    private Location lobbySpawn;
    private Location escapeLocation;

    //GameSetting
    private int duration;
    private double respawnTime;
    private int waitTime;
    private boolean allowRespawn;
    private boolean friendlyFire;
    private boolean autoBalance;

    //worldSetting
    private int borderSize;
    private boolean allowPvp;
    private boolean allowPve;

    //debug
    private boolean debug;

    private double gunParticalStart;

    public ScpOption() {}

    public static ScpOption getInstance(){
        if(instance==null){
            System.out.println("获取ScpOption出错，实例为空");
            return null;
        }
        return instance;
    }

    public static void setInstance(ScpOption scpOption) {
        instance = scpOption;
    }
}