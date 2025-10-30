package xyz.xiaocan.scpEntity.scpGameEntityInstance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

public class Scientist extends Human{


    public Scientist(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
        update();  //每tick更新

        giveItems();
        ArmorManager.getInstance().createScientistSuit(player); //create suit
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
    public void onShoot() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public void giveItems() {
//        try{
//            super.giveGrenadeItem();
//
//            ItemManager itemManager = ItemManager.getInstance();
//
//            Card card = itemManager.getCard("private");
//
//            MedicalType medicalType = MedicalType.MEDICALBAG;
//            Medical medical = itemManager.getAllMedicals().get(medicalType);
//
//            Gun gun1 = itemManager.getAllGun().get("gun1");
//
//            this.player.getInventory().addItem(card.createCardItemStack(),
//                    medical.createItemStacks(), gun1.createGun());
//
//            super.giveGrenadeItem();
//        }catch (Exception e){
//            Bukkit.getLogger().warning("无法找到初始化物品的一些信息,错误在"
//                    + this.getClass().getSimpleName());
//        }
    }
}
