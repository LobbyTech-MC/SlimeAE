package me.ddggdd135.slimeae.utils;

import static me.ddggdd135.slimeae.api.interfaces.IMEObject.Valid_Faces;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.interfaces.IMEController;
import me.ddggdd135.slimeae.api.interfaces.IMECraftHolder;
import me.ddggdd135.slimeae.api.interfaces.IMEObject;
import me.ddggdd135.slimeae.api.interfaces.IMEStorageObject;

public class NetworkUtils {
    public static void scan(Block block, Set<Location> blocks) {
        Stack<Location> stack = new Stack<>();
        stack.push(block.getLocation());
        while (!stack.empty()) {
            Location next = stack.pop();
            for (BlockFace blockFace : Valid_Faces) {
                Location testLocation = next.clone().add(blockFace.getDirection());
                if (blocks.contains(testLocation)) continue;
                if (SlimeAEPlugin.getNetworkData().AllNetworkBlocks.containsKey(testLocation)) {
                    blocks.add(testLocation);
                    stack.push(testLocation);
                } else {
                    SlimefunBlockData blockData = StorageCacheUtils.getBlock(testLocation);
                    if (blockData == null) {
                        continue;
                    }
                    SlimefunItem slimefunItem = SlimefunItem.getById(blockData.getSfId());
                    if (slimefunItem instanceof IMEObject IMEObject) {
                        blocks.add(testLocation);
                        SlimeAEPlugin.getNetworkData().AllNetworkBlocks.put(testLocation, IMEObject);

                        if (slimefunItem instanceof IMEController IMEController) {
                            SlimeAEPlugin.getNetworkData().AllControllers.put(testLocation, IMEController);
                        }

                        if (slimefunItem instanceof IMEStorageObject IMEStorageObject) {
                            SlimeAEPlugin.getNetworkData().AllStorageObjects.put(testLocation, IMEStorageObject);
                        }

                        if (slimefunItem instanceof IMECraftHolder IMECraftHolder) {
                            SlimeAEPlugin.getNetworkData().AllCraftHolders.put(testLocation, IMECraftHolder);
                        }

                        stack.push(testLocation);
                    }
                }
            }
        }
    }

    public static Set<Location> scan(Block block) {
        Set<Location> result = new HashSet<>();
        scan(block, result);
        return result;
    }
}
