package xyz.xiaocan.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import xyz.xiaocan.configload.option.Gun;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpitemstacks.gun.GunManager;
import xyz.xiaocan.scpitemstacks.gun.GunType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GunTest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player) commandSender;
//
//        for (Gun gun :GunManager.getInstance().getAllGun().values()) {
//            player.getInventory().addItem(gun.createItem());
//        }

//        GunManager.getInstance().test();

//        Gun gun = GunManager.getInstance().getAllGun().get(GunType.COM15);
//        Gun gun2 = GunManager.getInstance().getAllGun().get(GunType.COM18);

//        player.getInventory().addItem(gun.createItem());
//        player.getInventory().addItem(gun2.createItem());

//        Map<UUID, List<Gun>> playsGuns = GunManager.getInstance().getPlayersGuns();
//        List<Gun> orDefault = playsGuns.getOrDefault(player.getUniqueId(), new ArrayList<>());
//        orDefault.add(gun);
//        orDefault.add(gun2);

//        playsGuns.put(player.getUniqueId(), orDefault);

        Map<UUID, List<Gun>> playsGuns = GunManager.getInstance().getPlayersGuns();
        List<Gun> orDefault = playsGuns.getOrDefault(player.getUniqueId(), new ArrayList<>());
        for (GunType gunType:GunType.values()) {
            Gun gun2 = GunManager.getInstance().getAllGun().get(gunType);
            player.getInventory().addItem(gun2.createItem());
            orDefault.add(gun2);
        }

        GunManager.getInstance().getPlayersGuns().put(player.getUniqueId(), orDefault);
        GunManager.getInstance().initPlayerAmmoMap(player);

//        Map<GunType, Gun> allGun = GunManager.getInstance().getAllGun();
//        List<Gun> guns = GunManager.getInstance().getPlayersGuns().get(player.getUniqueId());
//        for (Gun gun1:allGun.values()) {
//            player.getInventory().addItem(gun1.createItem());
//
//            guns.add(gun1);
//
//        }
//
//        GunManager.getInstance().getPlayersGuns().put(player.getUniqueId(), guns);

        return true;
    }
}
