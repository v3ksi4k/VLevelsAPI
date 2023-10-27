package veksiak.vlevels.events;

import org.bukkit.entity.Player;

public final class VLevelsPlayerReceiveXPEvent {
    private final Player player;
    private final int totalXP;
    private final int xpReceived;

    public VLevelsPlayerReceiveXPEvent(Player player, int totalXP, int xpReceived) {
        this.player = player;
        this.totalXP = totalXP;
        this.xpReceived = xpReceived;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public int getXpReceived() {
        return xpReceived;
    }

}