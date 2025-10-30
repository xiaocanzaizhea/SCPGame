package xyz.xiaocan.scpEntity.scpGameEntityInstance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.configload.option.Card;
import xyz.xiaocan.configload.option.Gun;
import xyz.xiaocan.configload.option.Medical;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.tools.util;

import javax.xml.stream.Location;
import java.util.List;

public class DCLASS extends Human{
    public DCLASS(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
        update();

        giveItems();
        ArmorManager.getInstance().createDClassSuit(player);
    }

    @Override
    protected void onSpawn() {

    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void update() {
        if (expUpdateTask != null) {
            expUpdateTask.cancel();
        }

        expUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateHealthDisplay();
            }
        };
        expUpdateTask.runTaskTimer(SCPMain.getInstance(), 0L, 20L);
    }

    @Override
    public void giveItems() {
//        try{
//            super.giveGrenadeItem();
//
//            ItemManager itemManager = ItemManager.getInstance();
//
//            Card card = itemManager.getCard("o5");
//
//            MedicalType medicalType = MedicalType.PAINKILLER;
//            Medical medical = itemManager.getAllMedicals().get(medicalType);
//
//            Gun gun1 = itemManager.getAllGun().get("gun1");
//
//            this.player.getInventory().addItem(card.createCardItemStack(),
//                    medical.createItemStacks(), gun1.createGun());
//
//        }catch (Exception e){
//            Bukkit.getLogger().warning("无法找到初始化物品的一些信息,错误在"
//                    + this.getClass().getSimpleName());
//        }
    }

    @Override
    public void onShoot() {

    }

    @Override
    public void onReload() {

    }
}
