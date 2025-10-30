package xyz.xiaocan.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.xiaocan.dropitemsystem.DropManager;
import xyz.xiaocan.scpitemstacks.gun.GunManager;
import xyz.xiaocan.scpmanager.TeamManager;

public class ScptestCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player) commandSender;
//        TeamManager.getInstance().test();

//        DropManager.getInstance().test();

        Bukkit.getLogger().info("speed: " + player.getWalkSpeed());
        return true;
    }
}
