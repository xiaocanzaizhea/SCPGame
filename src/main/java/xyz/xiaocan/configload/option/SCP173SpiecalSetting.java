package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
public class SCP173SpiecalSetting {
    private static SCP173SpiecalSetting instance;

    private String id;
    private double damage;
    private double radius;

    private double cdOfTeleport;
    private double teleportDistance;

    private double cdOfMud;
    private double mudDuringTime;

    private double cdOfHighSpeed;
    private double highSpeedDuringTime;
    private double highSpeedAdd;
    private double highSpeedDistanceAdd;
    private double percentOfTeleportTime;

    public SCP173SpiecalSetting(String id,
                                double damage, double radius,
                                double cdOfTeleport, double teleportDistance,
                                double cdOfMud, double mudDuringTime,
                                double cdOfHighSpeed, double highSpeedDuringTime,
                                double highSpeedAdd, double highSpeedDistanceAdd, double percentOfTeleportTime) {
        this.id = id;
        this.damage = damage;
        this.radius = radius;
        this.cdOfTeleport = cdOfTeleport;
        this.teleportDistance = teleportDistance;
        this.cdOfMud = cdOfMud;
        this.mudDuringTime = mudDuringTime;
        this.cdOfHighSpeed = cdOfHighSpeed;
        this.highSpeedDuringTime = highSpeedDuringTime;
        this.highSpeedAdd = highSpeedAdd;
        this.highSpeedDistanceAdd = highSpeedDistanceAdd;
        this.percentOfTeleportTime = percentOfTeleportTime;

        instance = this;
    }

    @Override
    public String toString() {
        return "SCP173SpiecalSetting{" +
                "damage=" + damage +
                ", cdOfTeleport=" + cdOfTeleport +
                ", teleportDistance=" + teleportDistance +
                ", cdOfMud=" + cdOfMud +
                ", mudDuringTime=" + mudDuringTime +
                ", cdOfHighSpeed=" + cdOfHighSpeed +
                ", highSpeedDuringTime=" + highSpeedDuringTime +
                ", highSpeedAdd=" + highSpeedAdd +
                '}';
    }

    public static SCP173SpiecalSetting getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("SCP173SpiecalSetting实例为空");
            return null;
        }
        return instance;
    }
}
