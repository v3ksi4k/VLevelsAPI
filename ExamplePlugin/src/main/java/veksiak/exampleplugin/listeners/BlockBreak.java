package veksiak.exampleplugin.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import veksiak.vlevels.PlayerLevelingSystem;

public class BlockBreak implements Listener {
    private final PlayerLevelingSystem mining;

    public BlockBreak(PlayerLevelingSystem s) {
        mining = s;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            switch (e.getBlock().getType()) {
                case COAL_ORE:
                case COPPER_ORE:
                case REDSTONE_ORE:
                    mining.addXP(player, 1);
                    break;
                case IRON_ORE:
                    mining.addXP(player, 200);
                    break;
                case GOLD_ORE:
                    mining.addXP(player, 300);
                    break;
                case DIAMOND_ORE:
                    mining.addXP(player, 800);
                    break;
                case EMERALD_ORE:
                    mining.addXP(player, 15);
                    break;
            }
        }
    }
}
