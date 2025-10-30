package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.teams.roletypes.HumanType;
import xyz.xiaocan.teams.roletypes.RoleType;
import xyz.xiaocan.teams.roletypes.ScpType;

@Getter
@Setter
public class KillScore {
    RoleType roleType;
    double dclass;
    double scientist;
    double scp;
    double winscore;
    double speicalscore;

    public KillScore(String id, double d, double scientist, double scp, double winscore, double speicalscore) {
        this.roleType = fromConfigRoleType(id);
        if(roleType==null){
            SCPMain.getInstance().getLogger().warning("获取角色类型出错id为:" + id);
        }

        this.dclass = d;
        this.scientist = scientist;
        this.scp = scp;
        this.winscore = winscore;
        this.speicalscore = speicalscore;
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

    public double getScore(String name){
        double result = 0;
        switch(name){
            case "dclass":
                result = this.dclass;
                break;
            case "scientist":
                result = this.scientist;
                break;
            case "scp":
                result = this.scp;
                break;
            case "winscore":
                result = this.winscore;
                break;
            case "speicalscore":
                result = this.speicalscore;
                break;
            default:

        }
        return result;
    }

    @Override
    public String toString() {
        return "KillScore{" +
                "roleType=" + roleType +
                ", d=" + dclass +
                ", scientist=" + scientist +
                ", scp=" + scp +
                ", winscore=" + winscore +
                ", speicalscore=" + speicalscore +
                '}';
    }
}
