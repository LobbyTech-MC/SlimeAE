package me.ddggdd135.slimeae.api.interfaces;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

import me.ddggdd135.slimeae.api.autocraft.CraftingRecipe;

public interface IMECraftHolder extends IMEObject {
    @Nonnull
    Block[] getCraftingDevices(@Nonnull Block block);

    @Nonnull
    CraftingRecipe[] getSupportedRecipes(@Nonnull Block block);
}
