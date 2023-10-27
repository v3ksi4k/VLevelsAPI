package veksiak.exampleplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import veksiak.vlevels.PlayerLevelingSystem;

public class EntityDeath implements Listener {
    private final PlayerLevelingSystem hunting;

    public EntityDeath(PlayerLevelingSystem hunting) {
        this.hunting = hunting;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            switch (e.getEntity().getType()) {
                case WITHER:
                    hunting.addXP(killer, 200);
                    break;
                case ENDER_DRAGON:
                    hunting.addXP(killer, 150);
                    break;
                case ELDER_GUARDIAN:
                    hunting.addXP(killer, 50);
                    break;
                case CAVE_SPIDER:
                    hunting.addXP(killer, 200);
                    break; //TODO: Unchange this
                case SPIDER:
                case SILVERFISH:
                    hunting.addXP(killer, 100);
                    break;
                case ZOMBIE:
                case SKELETON:
                    hunting.addXP(killer, 4000);
                    break;
                case CREEPER:
                    hunting.addXP(killer, 500);
                    break;
                case ENDERMITE:
                case STRAY:
                    hunting.addXP(killer, 800);
                    break;
                case ENDERMAN:
                case DROWNED:
                case GUARDIAN:
                    hunting.addXP(killer, 600);
                    break;
            }
        }
    }
}
