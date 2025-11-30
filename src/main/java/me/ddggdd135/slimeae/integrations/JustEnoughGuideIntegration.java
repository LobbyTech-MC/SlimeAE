package me.ddggdd135.slimeae.integrations;

import me.ddggdd135.slimeae.api.interfaces.Integration;
import org.bukkit.Bukkit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
public class JustEnoughGuideIntegration implements Integration {
    private boolean cache = false;
    private boolean isCached = false;

    @Override
    @Async
    public boolean isLoaded() {
        if (!isCached) {
            cache = Bukkit.getPluginManager().isPluginEnabled("JustEnoughGuide");
            isCached = true;
        }
        return cache;
    }
}
