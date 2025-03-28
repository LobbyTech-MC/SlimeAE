package me.ddggdd135.slimeae.core.slimefun;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ddggdd135.slimeae.api.abstracts.MEBus;
import me.ddggdd135.slimeae.api.autocraft.CraftType;
import me.ddggdd135.slimeae.api.autocraft.CraftingRecipe;
import me.ddggdd135.slimeae.api.interfaces.IMERealCraftDevice;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.core.NetworkInfo;
import me.ddggdd135.slimeae.core.items.MenuItems;
import me.ddggdd135.slimeae.utils.ItemUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class CookingAllocator extends MEBus implements IMERealCraftDevice {
    private final Map<Block, CraftingRecipe> recipeMap = new HashMap<>();
    private final Set<Block> running = new HashSet<>();

    public CookingAllocator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(onBlockBreak());
    }

    @Nonnull
    protected BlockBreakHandler onBlockBreak() {
        return new SimpleBlockBreakHandler() {

            @Override
            public void onBlockBreak(@Nonnull Block block) {
                running.remove(block);

                BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
                if (blockMenu == null) return;

                for (int slot : getCardSlots()) {
                    ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack != null
                            && itemStack.getType() != Material.AIR
                            && !(SlimefunUtils.isItemSimilar(itemStack, MenuItems.CARD, true, false))) {
                        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                    }
                }
            }
        };
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    public boolean isSupport(@Nonnull Block block, @Nonnull CraftingRecipe recipe) {
        return recipe.getCraftType() == CraftType.COOKING;
    }

    @Override
    public boolean canStartCrafting(@Nonnull Block block, @Nonnull CraftingRecipe recipe) {
        if (!isSupport(block, recipe)) return false;
        if (running.contains(block)) return false;
        BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
        if (blockMenu == null) return false;

        block = block.getRelative(getDirection(blockMenu));
        blockMenu = StorageCacheUtils.getMenu(block.getLocation());
        if (block.getBlockData().getMaterial().isAir()) return false;
        if (blockMenu != null) {
            int[] inputSlots =
                    blockMenu.getPreset().getSlotsAccessedByItemTransport(blockMenu, ItemTransportFlow.INSERT, null);

            int[] outputSlots =
                    blockMenu.getPreset().getSlotsAccessedByItemTransport(blockMenu, ItemTransportFlow.WITHDRAW, null);

            return InvUtils.fitAll(blockMenu.getInventory(), recipe.getInput(), inputSlots)
                    && InvUtils.fitAll(blockMenu.getInventory(), recipe.getOutput(), outputSlots);
        }

        return false;
    }

    @Override
    public void startCrafting(@Nonnull Block block, @Nonnull CraftingRecipe recipe) {
        BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
        ItemUtils.getStorage(block.getRelative(getDirection(blockMenu)), false, false)
                .pushItem(recipe.getInput());
        running.add(block);
        recipeMap.put(block, recipe);
    }

    @Override
    public boolean isFinished(@Nonnull Block block) {
        if (!recipeMap.containsKey(block)) return false;
        if (!running.contains(block)) return false;
        if (!isSupport(block, recipeMap.get(block))) return false;
        BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
        if (blockMenu == null) return false;

        CraftingRecipe recipe = recipeMap.get(block);
        block = block.getRelative(getDirection(blockMenu));
        IStorage storage = ItemUtils.getStorage(block, false, false, true);
        if (storage == null) return false;
        return storage.contains(ItemUtils.createRequests(ItemUtils.getAmounts(recipe.getOutput())));
    }

    @Override
    @Nullable public CraftingRecipe getFinishedCraftingRecipe(@Nonnull Block block) {
        return recipeMap.getOrDefault(block, null);
    }

    @Override
    public void finishCrafting(@Nonnull Block block) {
        BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
        ItemUtils.getStorage(block.getRelative(getDirection(blockMenu)), false, true)
                .tryTakeItem(ItemUtils.createRequests(
                        ItemUtils.getAmounts(recipeMap.get(block).getOutput())));
        running.remove(block);
    }

    @Override
    public void onNetworkUpdate(Block block, NetworkInfo networkInfo) {}

    @Override
    public void onMEBusTick(Block block, SlimefunItem item, SlimefunBlockData data) {
        // 流程分配器不需要每tick处理，保持空实现
    }

    @Override
    public void onNetworkTick(Block block, NetworkInfo networkInfo) {}
}
