package veksiak.exampleplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import veksiak.exampleplugin.command.SetXp;
import veksiak.exampleplugin.customlisteners.HuntingLevelUp;
import veksiak.exampleplugin.customlisteners.MiningLevelUp;
import veksiak.exampleplugin.listeners.BlockBreak;
import veksiak.exampleplugin.listeners.EntityDeath;
import veksiak.vlevels.PlayerLevelingSystem;
import veksiak.vlevels.VLevels;
import veksiak.vlevels.VLevelsStorageSystem;
import veksiak.vlevels.exceptions.VLevelsRecurringVariablesException;
import veksiak.vlevels.levelstructures.MutableLevelStructure;

public final class ExamplePlugin extends JavaPlugin {
    VLevels vlevels = (VLevels) Bukkit.getServer().getPluginManager().getPlugin("VLevels");
    /* Define leveling systems that you want to use. */
    PlayerLevelingSystem mining = new PlayerLevelingSystem("mining", new MutableLevelStructure(this, 100, (x) -> x + 100, true, 100, false));
    PlayerLevelingSystem hunting = new PlayerLevelingSystem("hunting", new MutableLevelStructure(this, 100, (x) -> (int) (x * 1.2), true, 10, false));
    private VLevelsStorageSystem storageSystem;

    @Override
    public void onEnable() {
        getCommand("setxp").setExecutor(new SetXp(mining, hunting));
        setupVLevels();
        registerEvents();
        getLogger().warning("Warning! This plugin was made only for testing purposes. It isn't recommended for everyday use.");
        getLogger().info("Plugin successfully loaded.");
    }

    @Override
    public void onDisable() {
        storageSystem.saveLevelingData();
        getLogger().info("Player successfully disabled.");
    }

    public void setupVLevels() {
        /* Set up the storage system and try to retrieve the data from the data file */
        try {
            storageSystem = new VLevelsStorageSystem("veksiakExamplePlugin", vlevels, new PlayerLevelingSystem[]{mining, hunting});
            storageSystem.createDataFile();
            storageSystem.getLevelingData();
            storageSystem.setAutoSave(12000L);
        } catch (VLevelsRecurringVariablesException e) {
            e.printStackTrace();
        }
        /* Add listeners */
        mining.registerEvent(new MiningLevelUp());
        hunting.registerEvent(new HuntingLevelUp());
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new BlockBreak(mining), this);
        getServer().getPluginManager().registerEvents(new EntityDeath(hunting), this);
    }
}
