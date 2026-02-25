package me.ddggdd135.slimeae.utils;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemStackSnapshot;

public class ItemStackSnapshotUtils {
    @Nonnull
    public static ItemStack clone(@Nonnull ItemStackSnapshot itemStackSnapshot) {
        ItemStack itemStack = new ItemStack(itemStackSnapshot.getType(), itemStackSnapshot.getAmount());
        if (itemStackSnapshot.hasItemMeta()) itemStack.setItemMeta(itemStackSnapshot.getItemMeta());

        return itemStack;
    }
}
