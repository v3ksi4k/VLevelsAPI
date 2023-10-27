package veksiak.vlevels;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author veksiak
 */
public final class VLevels extends JavaPlugin {
    final List<String> names = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Plugin successfully loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin successfully disabled.");
    }
}
