package me.ddggdd135.slimeae.integrations;

import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import java.util.Arrays;
import me.ddggdd135.slimeae.api.interfaces.Integration;
import org.bukkit.Bukkit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
public class NetworksIntegration implements Integration {
    private boolean cache = false;
    private boolean isCached = false;

    @Override
    @Async
    public boolean isLoaded() {
        if (!isCached) {
            if (Bukkit.getPluginManager().isPluginEnabled("Networks-Changed")) {
                cache = true;
            }
            if (Bukkit.getPluginManager().isPluginEnabled("Networks")) {
                try {
                    Class.forName("com.ytdd9527.networksexpansion.utils.JavaUtil");
                    cache = false;
                } catch (ClassNotFoundException e) {
                    cache = true;
                }
            }
            isCached = true;
        }
        return cache;
    }

    @Async
    public ItemRequest[] asNetworkRequests(me.ddggdd135.slimeae.api.items.ItemRequest[] requests) {
        return Arrays.stream(requests)
                .map(x -> new io.github.sefiraat.networks.network.stackcaches.ItemRequest(
                        x.getKey().getItemStack(), (int) x.getAmount()))
                .toArray(io.github.sefiraat.networks.network.stackcaches.ItemRequest[]::new);
    }
}
