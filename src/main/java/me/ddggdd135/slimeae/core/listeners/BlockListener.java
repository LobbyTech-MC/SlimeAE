package me.ddggdd135.slimeae.core.listeners;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.core.NetworkInfo;
import me.ddggdd135.slimeae.core.recipes.SlimefunAERecipeTypes;
import me.ddggdd135.slimeae.core.slimefun.CraftingMonitor;

public class BlockListener implements Listener {
    private static void doDrop(Location location, ItemStack itemStack) {
        Random random = new Random();
        int i = random.nextInt(0, 100);
        if (i <= 55) {
            ItemStack result = itemStack.clone();
            result.setAmount(random.nextInt(1, itemStack.getAmount() + 1));
            location.getWorld().dropItem(location, result);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (slimefunItem.getRecipeType() == SlimefunAERecipeTypes.BLOCK_DESTROY) {
                for (ItemStack itemStack : slimefunItem.getRecipe()) {
                    if (itemStack == null || itemStack.getType().isAir()) continue;
                    SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                    if (sfItem != null) {
                        SlimefunItem b =
                                StorageCacheUtils.getSfItem(e.getBlock().getLocation());
                        if (b == sfItem) {
                            doDrop(e.getBlock().getLocation(), slimefunItem.getRecipeOutput());
                            e.setDropItems(false);
                        }
                    } else if (e.getBlock().getType() == itemStack.getType()) {
                        doDrop(e.getBlock().getLocation(), slimefunItem.getRecipeOutput());
                        e.setDropItems(false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerRightClickEvent e) {
        if (e.getSlimefunBlock().isEmpty()) return;
        if (e.getSlimefunBlock().get() instanceof CraftingMonitor) {
            Block block = e.getClickedBlock().get();
            NetworkInfo info = SlimeAEPlugin.getNetworkData().getNetworkInfo(block.getLocation());
            if (info == null) return;
            info.openAutoCraftingSessionsMenu(e.getPlayer());
            e.cancel();
        }
    }
}
