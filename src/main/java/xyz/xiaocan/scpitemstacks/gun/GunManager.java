package xyz.xiaocan.scpitemstacks.gun;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.Ammo;
import xyz.xiaocan.configload.option.Gun;
import xyz.xiaocan.scpgame.SCPMain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class GunManager {

    private static GunManager instance;

    //存储所有弹药类型
    public Map<AmmoType, Ammo> allAmmo = new HashMap<>();
    //存储所有枪械类型
    public Map<GunType, Gun> allGun = new HashMap<>();


    //枪械实例
    public Map<UUID, List<Gun>> playersGuns = new HashMap<>();
    //弹药拥有数量
    public Map<UUID, Map<AmmoType, Integer>> playersAmmos = new HashMap<>();

    private GunManager(){}

    public static GunManager getInstance(){
        if(instance==null)instance = new GunManager();
        return instance;
    }

    public void initPlayerAmmoMap(Player player){
        Map<AmmoType, Integer> map = new HashMap<>();
        for (AmmoType ammoType:AmmoType.values()) {
            Ammo ammo = getAllAmmo().get(ammoType);
            map.put(ammoType, 1000);
        }

        getPlayersAmmos().put(player.getUniqueId(), map);
    }

    public void test(){
        Bukkit.getLogger().info("AllGun size: " + allGun.size() + "--------------");
        for (Gun gun:allGun.values()) {
            Bukkit.getLogger().info(gun.toString());
        }

        Bukkit.getLogger().info("AllAmmo size: " + allAmmo.size() + "---------------");
        for (Ammo ammo: allAmmo.values()) {
            Bukkit.getLogger().info(ammo.toString());
        }

        Bukkit.getLogger().info("PlayerGuns size" + playersGuns.size() + "------------------");
        for (List<Gun> list: playersGuns.values()) {
            Bukkit.getLogger().info(list.toString());
        }

        Bukkit.getLogger().info("PlayerAmmo size" + playersAmmos.size() + "--------------------");
        for (Map<AmmoType, Integer> playerammosMap: playersAmmos.values()) {
            for (Map.Entry<AmmoType, Integer> entry:playerammosMap.entrySet()) {
                Bukkit.getLogger().info(entry.getKey() + " " + entry.getValue());
            }
        }
    }
}
