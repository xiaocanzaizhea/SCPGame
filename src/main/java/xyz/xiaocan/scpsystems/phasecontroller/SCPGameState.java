package xyz.xiaocan.scpsystems.phasecontroller;

public enum SCPGameState {
    WAITING("等待中"),
    ACTIVE("进行中"),
    ENDED("已结束");

    private final String displayName;

    SCPGameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

