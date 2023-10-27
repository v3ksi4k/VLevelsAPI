package veksiak.vlevels;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import veksiak.vlevels.exceptions.VLevelsRecurringVariablesException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class VLevelsStorageSystem {
    private final VLevels plugin;
    private final File file;
    private boolean isAutoSavingDeclared = false;
    private final PlayerLevelingSystem[] systemsForSaving;

    /**
     * @param filename      Name of the .json file in which this system's data should be stored and retrieved.
     * @param systemsToSave Systems that should be tracked and saved by this system.
     * @param plugin        Instance of VLevels plugin.
     * @throws VLevelsRecurringVariablesException Indicates that file names or PlayerLevelingSystem IDs are repeated.
     */
    public VLevelsStorageSystem(String filename, VLevels plugin, PlayerLevelingSystem[] systemsToSave) throws VLevelsRecurringVariablesException {
        this.plugin = plugin;
        if (plugin.names.contains(filename)) {
            throw new VLevelsRecurringVariablesException("Other plugin already declared this data file name. Filename: " + filename);
        } else {
            plugin.names.add(filename);
            file = new File(Path.of(plugin.getDataFolder().getAbsolutePath(), filename + ".json").toString());
        }
        List<String> ids = new ArrayList<>();
        for (PlayerLevelingSystem system : systemsToSave) {
            if (ids.contains(system.getId())) {
                throw new VLevelsRecurringVariablesException("Repeated IDs of PlayerLevelingSystems. ID: " + system.getId());
            }
            ids.add(system.getId());
        }
        systemsForSaving = systemsToSave;

    }

    /**
     * Checks if the data file exists and creates it if it doesn't.
     */
    public void createDataFile() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tries to retrieve the leveling data from the data file and applies it to specified PlayerLevelingSystems.
     */
    public void getLevelingData() {
        FileReader reader;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            reader = new FileReader(file);
            PlayerLevelingSystem[] systemsFromJSON = new Gson().fromJson(reader, PlayerLevelingSystem[].class);
            if (systemsFromJSON == null) {
                Bukkit.getLogger().warning("Data received from file:" + file.getPath() + " is null.");
                return;
            }
            for (PlayerLevelingSystem systemFromJSON : systemsFromJSON) {
                for (PlayerLevelingSystem system : systemsForSaving) {
                    if (Objects.equals(systemFromJSON.getId(), system.getId())) {
                        system.applyData(systemFromJSON.getData());
                    }
                }
            }
        } catch (IOException | ClassCastException e) {
            Bukkit.getLogger().warning("There was an error while trying to access the data from: " + file.getPath() + ".");
            e.printStackTrace();
        }
    }

    /**
     * Saves data to the json file with specified name in the VLevels plugin's folder.
     */
    public void saveLevelingData() {
        try {
            FileWriter writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(systemsForSaving));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets every how many ticks the data will be automatically written to the data file. <p>This method works only once. Reusing it won't do anything.</p>
     *
     * @param ticks Normally, 20 game ticks = 1 second.
     */
    public void setAutoSave(long ticks) {
        if (!isAutoSavingDeclared) {
            isAutoSavingDeclared = true;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::saveLevelingData, 0L, ticks);
        }
    }
}
