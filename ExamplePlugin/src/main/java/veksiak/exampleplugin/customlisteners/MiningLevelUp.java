package veksiak.exampleplugin.customlisteners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import veksiak.vlevels.events.VLevelsPlayerLevelUpEvent;
import veksiak.vlevels.listeners.VLevelsPlayerLevelUpListener;

public class MiningLevelUp implements VLevelsPlayerLevelUpListener {

    @Override
    public void onPlayerLevelUp(VLevelsPlayerLevelUpEvent e) {
        Player player = e.getPlayer();
        int newLevel = e.getNewLevel();
        player.sendMessage(ChatColor.GOLD + "[Mining] " + ChatColor.GREEN + "Congratulations! You've leveled up to lvl: " + ChatColor.AQUA + newLevel);
        if (newLevel >= 50) {
            player.giveExp(150);
            player.getInventory().addItem(new ItemStack(Material.EMERALD, 2), new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.IRON_INGOT, 5), new ItemStack(Material.COAL_BLOCK, 3));
        }
        if (newLevel >= 30) {
            player.giveExp(100);
            player.getInventory().addItem(new ItemStack(Material.EMERALD, 2), new ItemStack(Material.IRON_INGOT, 3), new ItemStack(Material.COAL_BLOCK, 2));
        } else {
            player.giveExp(newLevel);
            player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, newLevel / 3), new ItemStack(Material.COAL, newLevel / 3));
        }
    }
}
