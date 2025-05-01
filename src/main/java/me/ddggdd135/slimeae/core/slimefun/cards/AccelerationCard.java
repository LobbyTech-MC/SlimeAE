package me.ddggdd135.slimeae.core.slimefun.cards;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import me.ddggdd135.slimeae.api.abstracts.Card;
import me.ddggdd135.slimeae.api.abstracts.MEBus;
import me.ddggdd135.slimeae.core.slimefun.MEIOPort;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class AccelerationCard extends Card {

    public AccelerationCard(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void onTick(Block block, SlimefunItem item, SlimefunBlockData data) {
        // 处理普通机器
        if (item instanceof MachineProcessHolder<?> processorHolder) {
            MachineOperation operation = processorHolder.getMachineProcessor().getOperation(block);
            if (operation != null && !operation.isFinished()) {
                operation.addProgress(1);
            }
            return;
        }

        // 处理能源传输总线
        if (item instanceof MEBus meBus) {
            // 额外调用一次onMEBusTick来加速处理
            meBus.onMEBusTick(block, item, data);
        }

        // 处理能源传输 输入输出端口
        if (item instanceof MEIOPort meioPort) {
            meioPort.onMEIOPortTick(block, item, data);
        }
    }
}
