package xyz.theillusions;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.theillusions.events.EventListener;
import xyz.theillusions.portals.CustomPortalManager;
import xyz.theillusions.portals.PortalTabCompleter;

public class App extends JavaPlugin {
    private CustomPortalManager  portalManager;

    @Override
    public void onEnable() {
        // Portal stuff
        portalManager = new CustomPortalManager(this);
        getCommand("portal").setExecutor(portalManager);
        getCommand("portal").setTabCompleter(new PortalTabCompleter(portalManager));
        getCommand("broadcast").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(portalManager, this);

        // Event listener
        new EventListener(this);

        Formatter.logMessage("Cardinal System fully engaged.", getServer());
    }

    @Override
    public void onDisable() {
        portalManager.disable(this);
        Formatter.logMessage("Cardinal System has disengaged.", getServer());
    }

}
