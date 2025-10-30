package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.xiaocan.scpgame.SCPMain;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class Card {
    private String id;
    private String disPlayName;
    private List<Integer> permissionsLevel;
    private Material material;
    private int customModelData;

    private static NamespacedKey key = new NamespacedKey(SCPMain.getInstance(), "id");

    public Card(String id, String disPlayName,
                List<Integer> permissionsLevel, String material, int customModelData) {
        this.id = id;
        this.disPlayName = disPlayName;
        this.permissionsLevel = permissionsLevel;
        this.customModelData = customModelData;

        try{
            Material temp = Material.valueOf(material);
            this.material = temp;
        }catch(IllegalArgumentException e){
            SCPMain.getInstance().getLogger().warning("Card材料配置文件出错: " + material);
            this.material = Material.PAPER;
        }
    }

    //创建卡片实物
    public ItemStack createCardItemStack() {
        ItemStack card = new ItemStack(Material.PAPER);
        ItemMeta meta = card.getItemMeta();

        if (meta == null) return card;

        meta.setDisplayName("§6" + disPlayName);
        meta.setLore(Arrays.asList(
                "§fSCP基金会官方授权",
                "§e大门访问等级: " + permissionsLevel.get(0),
                "§e门访问等级: " + permissionsLevel.get(1),
                "§e武器访问等级: " + permissionsLevel.get(2),
                "§7用于开启安全门禁系统"
        ));

        // 添加自定义NBT标签用于识别
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING,
                id);

        meta.setCustomModelData(customModelData);

        card.setAmount(1);
        card.setItemMeta(meta);
        return card;
    }

    /**
     * 是否为我们的卡片
     */
    public static boolean isCard(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    /**
     * 获取卡片id
     */
    public static String getCardId(ItemStack item) {
        if (!isCard(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(SCPMain.getInstance(), "id");
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", disPlayName='" + disPlayName + '\'' +
                ", permissionsLevel=" + permissionsLevel +
                ", material=" + material +
                '}';
    }
}
