package xyz.xiaocan.scpitemstacks.armor;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class ArmorManager {
    private static ArmorManager instance;

    private ArmorManager(){}

    public static ArmorManager getInstance(){
        if(instance==null)instance = new ArmorManager();
        return instance;
    }

    public void createMTFSuit(Player player){
        ItemStack helmet = createDyedLeatherArmor(Material.LEATHER_HELMET, TrimPattern.RIB, TrimMaterial.DIAMOND, Color.fromRGB(0, 0, 139));
        ItemStack chestplate = createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, TrimPattern.RIB, TrimMaterial.DIAMOND, Color.fromRGB(0, 0, 139));
        ItemStack leggings = createDyedLeatherArmor(Material.LEATHER_LEGGINGS, TrimPattern.RIB, TrimMaterial.DIAMOND, Color.fromRGB(0, 0, 139));
        ItemStack boots = createDyedLeatherArmor(Material.LEATHER_BOOTS, TrimPattern.RIB, TrimMaterial.DIAMOND, Color.fromRGB(0, 0, 139));

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public void createChaosSuit(Player player){
        ItemStack helmet = createDyedLeatherArmor(Material.LEATHER_HELMET, TrimPattern.SENTRY, TrimMaterial.EMERALD, Color.fromRGB(0, 100, 0));
        ItemStack chestplate = createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, TrimPattern.SENTRY, TrimMaterial.EMERALD, Color.fromRGB(0, 100, 0));
        ItemStack leggings = createDyedLeatherArmor(Material.LEATHER_LEGGINGS, TrimPattern.SENTRY, TrimMaterial.EMERALD, Color.fromRGB(0, 100, 0));
        ItemStack boots = createDyedLeatherArmor(Material.LEATHER_BOOTS, TrimPattern.SENTRY, TrimMaterial.EMERALD, Color.fromRGB(0, 100, 0));

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public void createGuardSuit(Player player){
        ItemStack helmet = createDyedLeatherArmor(Material.LEATHER_HELMET, TrimPattern.TIDE, TrimMaterial.LAPIS, Color.fromRGB(0, 0, 255));
        ItemStack chestplate = createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, TrimPattern.TIDE, TrimMaterial.LAPIS, Color.fromRGB(0, 0, 255));
        ItemStack leggings = createDyedLeatherArmor(Material.LEATHER_LEGGINGS, TrimPattern.TIDE, TrimMaterial.LAPIS, Color.fromRGB(0, 0, 255));
        ItemStack boots = createDyedLeatherArmor(Material.LEATHER_BOOTS, TrimPattern.TIDE, TrimMaterial.LAPIS, Color.fromRGB(0, 0, 255));

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public void createScientistSuit(Player player){
        ItemStack helmet = createDyedLeatherArmor(Material.LEATHER_HELMET, TrimPattern.SILENCE, TrimMaterial.QUARTZ, Color.WHITE);
        ItemStack chestplate = createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, TrimPattern.SILENCE, TrimMaterial.QUARTZ, Color.WHITE);
        ItemStack leggings = createDyedLeatherArmor(Material.LEATHER_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.QUARTZ, Color.WHITE);
        ItemStack boots = createDyedLeatherArmor(Material.LEATHER_BOOTS, TrimPattern.SILENCE, TrimMaterial.QUARTZ, Color.WHITE);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public void createDClassSuit(Player player){
        ItemStack helmet = createDyedLeatherArmor(Material.LEATHER_HELMET, TrimPattern.SPIRE, TrimMaterial.COPPER, Color.ORANGE);
        ItemStack chestplate = createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, TrimPattern.SPIRE, TrimMaterial.COPPER, Color.ORANGE);
        ItemStack leggings = createDyedLeatherArmor(Material.LEATHER_LEGGINGS, TrimPattern.SPIRE, TrimMaterial.COPPER, Color.ORANGE);
        ItemStack boots = createDyedLeatherArmor(Material.LEATHER_BOOTS, TrimPattern.SPIRE, TrimMaterial.COPPER, Color.ORANGE);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public void createSCP049Suit(Player player){
        ItemStack helmet = createDyedLeatherArmor(Material.LEATHER_HELMET, TrimPattern.SILENCE, TrimMaterial.NETHERITE, Color.BLACK);
        ItemStack chestplate = createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, TrimPattern.SILENCE, TrimMaterial.NETHERITE, Color.BLACK);
        ItemStack leggings = createDyedLeatherArmor(Material.LEATHER_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.NETHERITE, Color.BLACK);
        ItemStack boots = createDyedLeatherArmor(Material.LEATHER_BOOTS, TrimPattern.SILENCE, TrimMaterial.NETHERITE, Color.BLACK);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    private ItemStack createDyedLeatherArmor(Material armorMaterial, TrimPattern trimPattern, TrimMaterial trimMaterial, Color color) {
        ItemStack armor = new ItemStack(armorMaterial);

        if (armor.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();

            meta.setColor(color);

            meta.setUnbreakable(true);

            String armorName = getArmorDisplayName(armorMaterial);
            meta.setDisplayName(ChatColor.AQUA + armorName);

            armor.setItemMeta(meta);

            setArmorTrim(armor, trimPattern, trimMaterial);
        }

        return armor;
    }

    public void createSCP173Suit(Player player){
        // 方案二：深灰色 + 红色血迹效果
        ItemStack helmet = createDyedLeatherArmor(Material.LEATHER_HELMET, TrimPattern.SENTRY, TrimMaterial.REDSTONE, Color.fromRGB(60, 60, 60));
        ItemStack chestplate = createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, TrimPattern.SENTRY, TrimMaterial.REDSTONE, Color.fromRGB(60, 60, 60));
        ItemStack leggings = createDyedLeatherArmor(Material.LEATHER_LEGGINGS, TrimPattern.SENTRY, TrimMaterial.REDSTONE, Color.fromRGB(60, 60, 60));
        ItemStack boots = createDyedLeatherArmor(Material.LEATHER_BOOTS, TrimPattern.SENTRY, TrimMaterial.REDSTONE, Color.fromRGB(60, 60, 60));

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    private void setArmorTrim(ItemStack armor, TrimPattern trimPattern, TrimMaterial trimMaterial) {
        if (armor.getItemMeta() instanceof ArmorMeta) {
            ArmorMeta meta = (ArmorMeta) armor.getItemMeta();

            if (trimPattern != null && trimMaterial != null) {
                ArmorTrim trim = new ArmorTrim(trimMaterial, trimPattern);
                meta.setTrim(trim);
            }

            armor.setItemMeta(meta);
        }
    }

    private String getArmorDisplayName(Material material) {
        switch(material) {
            case LEATHER_HELMET: return "皮革头盔";
            case LEATHER_CHESTPLATE: return "皮革胸甲";
            case LEATHER_LEGGINGS: return "皮革护腿";
            case LEATHER_BOOTS: return "皮革靴子";
            default: return "皮革装备";
        }
    }
}
