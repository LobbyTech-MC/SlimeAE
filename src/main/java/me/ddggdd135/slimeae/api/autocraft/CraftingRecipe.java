package me.ddggdd135.slimeae.api.autocraft;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import me.ddggdd135.guguslimefunlib.api.ItemHashMap;
import me.ddggdd135.slimeae.utils.CraftItemStackUtils;
import me.ddggdd135.slimeae.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class CraftingRecipe {
    private final CraftType craftType;
    private final ItemStack[] input;
    private final ItemStack[] output;
    private ItemHashMap<Long> inputAmounts;
    private ItemHashMap<Long> outputAmounts;

    public CraftingRecipe(@Nonnull CraftType craftType, @Nonnull ItemStack[] input, @Nonnull ItemStack[] output) {
        this.craftType = craftType;
        this.input = CraftItemStackUtils.asCraftCopy(input);
        this.output = CraftItemStackUtils.asCraftCopy(output);
    }

    public CraftingRecipe(@Nonnull CraftType craftType, @Nonnull ItemStack[] input, @Nonnull ItemStack output) {
        this(craftType, input, new ItemStack[] {output});
    }

    @Nonnull
    public CraftType getCraftType() {
        return craftType;
    }

    @Nonnull
    public ItemStack[] getInput() {
        return input.clone();
    }

    @Nonnull
    public ItemStack[] getOutput() {
        return output.clone();
    }

    @Nonnull
    public ItemHashMap<Long> getInputAmounts() {
        if (inputAmounts == null) inputAmounts = ItemUtils.getAmounts(input);

        return inputAmounts;
    }

    @Nonnull
    public ItemHashMap<Long> getOutputAmounts() {
        if (outputAmounts == null) outputAmounts = ItemUtils.getAmounts(output);

        return outputAmounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftingRecipe that = (CraftingRecipe) o;
        return craftType == that.craftType
                && ItemUtils.matchesAll(input, that.input, true)
                && ItemUtils.matchesAll(output, that.output, true);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(craftType);
        result = 31 * result + Arrays.hashCode(input);
        result = 31 * result + Arrays.hashCode(output);
        return result;
    }
}