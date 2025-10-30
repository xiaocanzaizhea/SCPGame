package xyz.xiaocan.scpmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TabManager {
    private static TabManager instance;
    private TabManager(){}
    public void setPlayerPrefix(Player player, String prefix, ChatColor color) {
        String teamName = "team_" + player.getName();

        Team existingTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
        if (existingTeam != null) {
            existingTeam.unregister();
        }

        prefix = color + prefix;
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(teamName);
        team.setPrefix(prefix + " ");
        team.setColor(color);
        team.addEntry(player.getName());

    }

    public void updatePlayerRole(Player player, String role) {
        switch(role.toLowerCase()) {
            case "scp173":
                setPlayerPrefix(player, "[SCP-173]", ChatColor.RED);
                break;
            case "scp049":
                setPlayerPrefix(player, "[SCP-049]", ChatColor.RED);
                break;
            case "guard":
                setPlayerPrefix(player, "[警卫]", ChatColor.BLUE);
                break;
            case "scientist":
                setPlayerPrefix(player, "[科学家]", ChatColor.WHITE);
                break;
            case "spec":
                setPlayerPrefix(player, "[观察者]", ChatColor.GRAY);
                break;
            case "mtf-captain":
                setPlayerPrefix(player, "[九尾狐指挥官]", ChatColor.DARK_BLUE);
                break;
            case "mtf-soldier":
                setPlayerPrefix(player, "[九尾狐列兵]", ChatColor.DARK_BLUE);
                break;
            case "chaos-gunner":
                setPlayerPrefix(player, "[混沌分裂者步枪手]", ChatColor.DARK_GREEN);
                break;
            case "chaos-gunner2":
                setPlayerPrefix(player, "[混沌分裂者机枪手]", ChatColor.DARK_GREEN);
                break;
            case "dclass":
                setPlayerPrefix(player, "[D级人员]", ChatColor.YELLOW);
                break;
            default:
                setPlayerPrefix(player, "[玩家]", ChatColor.GOLD);
        }
    }

    public static TabManager getInstance(){
        if(instance==null)instance = new TabManager();
        return instance;
    }

    public static void setNameHiden(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            String teamName = "team_" + player.getName();
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);

            if (team != null) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
        }
    }
}
