package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.NeedCreateItem;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;

import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
public class Medical implements NeedCreateItem{

    private MedicalType medicalType;
    private String id;
    private String disPlayName;
    private double usageTime;
    private double duringTime;  //没有持续时间就写一秒
    private double healingHp;
    private double healingShield;

    private static NamespacedKey key = new NamespacedKey(SCPMain.getInstance(), "unique_id");

    public Medical(String id, String disPlayName, double usageTime, double duringTime, double healingHp, double healingShield) {
        this.id = id;
        this.disPlayName = disPlayName;
        this.usageTime = usageTime;
        this.duringTime = duringTime;
        this.healingHp = healingHp;
        this.healingShield = healingShield;

        this.medicalType = MedicalType.getMedicalType(id);
        if(medicalType==null){
            SCPMain.getInstance().getLogger().warning("加载药物类型出错");
        }
    }

    public static boolean isMedicalItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    public static String getMedicalIdFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public void onUse(Player player){
        UUID uuid = player.getUniqueId();

        if(!TeamManager.getInstance().getAllPlayersMapping().containsKey(uuid)){
            Bukkit.getLogger().warning("TeamManager不包含" + player.getName() + "玩家的信息");
            return;
        }

        SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(uuid);
        GameEntity entity = scpPlayer.getEntity();

        if(entity instanceof Human human){
            healHp(human);
            healShield(human);
        }
    }

    public void healHp(Human human){
        new BukkitRunnable(){
            int temp = 0;
            @Override
            public void run() {
                temp++;
                human.setHp(human.getHp() + (double)healingHp / (double) duringTime);
                if(temp>=duringTime){
                    this.cancel();
                }
            }
        }.runTaskTimer(SCPMain.getInstance(),0l,20l);
    }

    public void healShield(Human human){
        human.setShield(human.getShield() + healingShield);
    }

    @Override
    public String toString() {
        return " id: " + id + " disPlayName:" + disPlayName + " usageTime:" + usageTime + " duringTime:"
                + duringTime + " healingHp:" + healingHp + " healingShield:" + healingShield;
    }

    @Override
    public ItemStack createItem() {
        ItemStack medical = new ItemStack(Material.RED_DYE);
        ItemMeta meta = medical.getItemMeta();

        if (meta == null) return medical;

        meta.setDisplayName("§6" + disPlayName);

        // 添加自定义NBT标签用于识别
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING,
                id);

        meta.setLore(Arrays.asList(
                "§a治疗血量: " + healingHp,
                "§b治疗护盾: " + healingShield,
                "",
                "§e右键使用"
        ));

        medical.setAmount(1);
        medical.setItemMeta(meta);
        return medical;
    }
}
