package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import xyz.xiaocan.teams.roletypes.*;

import java.util.*;

@Getter
@Setter
public class RoleTemplate {

    private RoleType roleType; //扮演的角色
    private String disPlayName;

    private double maxHp;
    private double healHpCount;
    private double healHpNeedTime;

    private double maxShield;
    private double recoverShieldCount;
    private double recoverShieldNeedTime;

    private double moveSpeed;
    private double armor;
    private String color;
    private Location location;
    private List<String> friendTeamsId;
    private RoleCategory camp;   //所属阵营

    public RoleTemplate(String id, String disPlayName,
                        double hp, double shield,
                        double healHpCount, double healHpNeedTime,
                        double recoverShieldCount, double recoverShieldNeedTime,
                        double moveSpeed, double armor, String color, Location location,
                            List<String> friendTeamsId, String camp) {

        this.disPlayName = disPlayName;
        this.maxHp = hp;
        this.healHpCount = healHpCount;
        this.healHpNeedTime = healHpNeedTime;

        this.maxShield = shield;
        this.recoverShieldCount = recoverShieldCount;
        this.recoverShieldNeedTime = recoverShieldNeedTime;

        this.moveSpeed = moveSpeed;
        this.armor = armor;
        this.color = color;
        this.location = location;
        this.friendTeamsId = friendTeamsId;

        this.roleType = fromConfigRoleType(id);
        if(roleType==null){
            System.out.println("[RoleTemplate] 配置文件角色id出错,无法找到id为" + id + "的RoleType");
        }

        this.camp = fromConfigCategory(camp);
        if(camp==null){
            System.out.println("[RoleTemplate] 配置文件队伍camp出错，无法找到队伍为" + camp + "的Rolecategory");
        }
    }


    public static RoleCategory fromConfigCategory(String configKey) {
        if(configKey.equals("chaos")){
            return RoleCategory.CHAOS;
        }else if(configKey.equals("mtf")){
            return RoleCategory.MTF;
        }else if(configKey.equals("scp")){
            return RoleCategory.SCP;
        }else if(configKey.equals("spec")){
            return RoleCategory.SPEC;
        }
        else{
            System.out.println("无法找得到阵营信息");
            return null;
        }
    }

    public static RoleType fromConfigRoleType(String configKey) {
        if (configKey == null) return null;

        // 按优先级顺序解析
        RoleType roleType = HumanType.fromConfigKey(configKey);
        if (roleType != null) return roleType;

        roleType = ScpType.fromConfigKey(configKey);
        if (roleType != null) return roleType;

        return null;
    }

    @Override
    public String toString() {
        return "RoleTemplate{" +
                "roleType=" + roleType +
                ", disPlayName='" + disPlayName + '\'' +
                ", maxHp=" + maxHp +
                ", healHpCount=" + healHpCount +
                ", healHpNeedTime=" + healHpNeedTime +
                ", maxShield=" + maxShield +
                ", recoverShieldCount=" + recoverShieldCount +
                ", recoverShieldNeedTime=" + recoverShieldNeedTime +
                ", moveSpeed=" + moveSpeed +
                ", armor=" + armor +
                ", color='" + color + '\'' +
                ", location=" + location +
                ", friendTeamsId=" + friendTeamsId +
                ", camp=" + camp +
                '}';
    }
}
