package xyz.xiaocan.bossbar;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpsystems.phasecontroller.PhaseController;
import xyz.xiaocan.scpsystems.phasecontroller.SCPGameState;

@Getter
@Setter
public class ScpBossBar {
    private static ScpBossBar instance;

    private BossBar bossBar;
    private int currentPlayer;
    private int maxPlayer;
    private double currentProgress;

    private ScpBossBar() {
        bossBar = createBossBar("等待中",0,ScpOption.getInstance().getMaxPlayers());
    }

    private static BarColor getHealthColor(double progress) {
        if (progress > 0.6) return BarColor.GREEN;
        if (progress > 0.3) return BarColor.YELLOW;
        return BarColor.RED;
    }

    /**
     * 便捷方法：创建血量BossBar
     */
    public BossBar createBossBar(String entityName, int currentHealth, int maxHealth) {
        double progress = Math.max(0, Math.min(1, (double)currentHealth / (double)maxHealth));
        BarColor color = getHealthColor(progress);
        String title = "§c" + entityName + " §f" + currentHealth + "§7/§f" + maxHealth;
        this.currentPlayer = currentHealth;
        this.maxPlayer = maxHealth;

        BossBar bar = Bukkit.createBossBar(title,color,BarStyle.SEGMENTED_10);
        bar.setProgress(progress);
        return bar;
    }

    /**
     * 更新血量显示
     */
    public void addCurrent(int num) {
        this.currentPlayer = Math.max(0,Math.min(maxPlayer,currentPlayer + num));
        this.currentProgress = (double) this.currentPlayer / (double) maxPlayer;

        updateBossBarDisplay();
    }

    /**
     * 单独更新显示的方法
     */
    private void updateBossBarDisplay() {
        BarColor color = getHealthColor(currentProgress);
        String title = PhaseController.getInstance().getCurrentGameState() == SCPGameState.WAITING?
                "§c" + "等待玩家中" + " §f" + currentPlayer + "§7/§f" + maxPlayer :
                "§b" + "游戏进行中" + " §f" + currentPlayer + "§7 / §f" + maxPlayer;
        getBossBar().setTitle(title);
        getBossBar().setColor(color);
        bossBar.setProgress(currentProgress);
    }

    public static ScpBossBar getInstance(){
        if(instance==null){
            instance = new ScpBossBar();
        }
        return instance;
    }

}