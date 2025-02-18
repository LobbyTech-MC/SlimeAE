package me.ddggdd135.slimeae.api.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ddggdd135.slimeae.api.autocraft.CraftingRecipe;
import org.bukkit.block.Block;

import me.ddggdd135.slimeae.api.CraftingRecipe;

public interface IMECraftDevice extends IMEObject {
    boolean isSupport(@Nonnull Block block, @Nonnull CraftingRecipe recipe);

    boolean canStartCrafting(@Nonnull Block block, @Nonnull CraftingRecipe recipe);

    void startCrafting(@Nonnull Block block, @Nonnull CraftingRecipe recipe);

    boolean isFinished(@Nonnull Block block);

    @Nullable CraftingRecipe getFinishedCraftingRecipe(@Nonnull Block block);

    void finishCrafting(@Nonnull Block block);

    boolean isGlobal(Block block);
}
