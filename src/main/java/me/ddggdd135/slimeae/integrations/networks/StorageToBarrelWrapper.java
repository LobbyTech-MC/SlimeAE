package me.ddggdd135.slimeae.integrations.networks;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.sefiraat.networks.network.barrel.BarrelType;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.ddggdd135.slimeae.api.StorageCollection;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.utils.NetworkUtils;

public class StorageToBarrelWrapper extends BarrelIdentity {
    protected final IStorage storage;

    public StorageToBarrelWrapper(@Nonnull Location location, @Nonnull IStorage storage, @Nonnull ItemStack itemStack) {
        super(
                location,
                itemStack,
                NetworkUtils.<Integer>doAntiNetworksTask(
                        storage, x -> x.getStorage().getOrDefault(itemStack, 0)),
                BarrelType.UNKNOWN);
        this.storage = storage;
    }

    @Override
    @Nullable public ItemStack requestItem(@Nonnull ItemRequest itemRequest) {
        if (itemRequest.getItemStack() == null
                || itemRequest.getItemStack().getType().isAir()) return null;
        if (!SlimefunUtils.isItemSimilar(getItemStack(), itemRequest.getItemStack(), true, false)) return null;
        IStorage tmp = storage;
        if (storage instanceof StorageCollection storageCollection) {
            tmp = new StorageCollection(storageCollection.getStorages().toArray(IStorage[]::new));
            Set<IStorage> storages = ((StorageCollection) tmp).getStorages();
            storages.removeIf(x -> x instanceof NetworksStorage);
        }

        ItemStack[] itemStacks = tmp.tryTakeItem(new me.ddggdd135.slimeae.api.ItemRequest(
                itemRequest.getItemStack(),
                Math.min(itemRequest.getItemStack().getMaxStackSize(), itemRequest.getAmount())));
        if (itemStacks.length == 1) return itemStacks[0];
        return null;
    }

    @Override
    public void depositItemStack(ItemStack[] itemStacks) {
        IStorage tmp = storage;
        if (storage instanceof StorageCollection storageCollection) {
            tmp = new StorageCollection(storageCollection.getStorages().toArray(IStorage[]::new));
            Set<IStorage> storages = ((StorageCollection) tmp).getStorages();
            storages.removeIf(x -> x instanceof NetworksStorage);
        }
        tmp.pushItem(itemStacks);
    }

    @Override
    public int[] getInputSlot() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlot() {
        return new int[0];
    }
}
