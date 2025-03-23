package me.ddggdd135.slimeae.core;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ddggdd135.guguslimefunlib.api.AEMenu;
import me.ddggdd135.guguslimefunlib.items.AdvancedCustomItemStack;
import me.ddggdd135.guguslimefunlib.libraries.colors.CMIChatColor;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.ConcurrentHashSet;
import me.ddggdd135.slimeae.api.autocraft.AutoCraftingSession;
import me.ddggdd135.slimeae.api.autocraft.CraftingRecipe;
import me.ddggdd135.slimeae.api.interfaces.IDisposable;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.api.items.ItemStorage;
import me.ddggdd135.slimeae.api.items.StorageCollection;
import me.ddggdd135.slimeae.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NetworkInfo implements IDisposable {
    private final Location controller;
    private Set<Location> children = new ConcurrentHashSet<>();
    private final Set<Location> craftingHolders = new ConcurrentHashSet<>();
    private final Map<Location, Set<CraftingRecipe>> recipeMap = new ConcurrentHashMap<>();
    private IStorage storage = new StorageCollection();
    private IStorage storageNoNetworks = new StorageCollection();
    private final Set<AutoCraftingSession> craftingSessions = new ConcurrentHashSet<>();
    private final AEMenu autoCraftingMenu = new AEMenu("&e自动合成任务");
    private final ItemStorage tmpStorage = new ItemStorage();

    private static int maxCraftingSessions;
    private static int maxCraftingAmount;

    public static int getMaxCraftingSessions() {
        return maxCraftingSessions;
    }

    public static int getMaxCraftingAmount() {
        return maxCraftingAmount;
    }

    // 重载配置
    public static void reloadConfig() {
        maxCraftingSessions = SlimeAEPlugin.getInstance().getConfig().getInt("auto-crafting.max-sessions", 32);
        maxCraftingAmount = SlimeAEPlugin.getInstance().getConfig().getInt("auto-crafting.max-amount", 32768);
    }

    // 静态初始化块,在类加载时加载配置
    static {
        reloadConfig();
    }

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
        tmpStorage.setReadonly(true);
    }

    public NetworkInfo(@Nonnull Location controller, @Nonnull Set<Location> children) {
        this.controller = controller;
        this.children = children;
        autoCraftingMenu.setSize(54);
        tmpStorage.setReadonly(true);
    }

    @Nonnull
    public IStorage getStorage() {
        return storage;
    }

    public void setStorage(@Nonnull IStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public IStorage getStorageNoNetworks() {
        return storageNoNetworks;
    }

    public void setStorageNoNetworks(@Nonnull IStorage storage) {
        this.storageNoNetworks = storage;
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
            // fix NullPointException in here
            Set<CraftingRecipe> recipes1 = recipeMap.get(location);
            if (null != recipes1) {
                recipes.addAll(recipes1);
            }
        }
        return recipes;
    }

    @Nullable public CraftingRecipe getRecipeFor(@Nonnull ItemStack output) {
        for (CraftingRecipe recipe : getRecipes()) {
            for (ItemStack itemStack : recipe.getOutput()) {
                if (itemStack.asOne().equals(output.asOne())) return recipe;
            }
        }

        return null;
    }

    @Nonnull
    public Set<AutoCraftingSession> getCraftingSessions() {
        return craftingSessions;
    }

    public void openAutoCraftingSessionsMenu(@Nonnull Player player) {
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
                itemStack.setAmount((int) Math.min(64, session.getCount()));
            } else {
                itemStack = new AdvancedCustomItemStack(
                        Material.BARREL,
                        "&e&l多物品",
                        Arrays.stream(itemStacks)
                                .map(x -> "  &e- &f" + ItemUtils.getItemName(x) + "&f x " + x.getAmount())
                                .toArray(String[]::new));
                itemStack.setAmount((int) Math.min(64, session.getCount()));
            }

            List<String> lore = itemStack.getLore();
            if (lore == null) lore = new ArrayList<>();
            lore.add("");
            lore.add("&e点击查看");
            itemStack.setLore(CMIChatColor.translate(lore));

            autoCraftingMenu.replaceExistingItem(i, itemStack);
            autoCraftingMenu.addMenuClickHandler(i, (player, i1, itemStack1, clickAction) -> {
                session.showGUI(player);
                return false;
            });
            i++;
        }
        autoCraftingMenu.getContents();
    }

    @Nonnull
    public ItemStorage getTempStorage() {
        return tmpStorage;
    }
}
