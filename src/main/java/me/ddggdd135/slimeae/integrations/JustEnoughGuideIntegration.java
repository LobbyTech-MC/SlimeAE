package me.ddggdd135.slimeae.integrations;

import org.bukkit.Bukkit;

import me.ddggdd135.slimeae.api.interfaces.Integration;

public class JustEnoughGuideIntegration implements Integration {
    private boolean cache = false;
    private boolean isCached = false;

    @Override
    public boolean isLoaded() {
        if (!isCached) {
            cache = Bukkit.getPluginManager().isPluginEnabled("JustEnoughGuide");
            isCached = true;
        }
        return cache;
    }
}
