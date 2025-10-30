package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.gun.GunManager;
import xyz.xiaocan.scpitemstacks.NeedCreateItem;
import xyz.xiaocan.tools.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Gun implements NeedCreateItem {
    private String id;
    private String disPlayName;
    private double damage;
    private double reloadTime;
    private AmmoType ammoType;
    private double rateOfFire;
    private int maxAmmo;
    private int customModelData;
    private double aimingAccuracy;
    private double waistShootAccuracy;

    private Sound sound;
    private Particle particle;

    private int currentAmmo;
    private long lastShootTime;

    private boolean isSetUpGun;
    private ItemStack crossBow;

    public static NamespacedKey GUN_ID_KEY
            = new NamespacedKey(SCPMain.getInstance(), "gun_id");

    public Gun(String id, String disPlayName, double damage, double reloadTime,
               String ammoType, double rateOfFire, int maxAmmo, String sound, String particle,
                int customModelData, double aimingAccuracy, double waistShootAccuracy) {
        this.id = id;
        this.disPlayName = disPlayName;
        this.damage = damage;
        this.reloadTime = reloadTime;

        this.rateOfFire = rateOfFire;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;

        this.aimingAccuracy = aimingAccuracy;
        this.waistShootAccuracy = waistShootAccuracy;

        this.ammoType = AmmoType.getByEnumName(ammoType);

        this.sound = util.getSafeSound(sound,Sound.ITEM_CROSSBOW_SHOOT);
        this.particle = util.getSafeParticle(particle,Particle.LAVA);

        this.customModelData = customModelData;

        this.isSetUpGun = false;
        this.lastShootTime = -1;
        this.crossBow = createItem();
    }

    private ItemStack preloadArrow(ItemStack crossbow) {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();

        AmmoType ammoType1 = ammoType;

        arrowMeta.setDisplayName("§b" + GunManager.getInstance().getAllAmmo()
                .get(ammoType1).getDisplayName());
        arrow.setItemMeta(arrowMeta);

        CrossbowMeta crossbowMeta = (CrossbowMeta) crossbow.getItemMeta();

        crossbowMeta.addChargedProjectile(arrow);

        crossbow.setItemMeta(crossbowMeta);
        return crossbow;
    }

    public static String getGunId(ItemStack itemStack){
        if(itemStack == null) return null;
        if(itemStack.getType() != Material.CROSSBOW) return null;
        if(!itemStack.hasItemMeta()) return null;

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        return pdc.get(GUN_ID_KEY, PersistentDataType.STRING);
    }

    public void setAmmo(int ammo){
        currentAmmo = Math.max(0, ammo);//防止小于0
    }

    public void reload(Player player){
        try{
            Map<AmmoType, Integer> ammoTypeIntegerMap
                    = GunManager.getInstance().getPlayersAmmos().get(player.getUniqueId());

            int ammoCnt = ammoTypeIntegerMap.get(ammoType);  //已有的弹药量
            int subCnt = maxAmmo - currentAmmo;   //需要消耗的弹药量

            int min = Math.min(subCnt, ammoCnt); //只能重载多少弹药
            currentAmmo = Math.min(maxAmmo, currentAmmo + min);
            ammoTypeIntegerMap.put(ammoType, ammoCnt - min);

        }catch (Exception e){
            Bukkit.getLogger().warning("[错误]: GunManager中playerammos出现错误");
        }
    }

    public ItemStack crossBowState(){
        ItemStack result = crossBow.clone();
        if(isSetUpGun){
            return result = preloadArrow(result);
        }else{
            return result;
        }
    }

    public double getAccuracy(){
        if(isSetUpGun){
            return aimingAccuracy;
        }else {
            return waistShootAccuracy;
        }
    }

    @Override
    public String toString() {
        return "Gun{" +
                "id='" + id + '\'' +
                ", disPlayName='" + disPlayName + '\'' +
                ", damage=" + damage +
                ", reloadTime=" + reloadTime +
                ", ammoType=" + ammoType +
                ", rateOfFire=" + rateOfFire +
                ", maxAmmo=" + maxAmmo +
                ", customModelData=" + customModelData +
                ", aimingAccuracy=" + aimingAccuracy +
                ", waistShootAccuracy=" + waistShootAccuracy +
                ", currentAmmo=" + currentAmmo +
                ", isSetUpGun=" + isSetUpGun +
                '}';
    }

    @Override
    public ItemStack createItem() {
        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = crossbow.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + disPlayName);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "右键射击");
        meta.setLore(lore);

        meta.setUnbreakable(true);

//        meta.setEnchantmentGlintOverride(true);

        meta.getPersistentDataContainer().set(GUN_ID_KEY,
                PersistentDataType.STRING, id);

        meta.setCustomModelData(customModelData);

        crossbow.setItemMeta(meta);

        return crossbow;
    }
}
