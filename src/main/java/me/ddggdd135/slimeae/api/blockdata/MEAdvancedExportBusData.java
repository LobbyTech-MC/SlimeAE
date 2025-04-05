package me.ddggdd135.slimeae.api.blockdata;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.interfaces.IInventoryBlockData;

public class MEAdvancedExportBusData extends MEAdvancedBusData implements IInventoryBlockData {
    private static final NamespacedKey key = new NamespacedKey(SlimeAEPlugin.getInstance(), "MEAdvancedExportBus");
    private ItemStack[] itemStacks = new ItemStack[0];

    @Override
    @Nonnull
    public ItemStack[] getItemStacks() {
        return itemStacks;
    }

    @Override
    public void setItemStacks(@Nonnull ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    @Nonnull
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MEAdvancedExportBusData that = (MEAdvancedExportBusData) o;
        return Arrays.equals(itemStacks, that.itemStacks);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(itemStacks);
        return result;
    }
}
