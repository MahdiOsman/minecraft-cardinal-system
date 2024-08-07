package xyz.theillusions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    // Command handler
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("broadcast")) {
            if (args.length == 0) {
                Formatter.sendMessage("Usage: /broadcast <message>", (Player) sender);
                return true;
            }

            String subCommand = args[0];
            switch (subCommand) {
                case "help":
                    Formatter.sendMessage("Usage: /broadcast <message>", (Player) sender);
                    break;
                default:
                    Formatter.broadcastMessage(args, sender.getServer());
                    break;
            }

        }
        return true;
    }
}
