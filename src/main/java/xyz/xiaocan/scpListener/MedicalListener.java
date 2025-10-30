package xyz.xiaocan.scpListener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.Medical;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.tools.progressBar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedicalListener implements Listener {
    private final Map<UUID, BukkitTask> medicalTasks = new HashMap<>();
    private final Map<UUID, Long> medicalStartTime = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !Medical.isMedicalItem(item)) {
            return;
        }

        // 开始使用药物
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                event.getAction() == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);

            String medicalId = Medical.getMedicalIdFromItem(item);
            MedicalType medicalType = getMedicalType(medicalId);

            if (medicalType == null) {
                return;
            }

            Medical medical = ItemManager.getInstance().allMedicals.get(medicalType);

            startMedicalUse(player, medical, item);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        cancelMedicalUse(player);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        cancelMedicalUse(player);
    }

    @EventHandler
    public void onPlayerSprint(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(medicalTasks.containsKey(player.getUniqueId())){
            cancelMedicalUse(player);
        }
    }

    private void startMedicalUse(Player player, Medical medical, ItemStack item) {
        UUID playerId = player.getUniqueId();

        medicalStartTime.put(playerId, System.currentTimeMillis());

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                long startTime = medicalStartTime.getOrDefault(playerId, 0L);
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - startTime;
                float progress = (float) (elapsed / (medical.getUsageTime() * 1000));

                progressBar.updateUseProgress(player, progress, medical.getDisPlayName() + "治疗进度");

                if (progress >= 1.0f) {
                    completeMedicalUse(player, medical, item);
                    cancelMedicalUse(player);
                }
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 2L);

        medicalTasks.put(playerId, task);

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }

    private void completeMedicalUse(Player player, Medical medical, ItemStack item) {
        medical.onUse(player);

        removeMedicalItem(player, item);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
        player.sendMessage("§a已使用 " + medical.getDisPlayName());

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(""));
    }

    public void cancelMedicalUse(Player player) {
        UUID playerId = player.getUniqueId();

        BukkitTask task = medicalTasks.remove(playerId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        medicalStartTime.remove(playerId);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(""));
    }

    private void removeMedicalItem(Player player, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }

    private MedicalType getMedicalType(String medicalId) {
        for (MedicalType type : MedicalType.values()) {
            if (type.getId().equals(medicalId)) {
                return type;
            }
        }
        Bukkit.getLogger().warning("无法找到药物类型: " + medicalId);
        return null;
    }
}