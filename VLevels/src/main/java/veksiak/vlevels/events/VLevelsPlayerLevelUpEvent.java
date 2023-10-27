package veksiak.vlevels.events;

import org.bukkit.entity.Player;

public final class VLevelsPlayerLevelUpEvent {
    private final Player player;
    private final int totalXP;
    private final int newLevel;
    private final boolean last;

    public VLevelsPlayerLevelUpEvent(Player player, int totalXP, int newLevel, boolean last) {
        this.player = player;
        this.totalXP = totalXP;
        this.newLevel = newLevel;
        this.last = last;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public boolean isLast() {
        return last;
    }
}
