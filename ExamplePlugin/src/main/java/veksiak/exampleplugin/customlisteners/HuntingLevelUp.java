package veksiak.exampleplugin.customlisteners;

import veksiak.vlevels.events.VLevelsPlayerLevelUpEvent;
import veksiak.vlevels.listeners.VLevelsPlayerLevelUpListener;

public class HuntingLevelUp implements VLevelsPlayerLevelUpListener {
    @Override
    public void onPlayerLevelUp(VLevelsPlayerLevelUpEvent e) {
        e.getPlayer().sendMessage("§6[Hunting] §aCongratulations! You've leveled up to lvl: §b" + e.getNewLevel());
        e.getPlayer().giveExp(e.getNewLevel() * 50);
    }
}
