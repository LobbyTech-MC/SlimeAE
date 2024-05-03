package me.ddggdd135.slimeae.core;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.AEMenu;
import me.ddggdd135.slimeae.api.CraftingRecipe;
import me.ddggdd135.slimeae.api.StorageCollection;
import me.ddggdd135.slimeae.api.interfaces.IDisposable;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.utils.AdvancedCustomItemStack;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NetworkInfo implements IDisposable {
    private Location controller;
    private Set<Location> children = new HashSet<>();
    private Set<Location> craftingHolders = new HashSet<>();
    private Map<Location, Set<CraftingRecipe>> recipeMap = new HashMap<>();
    private IStorage storage = new StorageCollection();
    private Set<AutoCraftingSession> craftingSessions = new HashSet<>();
    private AEMenu autoCraftingMenu = new AEMenu("&e自动合成任务");

    @Nonnull
    public Location getController() {
        return controller;
    }

    @Nonnull
    public Set<Location> getChildren() {
        return children;
    }

    public NetworkInfo(@Nonnull Location controller) {
        this.controller = controller;
        autoCraftingMenu.setSize(54);
    }

    public NetworkInfo(@Nonnull Location controller, @Nonnull Set<Location> children) {
        this.controller = controller;
        this.children = children;
        autoCraftingMenu.setSize(54);
    }

    @Nonnull
    public IStorage getStorage() {
        return storage;
    }

    public void setStorage(@Nonnull IStorage storage) {
        this.storage = storage;
    }

    @Override
    public void dispose() {
        NetworkData networkData = SlimeAEPlugin.getNetworkData();
        networkData.AllNetworkData.remove(this);
    }

    @Override
    public int hashCode() {
        return controller.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkInfo that = (NetworkInfo) o;
        return controller.equals(that.controller);
    }

    @Nonnull
    public Set<Location> getCraftingHolders() {
        return craftingHolders;
    }

    @Nonnull
    public Set<CraftingRecipe> getRecipes(@Nonnull Block holder) {
        return recipeMap.get(holder.getLocation());
    }

    @Nonnull
    public Map<Location, Set<CraftingRecipe>> getRecipeMap() {
        return recipeMap;
    }

    @Nonnull
    public Set<CraftingRecipe> getRecipes() {
        Set<CraftingRecipe> recipes = new HashSet<>();
        for (Location location : craftingHolders) {
            recipes.addAll(recipeMap.get(location));
        }
        return recipes;
    }

    @Nullable public CraftingRecipe getRecipeFor(@Nonnull ItemStack output) {
        for (CraftingRecipe recipe : getRecipes()) {
            if (Arrays.asList(recipe.getOutput()).contains(output)) return recipe;
        }

        return null;
    }

    public Set<AutoCraftingSession> getCraftingSessions() {
        return craftingSessions;
    }

    public void openAutoCraftingSessionsMenu(Player player) {
        updateAutoCraftingMenu();
        autoCraftingMenu.open(player);
    }

    public void updateAutoCraftingMenu() {
        for (ItemStack content : autoCraftingMenu.getContents()) {
            if (content == null) continue;
            content.setType(Material.AIR);
        }
        List<AutoCraftingSession> sessions = getCraftingSessions().stream().toList();
        if (sessions.size() > 53) sessions = sessions.subList(sessions.size() - 53, sessions.size());
        for (int i = 0; i < 54; i++) {
            autoCraftingMenu.replaceExistingItem(i, null);
            autoCraftingMenu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
        }
        int i = 0;
        for (AutoCraftingSession session : sessions) {
            ItemStack[] itemStacks = session.getRecipe().getOutput();
            ItemStack itemStack;
            if (itemStacks.length == 1) {
                itemStack = itemStacks[0].clone();
                itemStack.setAmount(Math.min(64, session.getCount()));
            } else {
                itemStack = new AdvancedCustomItemStack(
                        Material.BARREL,
                        "&e&l多物品",
                        Arrays.stream(itemStacks)
                                .map(x -> {
                                    SlimefunItem slimefunItem = SlimefunItem.getByItem(x);
                                    if (slimefunItem != null) {
                                        return "  - " + CMIChatColor.stripColor(slimefunItem.getItemName()) + " x "
                                                + x.getAmount();
                                    } else {
                                        return "  - "
                                                + CMIMaterial.get(x.getType()).getTranslatedName() + " x "
                                                + x.getAmount();
                                    }
                                })
                                .toArray(String[]::new));
                itemStack.setAmount(Math.min(64, session.getCount()));
            }
            autoCraftingMenu.replaceExistingItem(i, itemStack);
            autoCraftingMenu.addMenuClickHandler(i, (player, i1, itemStack1, clickAction) -> {
                session.showGUI(player);
                return false;
            });
            i++;
        }
        autoCraftingMenu.getContents();
    }
}
