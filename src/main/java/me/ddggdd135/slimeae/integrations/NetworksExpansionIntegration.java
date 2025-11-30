package me.ddggdd135.slimeae.integrations;

import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import me.ddggdd135.slimeae.api.interfaces.Integration;
import me.ddggdd135.slimeae.integrations.networks.StorageToBarrelWrapper;
import org.bukkit.Bukkit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
public class NetworksExpansionIntegration implements Integration {
    private boolean cache = false;
    private boolean isCached = false;

    @Override
    @Async
    public boolean isLoaded() {
        if (!isCached) {
            if (Bukkit.getPluginManager().isPluginEnabled("Networks")) {
                try {
                    Class.forName("com.ytdd9527.networksexpansion.utils.JavaUtil");
                    cache = true;
                } catch (ClassNotFoundException e) {
                    cache = false;
                }
            }
            isCached = true;
        }
        return cache;
    }

    @Async
    public <T> T doBannedTask(NetworkRoot root, Function<NetworkRoot, T> action) {
        Set<BarrelIdentity> inputBaned = new HashSet<>();
        Set<BarrelIdentity> outputBaned = new HashSet<>();
        root.getInputAbleBarrels().removeIf(x -> {
            if (x instanceof StorageToBarrelWrapper) {
                inputBaned.add(x);
                return true;
            }

            return false;
        });
        root.getOutputAbleBarrels().removeIf(x -> {
            if (x instanceof StorageToBarrelWrapper) {
                outputBaned.add(x);
                return true;
            }

            return false;
        });
        T result = action.apply(root);
        root.getInputAbleBarrels().addAll(inputBaned);
        root.getOutputAbleBarrels().addAll(outputBaned);

        return result;
    }

    @Async
    public void doBannedTask(NetworkRoot root, Consumer<NetworkRoot> action) {
        Set<BarrelIdentity> inputBaned = new HashSet<>();
        Set<BarrelIdentity> outputBaned = new HashSet<>();
        root.getInputAbleBarrels().removeIf(x -> {
            if (x instanceof StorageToBarrelWrapper) {
                inputBaned.add(x);
                return true;
            }

            return false;
        });
        root.getOutputAbleBarrels().removeIf(x -> {
            if (x instanceof StorageToBarrelWrapper) {
                outputBaned.add(x);
                return true;
            }

            return false;
        });
        action.accept(root);
        root.getInputAbleBarrels().addAll(inputBaned);
        root.getOutputAbleBarrels().addAll(outputBaned);
    }
}
