package me.ddggdd135.slimeae.integrations;

import org.bukkit.Bukkit;

import me.ddggdd135.slimeae.api.interfaces.Integration;

public class ExoticGardenIntegration implements Integration {
    private boolean cache = false;
    private boolean isCached = false;

    @Override
    public boolean isLoaded() {
        if (!isCached) {
            cache = Bukkit.getPluginManager().isPluginEnabled("ExoticGarden");
            isCached = true;
        }
        return cache;
    }
}
