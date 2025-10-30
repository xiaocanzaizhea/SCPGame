package xyz.xiaocan.scpitemstacks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SCPItem {
    String getItemType();
    void use();
    ItemStack createItem();
}
