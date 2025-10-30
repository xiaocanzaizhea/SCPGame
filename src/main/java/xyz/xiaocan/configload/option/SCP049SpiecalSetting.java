package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
public class SCP049SpiecalSetting {
    private static SCP049SpiecalSetting instance;

    public SCP049SpiecalSetting(String id,
                                double damage, double damageDuringTime, double attackCooldown,
                                double fSkillDuringTime, double fSkillCooldown, double fSkillSpeedAdd,
                                double rSkillRadius, double totalShield, double rSkillDuringTime, double rSkillColldown,
                                double helpTime) {
        this.id = id;
        this.damage = damage;
        this.damageDuringTime = damageDuringTime;
        this.attackCooldown = attackCooldown;
        this.fSkillDuringTime = fSkillDuringTime;
        this.fSkillCooldown = fSkillCooldown;
        this.fSkillSpeedAdd = fSkillSpeedAdd;
        this.rSkillRadius = rSkillRadius;
        this.totalShield = totalShield;
        this.rSkillDuringTime = rSkillDuringTime;
        this.rSkillColldown = rSkillColldown;
        this.helpTime = helpTime;

        instance = this;
    }

    private String id;

    private double damage;
    private double damageDuringTime;
    private double attackCooldown;

    private double fSkillDuringTime;
    private double fSkillCooldown;
    private double fSkillSpeedAdd;

    private double rSkillRadius;
    private double totalShield;
    private double rSkillDuringTime;
    private double rSkillColldown;

    private double helpTime;

    public static SCP049SpiecalSetting getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("scp049设置错误");
            return null;
        }
        return instance;
    }

    @Override
    public String toString() {
        return "SCP049SpiecalSetting{" +
                "id='" + id + '\'' +
                ", damage=" + damage +
                ", damageDuringTime=" + damageDuringTime +
                ", attackCooldown=" + attackCooldown +
                ", fSkillDuringTime=" + fSkillDuringTime +
                ", fSkillCooldown=" + fSkillCooldown +
                ", fSkillSpeedAdd=" + fSkillSpeedAdd +
                ", rSkillDuringTime=" + rSkillDuringTime +
                ", rSkillColldown=" + rSkillColldown +
                ", helpTime=" + helpTime +
                '}';
    }
}
