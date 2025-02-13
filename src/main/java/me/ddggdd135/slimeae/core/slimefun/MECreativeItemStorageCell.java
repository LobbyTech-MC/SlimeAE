package me.ddggdd135.slimeae.core.slimefun;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public class MECreativeItemStorageCell extends MEItemStorageCell {
    public MECreativeItemStorageCell(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, Integer.MAX_VALUE);
    }

    @Override
    public void setSize(long size) {}
}
