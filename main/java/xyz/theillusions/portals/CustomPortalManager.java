package xyz.theillusions.portals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import xyz.theillusions.Formatter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomPortalManager implements CommandExecutor, Listener {
    private Map<String, Portal> portals;
    private Map<String, String> portalLinks;
    private FileConfiguration dataConfig;
    private File dataFile;
    private Server server;
    final private Material portalBlock = Material.WHITE_STAINED_GLASS;

    // Constructor
    public CustomPortalManager(JavaPlugin plugin) {
        server = plugin.getServer();
        portals = new HashMap<>();
        portalLinks = new HashMap<>();
        dataFile = new File(plugin.getDataFolder(), "portals.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadPortals();
        loadPortalLinks();
    }

    // Load portals from file
    private void loadPortalLinks() {
        if (dataConfig.contains("portal_links")) {
            ConfigurationSection linksSection = dataConfig.getConfigurationSection("portal_links");
            for (String portalName : linksSection.getKeys(false)) {
                String linkedPortalName = linksSection.getString(portalName);
                portalLinks.put(portalName, linkedPortalName);
            }
        }
    }

    // Load portal links from file
    private void loadPortals() {
        if (dataConfig.contains("portals")) {
            ConfigurationSection portalsSection = dataConfig.getConfigurationSection("portals");
            for (String portalName : portalsSection.getKeys(false)) {
                Location location = portalsSection.getLocation(portalName);
                Portal portal = new Portal(portalName, location);
                portals.put(portalName, portal);
            }
        }
    }

    // Save portals to data file
    private void savePortals() {
        ConfigurationSection portalsSection = dataConfig.createSection("portals");
        for (Map.Entry<String, Portal> entry : portals.entrySet()) {
            String portalName = entry.getKey();
            Location location = entry.getValue().getLocation();
            portalsSection.set(portalName, location);
        }
        // Log message
        Formatter.logMessage("Portals saved to data file.", server);
        saveDataFile();
    }

    // Save portal links to data file
    private void savePortalLinks() {
        ConfigurationSection linksSection = dataConfig.createSection("portal_links");
        for (Map.Entry<String, String> entry : portalLinks.entrySet()) {
            String portalName = entry.getKey();
            String linkedPortalName = entry.getValue();
            linksSection.set(portalName, linkedPortalName);
        }
        // Log message
        Formatter.logMessage("Portal links saved to data file.", server);
        saveDataFile();
    }

    // Save data file
    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if portal exists in location
    public boolean portalExists(Location location) {
        for (Portal portal : portals.values()) {
            if (portal.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    // Get portals
    public Map<String, Portal> getPortals() {
        return portals;
    }

    // Create portal
    public void createPortal(Player player, String name) {
        Block targetBlock = player.getTargetBlock(null, 5);

        // If portal already exists in location, send error message
        if (portalExists(targetBlock.getLocation())) {
            Formatter.sendMessage("A portal already exists in this location.", player);
            return;
        }

        // Check if target block is a portal block
        if (targetBlock.getType() != portalBlock) {
            Formatter.sendMessage("Invalid portal block. BLOCK: " + portalBlock, player);
            return;
        }

        // Create the portal object
        Location portalLocation = targetBlock.getLocation();
        Portal portal = new Portal(name, portalLocation);

        // Store the portal in the portals map
        portals.put(name, portal);

        // Save the portal to the data file
        savePortals();
        Formatter.sendMessage("Portal " + name + " created successfully.", player);
    }

    // Link portals
    public void linkPortals(Player player, String portalName1, String portalName2) {
        // Check if portals exist
        if (!portals.containsKey(portalName1) || !portals.containsKey(portalName2)) {
            Formatter.sendMessage("One or both of the specified portals do not exist.", player);
            return;
        }

        // Check if portal is already linked
        if (portalLinks.containsKey(portalName1) || portalLinks.containsKey(portalName2)) {
            Formatter.sendMessage("One or both of the specified portals are already linked.", player);
            return;
        }

        // Link the portals
        portalLinks.put(portalName1, portalName2);
        portalLinks.put(portalName2, portalName1);

        // Save the portal links to the data file
        savePortalLinks();
        Formatter.sendMessage("Portals " + portalName1 + " and " + portalName2 + " linked successfully.", player);
    }

    // Unlink portals
    public void unlinkPortals(Player player, String portalName) {
        if (!portals.containsKey(portalName)) {
            Formatter.sendMessage("The specified portal does not exist.", player);
            return;
        }

        // Unlink the specified portal from its linked portal
        if (portalLinks.containsKey(portalName)) {
            String linkedPortalName = portalLinks.get(portalName);
            // Remove the portal links from the portal links map
            portalLinks.remove(portalName);
            portalLinks.remove(linkedPortalName);

            // Save the portal links to the data file
            savePortalLinks();

            // Send success message
            Formatter.sendMessage("Portal unlinked successfully.", player);
        } else {
            // Send error message
            Formatter.sendMessage("This portal is not linked to any other portal.", player);
        }
    }

    // Get linked portal
    public Portal getLinkedPortal(String portalName) {
        if (portalLinks.containsKey(portalName)) {
            String linkedPortalName = portalLinks.get(portalName);
            return portals.get(linkedPortalName);
        }
        return null;
    }

    // Remove portal
    public void removePortal(Player player, String portalName) {
        if (!portals.containsKey(portalName)) {
            Formatter.sendMessage("The specified portal does not exist.", player);
            return;
        }

        // Remove the specified portal
        portals.remove(portalName);

        // Remove any linked portal
        if (portalLinks.containsKey(portalName)) {
            String linkedPortalName = portalLinks.get(portalName);
            // Remove the portal links from the portal links map
            portalLinks.remove(portalName);
            portalLinks.remove(linkedPortalName);
        }

        // Save the portals and portal links to the data file
        savePortals();
        savePortalLinks();

        // Send success message
        Formatter.sendMessage("Portal removed successfully.", player);
    }

    // List portals
    public void listPortals(Player player) {
        player.sendMessage(ChatColor.AQUA + "List of Portals:");
        for (String portalName : portals.keySet()) {
            player.sendMessage(ChatColor.GREEN + "- " + portalName);
        }
    }

    // List portal links
    public void listPortalLinks(Player player) {
        player.sendMessage(ChatColor.AQUA + "List of Portal Links:");
        for (String portalName : portalLinks.keySet()) {
            String linkedPortalName = portalLinks.get(portalName);
            player.sendMessage(ChatColor.GREEN + "- " + portalName + " -> " + linkedPortalName);
        }
    }

    // Get portal name from location
    private String getPortalNameFromLocation(Location location) {
        for (Portal portal : portals.values()) {
            if (portal.getLocation().equals(location)) {
                return portal.getName();
            }
        }
        return null;
    }

    // Disable the plugin and save data file
    public void disable(JavaPlugin plugin) {
        // Save the portals and portal links to the data file
        savePortals();
        savePortalLinks();
        // Log message
        Formatter.logMessage("Portals module shutting down...", server);
        // Cancel all tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    // EVENTS //
    // Check if a block is placed on a portal block
    @EventHandler
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        Location placedBlockLocation = placedBlock.getLocation();
        Location portalBlockLocation = placedBlockLocation.clone();
        portalBlockLocation.setY(portalBlockLocation.getBlockY() - 1); // Adjust Y-coordinate here
        String portalName = getPortalNameFromLocation(portalBlockLocation);

        // If block is placed on a portal block, cancel the event
        if (placedBlock.getType() != null) {
            if (portalName != null) {
                event.setCancelled(true);
            }
        }
    }

    // Teleport player to linked portal
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Stop portal block from being broken
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock.getType() == portalBlock) {
                Location clickedBlockLocation = clickedBlock.getLocation();
                String portalName = getPortalNameFromLocation(clickedBlockLocation);
                if (portalName != null) {
                    event.setCancelled(true);
                }
            }
        }

        // Teleport player to linked portal if they click on a portal block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock.getType() == portalBlock) {
                Location clickedBlockLocation = clickedBlock.getLocation();
                String portalName = getPortalNameFromLocation(clickedBlockLocation);
                if (portalName != null) {
                    Portal linkedPortal = getLinkedPortal(portalName);
                    if (linkedPortal != null) {
                        final Location destination = linkedPortal.getLocation();
                        // Create a temporary object to avoid editing the original location
                        Location tempDestionation = destination.clone();

                        // Play ping sound
                        event.getPlayer().playSound(event.getPlayer().getLocation(), "block.note_block.pling", 1, 1);
                        // Teleport player above linked portal
                        tempDestionation.setY(tempDestionation.getBlockY() + 1); // Adjust Y-coordinate here
                        event.getPlayer().teleport(tempDestionation);
                    }
                }
            }
        }
    }

    // COMMANDS //
    // Command handler
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Formatter.logMessage("This command can only be executed by a player.", server);
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("portal")) {
            if (args.length == 0) {
                Formatter.sendMessage(
                        "Usage: /portal create <name> | /portal link <portal1> <portal2> | /portal unlink <portal> | /portal remove <name> | /portal list",
                        player);
                return true;
            }

            String subCommand = args[0];

            if (subCommand.equalsIgnoreCase("create")) {
                if (args.length < 2) {
                    Formatter.sendMessage("Usage: /portal create <name>", player);
                    return true;
                }

                String portalName = args[1];
                createPortal(player, portalName);
                return true;
            }

            if (subCommand.equalsIgnoreCase("link")) {
                if (args.length < 3) {
                    Formatter.sendMessage("Usage: /portal link <portal1> <portal2>", player);
                    return true;
                }

                String portalName1 = args[1];
                String portalName2 = args[2];
                linkPortals(player, portalName1, portalName2);
                return true;
            }

            if (subCommand.equalsIgnoreCase("unlink")) {
                if (args.length < 2) {
                    Formatter.sendMessage("Usage: /portal unlink <portal>", player);
                    return true;
                }

                String portalName = args[1];
                unlinkPortals(player, portalName);
                return true;
            }

            if (subCommand.equalsIgnoreCase("remove")) {
                if (args.length < 2) {
                    Formatter.sendMessage("Usage: /portal remove <name>", player);
                    return true;
                }

                String portalName = args[1];
                removePortal(player, portalName);
                return true;
            }

            if (subCommand.equalsIgnoreCase("list")) {
                listPortals(player);
                return true;
            }

            if (subCommand.equalsIgnoreCase("listlinks")) {
                listPortalLinks(player);
                return true;
            }
        }

        return true;
    }
}
