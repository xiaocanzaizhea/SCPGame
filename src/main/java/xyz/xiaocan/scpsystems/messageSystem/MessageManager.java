package xyz.xiaocan.scpsystems.messageSystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MessageManager {
    public static void boardCast(String s){
        Bukkit.broadcastMessage(ChatColor.BLUE +  " §o[C . A . S . S . I . C] :    " + s);
    }
}
