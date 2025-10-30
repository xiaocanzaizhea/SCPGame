package xyz.xiaocan.teams.roletypes;

public enum ScpType implements RoleType{
    SCP173("scp173"),
    SCP049("scp049");
    private final String id;

    ScpType(String id) {
        this.id = id;
    }

    @Override public String getId() { return id; }

    public static ScpType fromConfigKey(String configKey) {
        for (ScpType type : values()) {
            if (type.id.equals(configKey)) return type;
        }
        return null;
    }
}
