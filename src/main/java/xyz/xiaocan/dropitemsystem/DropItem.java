package xyz.xiaocan.dropitemsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.xiaocan.scpgame.SCPMain;

import java.awt.*;
import java.util.UUID;


@Getter
@Setter
public class DropItem {
    private ItemStack itemStack;
    private Interaction interaction;
    private Location location;
    private ItemDisplay itemDisplay;
    private long dropTime;

    private DropItem(ItemStack itemStack, Location location) {
        this.itemStack = itemStack;
        this.location = location.clone();
        this.dropTime = System.currentTimeMillis();
        createEntities();
    }

    public static DropItem create(ItemStack itemStack, Location location) {
        return new DropItem(itemStack, location);
    }

    private void createEntities() {
        World world = location.getWorld();
        if (world == null) return;

        this.interaction = (Interaction) world.spawnEntity(location, EntityType.INTERACTION);

        interaction.setInteractionWidth(0.8f);
        interaction.setInteractionHeight(0.15f);
        interaction.setPersistent(false);
        interaction.setInvulnerable(true);

        String itemName = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()
                ? itemStack.getItemMeta().getDisplayName()
                : formatItemName(itemStack.getType().name());
        interaction.setCustomName("可拾取: " + itemName);
        interaction.setCustomNameVisible(false);

        createItemDisplay();
    }
    private void createItemDisplay(){
        World world = location.getWorld();
        if (world == null) return;

        itemDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);

        itemDisplay.setItemStack(itemStack.clone());

        Transformation transformation = new Transformation(
                new Vector3f(0f, 0.05f, 0.2f),  // 位置偏移
                new Quaternionf().rotateX((float) Math.toRadians(-90)), // X轴旋转-90度（平躺）
                new Vector3f(1.0f, 1.0f, 1.0f),  // 缩放
                new Quaternionf()  // 右边旋转
        );
        itemDisplay.setTransformation(transformation);

        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);

        itemDisplay.setPersistent(false);
        itemDisplay.setInvulnerable(true);
        itemDisplay.setGravity(false);
    }

    /**
     * 获取交互实体的UUID（用于Map键）
     */
    public UUID getInteractionUUID() {
        return interaction != null ? interaction.getUniqueId() : null;
    }

    /**
     * 移除所有实体
     */
    public void remove() {
        if (interaction != null && !interaction.isDead()) {
            interaction.remove();
        }
        if (itemDisplay != null && !itemDisplay.isDead()) {
            itemDisplay.remove();
        }
    }

    /**
     * 设置物品显示的位置（跟随交互实体）
     */
    public void updateDisplayPosition() {
        if (interaction != null && itemDisplay != null) {
            itemDisplay.teleport(interaction.getLocation());
        }
    }

    /**
     * 获取当前的位置
     */
    public Location getCurrentLocation() {
        return interaction != null ? interaction.getLocation() : location;
    }

    private String formatItemName(String materialName) {
        return materialName.toLowerCase().replace('_', ' ');
    }
}
