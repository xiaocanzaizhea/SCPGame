package xyz.xiaocan.tools;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.Medical;

public class progressBar {

    /**
     * 显示进度条
     * @param player
     * @param progress
     */
    public static void updateUseProgress(Player player, float progress, String s) {
        int percentage = (int) (progress * 100);
        String progressBar = createProgressBar(progress, 10);
        String actionBar = "§a" + s + " §7[" + progressBar + "§7] §e" + percentage + "%";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(actionBar));
    }

    /**
     * 创建一个进度条
     * @param progress
     * @param length
     * @return
     */
    public static String createProgressBar(double progress, int length) {
        int filled = (int) (progress * length);
        int empty = length - filled;

        return "§2" + "█".repeat(filled) + "§8" + "█".repeat(empty);
    }
}
