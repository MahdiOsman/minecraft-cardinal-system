package xyz.theillusions.events;

import xyz.theillusions.App;
import xyz.theillusions.Formatter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {

    private final App plugin; // Reference to the main plugin class
    private long spawnDelay = 5000L; // Delay in milliseconds (5 seconds)
    private long lastSpawnTime = 0L; // Last spawn time in milliseconds

    // Constructor
    public EventListener(App plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Disable join message
        event.setJoinMessage(null);
        // Send a message to the player
        Formatter.sendMessage(ChatColor.LIGHT_PURPLE + "You're bad <3", player);
        // Custom join message
        Formatter.broadcastMessage(player.getName() + " has joined the server", player.getServer());
    }

    // When a player leaves the server
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Disable leave message
        event.setQuitMessage(null);
        // Custom leave message
        Formatter.broadcastMessage(player.getName() + " has left the server", player.getServer());
    }

    @EventHandler
    public void onEndermanBlockTake(EntityChangeBlockEvent event) {
        Server server = Bukkit.getServer();

        // Check if the entity is an enderman
        if (event.getEntity().getType().equals(EntityType.ENDERMAN)) {
            // Cancel the event
            event.setCancelled(true);
            Formatter.broadcastMessage("Enderman #" + event.getEntity().getEntityId() + " get fucked <3", server);
            // Wiped from existance
            event.getEntity().remove();
        }
    }

    // Play a creeper sound when player steps on block
    @EventHandler
    public void crepperTrollOnPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock().getRelative(0, -1, 0);

        // Check if player is on block and if creeper can spawn
        if (block.getType() == Material.SEA_LANTERN && canSpawnCreeper()) {
            // Player creeper sound
            player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);

            // Spawn a creeper and then despawn it after x seconds
            Vector playerDirection = player.getLocation().getDirection();
            Vector spawnLocation = player.getLocation().add(playerDirection.multiply(3)).toVector()
                    .add(new Vector(0, 1, 0));
            final Creeper creeper = player.getWorld().spawn(spawnLocation.toLocation(player.getWorld()), Creeper.class);

            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    creeper.remove();
                }
            }, 10L);

            lastSpawnTime = System.currentTimeMillis();
        }
    }

    // Check if the creeper can spawn
    private boolean canSpawnCreeper() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastSpawnTime) >= spawnDelay;
    }
}
