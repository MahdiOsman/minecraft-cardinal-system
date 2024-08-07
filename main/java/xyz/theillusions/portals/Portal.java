package xyz.theillusions.portals;

import org.bukkit.Location;

public class Portal {
    private String name;
    private Location location;

    // Constructor
    public Portal(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    // Getters
    public String getName() {
        return name;
    }

    // Setters
    public Location getLocation() {
        return location;
    }
}
