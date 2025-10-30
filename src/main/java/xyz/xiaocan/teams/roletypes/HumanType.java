package xyz.xiaocan.teams.roletypes;

/**
 * SCP类型枚举
 */
public enum HumanType implements RoleType {
    CHAOSGUNNER("chaos-gunner"),
    CHAOSGUNNER2("chaos-gunner2"),
    DCLASS("dclass"),
    MTFCAPTAIN("mtf-captain"),
    MTFSOLDIER("mtf-soldier"),
    GUARD("guard"),
    SCIENTIST("scientist");

    private final String id;

    HumanType(String id) {
        this.id = id;
    }

    @Override public String getId() { return id; }

    public static HumanType fromConfigKey(String configKey) {
        for (HumanType type : values()) {
            if (type.id.equals(configKey)) return type;
        }
        return null;
    }
}
