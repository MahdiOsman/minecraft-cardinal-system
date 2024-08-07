package xyz.theillusions.portals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PortalTabCompleter implements TabCompleter {
    private final CustomPortalManager portalManager;

    // Constructor
    public PortalTabCompleter(CustomPortalManager portalManager) {
        this.portalManager = portalManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String subCommand = args[0];
            completions.add("create");
            completions.add("remove");
            completions.add("list");
            completions.add("link");
            completions.add("unlink");
            completions.removeIf(option -> !option.startsWith(subCommand));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("unlink")) {
            completions.addAll(portalManager.getPortals().keySet());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("link")) {
            completions.addAll(portalManager.getPortals().keySet());
        }

        return completions;
    }
}
