package veksiak.vlevels;

import org.bukkit.entity.Player;
import veksiak.vlevels.events.VLevelsPlayerLevelUpEvent;
import veksiak.vlevels.events.VLevelsPlayerReceiveXPEvent;
import veksiak.vlevels.levelstructures.LevelStructure;
import veksiak.vlevels.listeners.VLevelsPlayerLevelUpListener;
import veksiak.vlevels.listeners.VLevelsPlayerReceiveXPListener;

import java.util.*;

public final class PlayerLevelingSystem {
    private final String id;
    private transient final LevelStructure levelStructure;
    private final transient List<VLevelsPlayerLevelUpListener> playerLevelUpListenerList = new ArrayList<>();
    private final transient List<VLevelsPlayerReceiveXPListener> playerReceiveXPListenerList = new ArrayList<>();
    private Map<UUID, Integer> playerXP = new HashMap<>();
    private transient final Map<UUID, Integer> playerLVLNumber = new HashMap<>();

    /**
     * @param id        Identifier of the system that is later used to distinguish its data.
     * @param structure Structure to use for leveling.
     */
    public PlayerLevelingSystem(String id, LevelStructure structure) {
        this.id = id;
        this.levelStructure = structure;
    }

    public void registerEvent(VLevelsPlayerLevelUpListener listener) {
        playerLevelUpListenerList.add(listener);
    }

    public void registerEvent(VLevelsPlayerReceiveXPListener listener) {
        playerReceiveXPListenerList.add(listener);
    }

    /**
     * Adds a specified amount of XP to a player's total XP.
     */
    public void addXP(Player player, int xp) {
        if (xp < 0) {
            xp = 0;
        }
        UUID playerUUID = player.getUniqueId();
        putInXPMap(playerUUID);
        int currentXPAmount = playerXP.get(playerUUID);
        playerXP.merge(playerUUID, xp, Integer::sum);
        int newXPAmount = playerXP.get(playerUUID);
        callListenersAndUpdateLevel(player, currentXPAmount, newXPAmount, xp);
    }

    /**
     * Subtracts a specified amount of XP from a player's total XP.
     */
    public void subtractFromXP(Player player, int xp) {
        if (xp < 0) {
            xp = 0;
        }
        UUID playerUUID = player.getUniqueId();
        putInXPMap(playerUUID);
        int currentXPAmount = playerXP.get(playerUUID);
        int newXPAmount = Math.max(currentXPAmount - xp, 0);
        playerXP.put(playerUUID, newXPAmount);
        callListenersAndUpdateLevel(player, currentXPAmount, newXPAmount, xp);
    }

    /**
     * Overrides a player's total XP and sets it to a specified amount.
     */
    public void setXP(Player player, int xp) {
        if (xp < 0) {
            xp = 0;
        }
        UUID playerUUID = player.getUniqueId();
        playerXP.put(playerUUID, xp);
        playerLVLNumber.put(playerUUID, levelStructure.getLevelNumber(xp));
    }

    /**
     * Overrides a player's total XP and sets it to the minimum total XP required for a specified level.
     */
    public void setLevel(Player player, int level) {
        if (level < 1) {
            level = 1;
        }
        UUID playerUUID = player.getUniqueId();
        playerXP.put(playerUUID, levelStructure.getTotalXPForLevel(level));
        playerLVLNumber.put(playerUUID, level);
    }

    private void callListenersAndUpdateLevel(Player player, int currentTotalXPAmount, int newTotalXPAmount, int amount) {
        putInLevelMap(player.getUniqueId());
        int oldLevel = levelStructure.getLevelNumber(currentTotalXPAmount);
        int newLevel = levelStructure.getLevelNumber(newTotalXPAmount);
        int levelDifference = newLevel - oldLevel;
        if (amount > 0 && !playerReceiveXPListenerList.isEmpty()) {
            for (VLevelsPlayerReceiveXPListener listener : playerReceiveXPListenerList) {
                listener.onPlayerReceiveXP(new VLevelsPlayerReceiveXPEvent(player, newTotalXPAmount, amount));
            }
        }
        if (levelDifference > 0 && !playerLevelUpListenerList.isEmpty()) {
            playerLVLNumber.put(player.getUniqueId(), newLevel);
            for (int i = 1; i < levelDifference; i++) {
                for (VLevelsPlayerLevelUpListener listener : playerLevelUpListenerList) {
                    listener.onPlayerLevelUp(new VLevelsPlayerLevelUpEvent(player, levelStructure.getTotalXPForLevel(oldLevel + i), oldLevel + i, false));
                }
            }
            for (VLevelsPlayerLevelUpListener listener : playerLevelUpListenerList) {
                listener.onPlayerLevelUp(new VLevelsPlayerLevelUpEvent(player, newTotalXPAmount, newLevel, true));
            }
        }
    }

    private void putInXPMap(UUID uuid) {
        if (!playerXP.containsKey(uuid)) playerXP.put(uuid, 0);
    }

    private void putInLevelMap(UUID uuid) {
        if (!playerLVLNumber.containsKey(uuid)) playerLVLNumber.put(uuid, 1);
    }

    /**
     * @return The level of a specified player, or 1 if the player is not registered.
     */
    public Integer getPlayerLVL(Player player) {
        UUID uuid = player.getUniqueId();
        return getPlayerLVL(uuid);
    }

    /**
     * @return The level of a specified player, or 1 if the player is not registered.
     */
    public Integer getPlayerLVL(UUID uuid) {
        return levelStructure.getLevelNumber(getPlayerXP(uuid));
    }

    /**
     * @return The amount of XP that the specified player has, or 0 if the player is not registered.
     */
    public Integer getPlayerXP(Player player) {
        UUID uuid = player.getUniqueId();
        return getPlayerXP(uuid);
    }

    /**
     * @return The amount of XP that the specified player has, or 0 if the player is not registered.
     */
    public Integer getPlayerXP(UUID uuid) {
        if (playerXP.containsKey(uuid)) {
            return playerXP.get(uuid);
        }
        return 0;
    }

    /**
     * Applies the specified data to the leveling system.
     *
     * @param data Map of player UUIDs and their total XP amount.
     * @see VLevelsStorageSystem
     * @deprecated For safety and simplicity. It is recommended to use the VLevelStorageSystem class for managing data.
     */
    @Deprecated
    public void applyData(Map<UUID, Integer> data) {
        playerXP = data;
    }

    /**
     * Gets the information about player XP from the leveling system.
     *
     * @return Map of player UUIDs and their total XP amount.
     * @see VLevelsStorageSystem
     * @deprecated For safety and simplicity. It is recommended to use the VLevelStorageSystem class for managing data.
     */
    @Deprecated
    public Map<UUID, Integer> getData() {
        return playerXP;
    }

    public String getId() {
        return id;
    }
}
