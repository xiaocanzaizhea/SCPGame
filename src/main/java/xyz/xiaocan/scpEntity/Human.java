package xyz.xiaocan.scpEntity;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpitemstacks.ItemManager;

import javax.xml.stream.Location;
import java.util.List;

public abstract class Human extends GameEntity{

    protected boolean isAiming;
    protected int ammoCount;

    public Human(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);

        this.maxShield = maxShield;
        this.shield = 0;

        this.ammoCount = 0;
    }

    public void giveGrenadeItem(){
        ItemStack item = ItemManager.getInstance().getAllScpItems().get("grenade").createItem();
        ItemStack item2 = ItemManager.getInstance().getAllScpItems().get("smokegrenade").createItem();
        player.getInventory().addItem(item);
        player.getInventory().addItem(item2);
    }
    // --- 抽象方法 ---
    public abstract void onShoot(); // 射击行为（具体射击逻辑在监听器）
    public abstract void onReload(); // 装弹行为

    public boolean isAiming() { return isAiming; }
    public void setAiming(boolean aiming) { isAiming = aiming; }

    public int getAmmoCount() { return ammoCount; }
    public void setAmmoCount(int ammoCount) { this.ammoCount = ammoCount; }
    public boolean hasAmmo() { return ammoCount > 0; }

    @Override
    public void onDamaged(){

    }
}
