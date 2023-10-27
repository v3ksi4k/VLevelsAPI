package veksiak.exampleplugin.customlisteners;

import veksiak.vlevels.events.VLevelsPlayerReceiveXPEvent;
import veksiak.vlevels.listeners.VLevelsPlayerReceiveXPListener;

public class HuntingRecieveXP implements VLevelsPlayerReceiveXPListener {
    @Override
    public void onPlayerReceiveXP(VLevelsPlayerReceiveXPEvent e) {
            e.getPlayer().sendMessage("§7[§6Hunting§7] Received §6"+e.getXpReceived()+" §7XP.");
    }
}
