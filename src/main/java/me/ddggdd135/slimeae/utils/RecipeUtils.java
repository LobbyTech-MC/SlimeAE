package me.ddggdd135.slimeae.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ddggdd135.guguslimefunlib.api.abstracts.AbstractMachineBlock;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.CraftingRecipe;
import me.ddggdd135.slimeae.api.autocraft.CraftType;
import me.ddggdd135.slimeae.core.items.SlimefunAEItems;
import me.ddggdd135.slimeae.core.recipes.SlimefunAERecipeTypes;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.sfiguz7.transcendence.lists.TEItems;
import me.sfiguz7.transcendence.lists.TERecipeType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.CraftingRecipe;
import me.ddggdd135.slimeae.api.autocraft.CraftType;
import me.ddggdd135.slimeae.core.recipes.SlimefunAERecipeTypes;
import me.sfiguz7.transcendence.lists.TERecipeType;

public class RecipeUtils {
    public static final Map<RecipeType, SlimefunItem> SUPPORTED_RECIPE_TYPES = new HashMap<>();
    public static final Map<RecipeType, SlimefunItem> CRAFTING_TABLE_TYPES = new HashMap<>();

    @Nullable public static CraftingRecipe getRecipe(@Nonnull ItemStack itemStack) {
        return getRecipe(itemStack, SUPPORTED_RECIPE_TYPES);
    }

    @Nullable public static CraftingRecipe getRecipe(@Nonnull ItemStack itemStack, Map<RecipeType, SlimefunItem> supported) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem != null) {
            if (supported.containsKey(slimefunItem.getRecipeType())) {
                return new CraftingRecipe(
                        CraftType.CRAFTING_TABLE, slimefunItem.getRecipe(), slimefunItem.getRecipeOutput());
            } else {
                for (Map.Entry<RecipeType, SlimefunItem> entry : supported.entrySet()) {
                    if (entry.getValue() == null) continue;
                    for (ItemStack[] input : getInputs(entry.getKey())) {
                        ItemStack[] outputs = getOutputs(entry.getKey(), input);
                        if (outputs.length != 1) continue;
                        ItemStack output = outputs[0];
                        if (SlimefunUtils.isItemSimilar(itemStack, output, true, false)) {
                            return new CraftingRecipe(CraftType.CRAFTING_TABLE, input, output);
                        }
                    }
                }
            }

            return new CraftingRecipe(CraftType.COOKING, slimefunItem.getRecipe(), slimefunItem.getRecipeOutput());
        }
        List<Recipe> minecraftRecipe = Bukkit.getRecipesFor(itemStack);
        for (Recipe recipe : minecraftRecipe) {
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                return new CraftingRecipe(
                        CraftType.CRAFTING_TABLE,
                        shapedRecipe.getIngredientMap().values().toArray(ItemStack[]::new),
                        new ItemStack(
                                shapedRecipe.getResult().getType(),
                                shapedRecipe.getResult().getAmount()));
            }
            if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                return new CraftingRecipe(
                        CraftType.CRAFTING_TABLE,
                        shapelessRecipe.getIngredientList().toArray(ItemStack[]::new),
                        new ItemStack(
                                shapelessRecipe.getResult().getType(),
                                shapelessRecipe.getResult().getAmount()));
            }
            if (recipe instanceof CookingRecipe cookingRecipe)
                return new CraftingRecipe(
                        CraftType.COOKING,
                        new ItemStack[] {
                            new ItemStack(
                                    cookingRecipe.getInput().getType(),
                                    cookingRecipe.getInput().getAmount())
                        },
                        new ItemStack(
                                cookingRecipe.getResult().getType(),
                                cookingRecipe.getResult().getAmount()));
        }
        return null;
    }

    @Nullable public static CraftingRecipe getRecipe(@Nonnull ItemStack[] input) {
        return getRecipe(input, SUPPORTED_RECIPE_TYPES);
    }

    @Nullable public static CraftingRecipe getRecipe(@Nonnull ItemStack[] input, Map<RecipeType, SlimefunItem> supported) {
        for (Map.Entry<RecipeType, SlimefunItem> entry : supported.entrySet()) {
            if (entry.getValue() == null) continue;
            in:
            for (ItemStack[] input1 : getInputs(entry.getKey())) {
                for (int i = 0; i < Math.max(input.length, input1.length); i++) {
                    ItemStack x = null;
                    ItemStack y = null;
                    if (input.length > i) {
                        x = input[i];
                    }
                    if (input1.length > i) {
                        y = input1[i];
                    }
                    if (!SlimefunUtils.isItemSimilar(x, y, true, false)) {
                        continue in;
                    }
                }

                return new CraftingRecipe(CraftType.CRAFTING_TABLE, input1, getOutputs(entry.getKey(), input1));
            }
        }

        Recipe minecraftRecipe =
                Bukkit.getCraftingRecipe(input, Bukkit.getWorlds().get(0));
        if (minecraftRecipe instanceof ShapedRecipe shapedRecipe) {
            return new CraftingRecipe(
                    CraftType.CRAFTING_TABLE,
                    shapedRecipe.getIngredientMap().values().toArray(ItemStack[]::new),
                    new ItemStack(
                            shapedRecipe.getResult().getType(),
                            shapedRecipe.getResult().getAmount()));
        }
        if (minecraftRecipe instanceof ShapelessRecipe shapelessRecipe) {
            return new CraftingRecipe(
                    CraftType.CRAFTING_TABLE,
                    shapelessRecipe.getIngredientList().toArray(ItemStack[]::new),
                    new ItemStack(
                            shapelessRecipe.getResult().getType(),
                            shapelessRecipe.getResult().getAmount()));
        }
        if (minecraftRecipe instanceof CookingRecipe cookingRecipe)
            return new CraftingRecipe(
                    CraftType.COOKING,
                    new ItemStack[] {
                        new ItemStack(
                                cookingRecipe.getInput().getType(),
                                cookingRecipe.getInput().getAmount())
                    },
                    new ItemStack(
                            cookingRecipe.getResult().getType(),
                            cookingRecipe.getResult().getAmount()));

        return null;
    }

    @Nullable public static CraftingRecipe getRecipe(@Nonnull ItemStack[] input, @Nonnull ItemStack[] output) {
        return getRecipe(input, output, SUPPORTED_RECIPE_TYPES);
    }

    @Nullable public static CraftingRecipe getRecipe(
            @Nonnull ItemStack[] input, @Nonnull ItemStack[] output, Map<RecipeType, SlimefunItem> supported) {
        for (Map.Entry<RecipeType, SlimefunItem> entry : supported.entrySet()) {
            if (entry.getValue() == null) continue;
            in:
            for (ItemStack[] input1 : getInputs(entry.getKey())) {
                for (int i = 0; i < Math.max(input.length, input1.length); i++) {
                    ItemStack x = null;
                    ItemStack y = null;
                    if (input.length > i) {
                        x = input[i];
                    }
                    if (input1.length > i) {
                        y = input1[i];
                    }
                    if (!SlimefunUtils.isItemSimilar(x, y, true, false)) {
                        continue in;
                    }
                }

                ItemStack[] output1 = getOutputs(entry.getKey(), input1);

                for (int i = 0; i < Math.max(output.length, output1.length); i++) {
                    ItemStack x = null;
                    ItemStack y = null;
                    if (output.length > i) {
                        x = output[i];
                    }
                    if (output1.length > i) {
                        y = output1[i];
                    }
                    if (!SlimefunUtils.isItemSimilar(x, y, true, false)) {
                        continue in;
                    }
                }

                return new CraftingRecipe(CraftType.CRAFTING_TABLE, input1, output1);
            }
        }

        Recipe minecraftRecipe =
                Bukkit.getCraftingRecipe(input, Bukkit.getWorlds().get(0));
        if (minecraftRecipe instanceof ShapedRecipe shapedRecipe) {
            ItemStack out = new ItemStack(
                    shapedRecipe.getResult().getType(), shapedRecipe.getResult().getAmount());
            if (output.length == 1 && SlimefunUtils.isItemSimilar(output[0], out, true, false))
                return new CraftingRecipe(
                        CraftType.CRAFTING_TABLE,
                        shapedRecipe.getIngredientMap().values().toArray(ItemStack[]::new),
                        output);
        }
        if (minecraftRecipe instanceof ShapelessRecipe shapelessRecipe) {
            ItemStack out = new ItemStack(
                    shapelessRecipe.getResult().getType(),
                    shapelessRecipe.getResult().getAmount());
            if (output.length == 1 && SlimefunUtils.isItemSimilar(output[0], out, true, false))
                return new CraftingRecipe(
                        CraftType.CRAFTING_TABLE,
                        shapelessRecipe.getIngredientList().toArray(ItemStack[]::new),
                        new ItemStack(
                                shapelessRecipe.getResult().getType(),
                                shapelessRecipe.getResult().getAmount()));
        }
        if (minecraftRecipe instanceof CookingRecipe cookingRecipe) {
            ItemStack out = new ItemStack(
                    cookingRecipe.getResult().getType(),
                    cookingRecipe.getResult().getAmount());
            if (output.length == 1 && SlimefunUtils.isItemSimilar(output[0], out, true, false))
                return new CraftingRecipe(
                        CraftType.COOKING,
                        new ItemStack[] {
                            new ItemStack(
                                    cookingRecipe.getInput().getType(),
                                    cookingRecipe.getInput().getAmount())
                        },
                        new ItemStack(
                                cookingRecipe.getResult().getType(),
                                cookingRecipe.getResult().getAmount()));
        }

        if (output.length == 1) return getRecipe(output[0]);

        return null;
    }

    public static List<ItemStack[]> getInputs(RecipeType recipeType) {
        SlimefunItem slimefunItem = SUPPORTED_RECIPE_TYPES.get(recipeType);
        if (slimefunItem == null) return new ArrayList<>();
        if (slimefunItem instanceof MultiBlockMachine multiBlockMachine) {
            return RecipeType.getRecipeInputList(multiBlockMachine);
        }

        if (slimefunItem instanceof AContainer aContainer) {
            return aContainer.getMachineRecipes().stream()
                    .map(MachineRecipe::getInput)
                    .toList();
        }

        if (slimefunItem instanceof AbstractMachineBlock abstractMachineBlock) {
            return abstractMachineBlock.getMachineRecipes().stream()
                    .map(MachineRecipe::getInput)
                    .toList();
        }

        return new ArrayList<>();
    }

    public static ItemStack[] getOutputs(RecipeType recipeType, ItemStack[] inputs) {
        SlimefunItem slimefunItem = SUPPORTED_RECIPE_TYPES.get(recipeType);
        if (slimefunItem == null) return new ItemStack[0];
        if (slimefunItem instanceof MultiBlockMachine multiBlockMachine) {
            return new ItemStack[] {RecipeType.getRecipeOutputList(multiBlockMachine, inputs)};
        }

        if (slimefunItem instanceof AContainer aContainer) {
            List<MachineRecipe> recipes = aContainer.getMachineRecipes();
            i:
            for (MachineRecipe recipe : recipes) {
                ItemStack[] in = recipe.getInput();
                for (int i = 0; i < Math.max(in.length, inputs.length); i++) {
                    ItemStack x = null;
                    ItemStack y = null;
                    if (in.length > i) {
                        x = in[i];
                    }
                    if (inputs.length > i) {
                        y = inputs[i];
                    }
                    if (!SlimefunUtils.isItemSimilar(x, y, true, false)) {
                        continue i;
                    }
                }

                return recipe.getOutput();
            }
        }

        if (slimefunItem instanceof AbstractMachineBlock abstractMachineBlock) {
            List<MachineRecipe> recipes = abstractMachineBlock.getMachineRecipes();
            i:
            for (MachineRecipe recipe : recipes) {
                ItemStack[] in = recipe.getInput();
                for (int i = 0; i < Math.max(in.length, inputs.length); i++) {
                    ItemStack x = null;
                    ItemStack y = null;
                    if (in.length > i) {
                        x = in[i];
                    }
                    if (inputs.length > i) {
                        y = inputs[i];
                    }
                    if (!SlimefunUtils.isItemSimilar(x, y, true, false)) {
                        continue i;
                    }
                }

                return recipe.getOutput();
            }
        }

        return new ItemStack[0];
    }

    static {
        SUPPORTED_RECIPE_TYPES.put(
                RecipeType.ENHANCED_CRAFTING_TABLE, SlimefunItem.getByItem(SlimefunItems.ENHANCED_CRAFTING_TABLE));
        SUPPORTED_RECIPE_TYPES.put(SlimefunAERecipeTypes.CHARGER, SlimefunItem.getByItem(SlimefunAEItems.CHARGER));
        SUPPORTED_RECIPE_TYPES.put(SlimefunAERecipeTypes.INSCRIBER, SlimefunItem.getByItem(SlimefunAEItems.INSCRIBER));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.MAGIC_WORKBENCH, SlimefunItem.getByItem(SlimefunItems.MAGIC_WORKBENCH));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.ARMOR_FORGE, SlimefunItem.getByItem(SlimefunItems.ARMOR_FORGE));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.SMELTERY, SlimefunItem.getByItem(SlimefunItems.SMELTERY));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.ANCIENT_ALTAR, SlimefunItem.getByItem(SlimefunItems.ANCIENT_ALTAR));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.COMPRESSOR, SlimefunItem.getByItem(SlimefunItems.COMPRESSOR));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.GRIND_STONE, SlimefunItem.getByItem(SlimefunItems.GRIND_STONE));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.JUICER, SlimefunItem.getByItem(SlimefunItems.JUICER));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.ORE_CRUSHER, SlimefunItem.getByItem(SlimefunItems.ORE_CRUSHER));
        SUPPORTED_RECIPE_TYPES.put(RecipeType.PRESSURE_CHAMBER, SlimefunItem.getByItem(SlimefunItems.PRESSURE_CHAMBER));

        if (SlimeAEPlugin.getTranscEndenceIntegration().isLoaded()) {
            SUPPORTED_RECIPE_TYPES.put(TERecipeType.NANOBOT_CRAFTER, SlimefunItem.getByItem(TEItems.NANOBOT_CRAFTER));
        }

        CRAFTING_TABLE_TYPES.put(
                RecipeType.ENHANCED_CRAFTING_TABLE, SlimefunItem.getByItem(SlimefunItems.ENHANCED_CRAFTING_TABLE));

        if (SlimeAEPlugin.getTranscEndenceIntegration().isLoaded()) {
            CRAFTING_TABLE_TYPES.put(TERecipeType.NANOBOT_CRAFTER, SlimefunItem.getByItem(TEItems.NANOBOT_CRAFTER));
        }
    }
}
