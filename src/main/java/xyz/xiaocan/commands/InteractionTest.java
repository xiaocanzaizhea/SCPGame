package xyz.xiaocan.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class InteractionTest implements CommandExecutor {

    Interaction interaction;
    ItemDisplay itemDisplay;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player) commandSender;
        if(interaction==null && itemDisplay==null){
            Location spawnLocation = player.getLocation(); // 在玩家旁边生成

            // 1. 创建 ItemDisplay 实体
            itemDisplay = (ItemDisplay) player.getWorld().spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY);

            itemDisplay.setItemStack(new ItemStack(Material.DIAMOND_SWORD));
            itemDisplay.setCustomName("一把剑");
            itemDisplay.setPassenger(player);

//        itemDisplay.setDis(ItemDisplay.DisplayType.THIRD_PERSON); // 显示模式
            itemDisplay.setPersistent(false); // 是否持久化
            itemDisplay.setInvulnerable(true); // 无敌

            interaction = (Interaction) player.getWorld().spawnEntity(spawnLocation, EntityType.INTERACTION);

            interaction.setInteractionWidth(1.0f);  // 设置交互区域宽度
            interaction.setInteractionHeight(2.0f); // 设置交互区域高度
            interaction.setPersistent(false);
            interaction.setInvulnerable(true);

            player.sendMessage("已生成交互实体和物品显示实体！");
        }else{
            itemDisplay.remove();
            interaction.remove();
            itemDisplay=null;
            interaction=null;

            player.sendMessage("已清除交互实体！");
        }
        return true;
    }
}
