package me.ddggdd135.slimeae.integrations.networks;

import io.github.sefiraat.networks.network.barrel.BarrelType;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ddggdd135.guguslimefunlib.items.ItemKey;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class StorageToBarrelWrapper extends BarrelIdentity {
    protected final IStorage storage;

    public StorageToBarrelWrapper(@Nonnull Location location, @Nonnull IStorage storage, @Nonnull ItemKey key) {
        super(location, key.getItemStack(), storage.getStorage().getOrDefault(key, 0L), BarrelType.UNKNOWN);
        this.storage = storage;
    }

    @Override
    @Nullable public ItemStack requestItem(@Nonnull ItemRequest itemRequest) {
        if (itemRequest.getItemStack() == null
                || itemRequest.getItemStack().getType().isAir()) return null;
        if (!SlimefunUtils.isItemSimilar(getItemStack(), itemRequest.getItemStack(), true, false)) return null;

        ItemStack[] itemStacks = storage.takeItem(new me.ddggdd135.slimeae.api.items.ItemRequest(
                        new ItemKey(itemRequest.getItemStack()),
                        Math.min(itemRequest.getItemStack().getMaxStackSize(), itemRequest.getAmount())))
                .toItemStacks();
        if (itemStacks.length == 1) return itemStacks[0];
        return null;
    }

    @Override
    public void depositItemStack(ItemStack[] itemStacks) {
        storage.pushItem(itemStacks);
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
