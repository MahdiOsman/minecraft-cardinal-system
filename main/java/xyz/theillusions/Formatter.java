package xyz.theillusions;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Formatter {
    public static void sendMessage(String message, Player player) {
        player.sendMessage(ChatColor.AQUA + "[Cardinal System] " + ChatColor.GREEN + message);
    }

    public static void broadcastMessage(String message, Server server) {
        server.broadcastMessage(ChatColor.AQUA + "[Cardinal System] " + ChatColor.GREEN + message);
    }

    public static void broadcastMessage(String[] message, Server server) {
        // Concatenate the message into a single string
        String msg = "";
        for (String s : message) {
            msg += s + " ";
        }
        server.broadcastMessage(ChatColor.AQUA + "[Cardinal System] " + ChatColor.GREEN + msg);
    }

    public static void logMessage(String message, Server server) {
        server.getConsoleSender().sendMessage(ChatColor.AQUA + "[Cardinal System] " + ChatColor.GREEN + message);
    }

    public static void logError(String string, Server server) {
    }

    public static String formatMessage(String string) {
        return null;
    }

}
