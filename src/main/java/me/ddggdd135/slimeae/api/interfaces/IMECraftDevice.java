package me.ddggdd135.slimeae.api.interfaces;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

import me.ddggdd135.slimeae.api.autocraft.CraftingRecipe;

public interface IMECraftDevice extends IMEObject {
    boolean isSupport(@Nonnull Block block, @Nonnull CraftingRecipe recipe);
}
