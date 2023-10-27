package veksiak.exampleplugin.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import veksiak.vlevels.PlayerLevelingSystem;

import java.util.ArrayList;
import java.util.List;

/*
 *   Command used to set the xp of any online player.
 */
public class SetXp implements TabExecutor {
    PlayerLevelingSystem mining;
    PlayerLevelingSystem hunting;

    public SetXp(PlayerLevelingSystem mining, PlayerLevelingSystem hunting) {
        this.mining = mining;
        this.hunting = hunting;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 3) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            if (!playerNames.contains(args[1])) {
                commandSender.sendMessage("§c[ExamplePlugin] Player not found!");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                commandSender.sendMessage("§c[ExamplePlugin] Player not found!");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                String system = "";
                switch (args[0]) {
                    case "mining":
                        mining.setXP(player, amount);
                        system = "mining";
                        break;
                    case "hunting":
                        hunting.setXP(player, amount);
                        system = "hunting";
                        break;
                    default:
                        commandSender.sendMessage("§c[ExamplePlugin] Invalid leveling system specified.");
                        return true;
                }
                commandSender.sendMessage("§a[ExamplePlugin] Successfully set §b" + player.getName() + "§a's XP in system §b" + system + " §ato §b" + amount);
            } catch (NumberFormatException e) {
                commandSender.sendMessage("§c[ExamplePlugin] Invalid amount specified.");
            }
        } else {
            commandSender.sendMessage("§c[ExamplePlugin] Usage: /setxp <option> <user> <amount>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("mining");
            options.add("hunting");
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                options.add(player.getName());
            }
        }
        return options;
    }
}
