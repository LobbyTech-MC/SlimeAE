package me.ddggdd135.slimeae.core.slimefun;

import static me.ddggdd135.slimeae.core.slimefun.terminals.METerminal.ALPHABETICAL_SORT;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ddggdd135.guguslimefunlib.GuguSlimefunLib;
import me.ddggdd135.guguslimefunlib.libraries.colors.CMIChatColor;
import me.ddggdd135.guguslimefunlib.libraries.nbtapi.NBT;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.items.MEStorageCellCache;
import me.ddggdd135.slimeae.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

/**
 * 能源传输物品存储元件类
 * 用于存储物品的基本单元
 */
public class MEItemStorageCell extends SlimefunItem implements NotPlaceable {
    public static final String UUID_KEY = "uuid";
    public static final String SERVER_UUID_KEY = "server_uuid";
    private long size;

    public MEItemStorageCell(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, long size) {
        super(itemGroup, item, recipeType, recipe);
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static long getSize(@Nonnull ItemStack itemStack) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (!(slimefunItem instanceof MEItemStorageCell meItemStorageCell)) {
            return 0;
        } else return meItemStorageCell.getSize();
    }

    @Nullable public static MEStorageCellCache getStorage(@Nonnull ItemStack itemStack) {
        if (!(SlimefunItem.getByItem(itemStack) instanceof MEItemStorageCell)) return null;
        if (SlimefunItem.getByItem(itemStack) instanceof MECreativeItemStorageCell)
            return new MEStorageCellCache(itemStack);
        return MEStorageCellCache.getMEStorageCellCache(itemStack);
    }

    public static void saveStorage(@Nonnull ItemStack itemStack) {
        if (SlimefunItem.getByItem(itemStack) instanceof MECreativeItemStorageCell) return;

        SlimeAEPlugin.getStorageCellStorageDataController()
                .updateAsync(getStorage(itemStack).getStorageData());
        SlimeAEPlugin.getStorageCellFilterDataController()
                .updateAsync(getStorage(itemStack).getFilterData());
    }

    /**
     * 更新存储元件的物品列表
     *
     * @param itemStack 存储元件物品
     */
    public static void updateLore(@Nonnull ItemStack itemStack) {
        if (SlimefunItem.getByItem(itemStack) instanceof MECreativeItemStorageCell) return;
        MEStorageCellCache meStorageCellCache = MEStorageCellCache.getMEStorageCellCache(itemStack);
        List<String> lores = new ArrayList<>();
        lores.add(
                CMIChatColor.translate("&e已存储 " + meStorageCellCache.getStored() + "/" + meStorageCellCache.getSize()));
        lores.add("");
        Stream <Entry<ItemStack, Long>> sorted = meStorageCellCache.getStorageUnsafe().entrySet().stream().sorted(ALPHABETICAL_SORT);
        //long count = sorted.count();
        List<Entry<ItemStack, Long>> storages = sorted.toList();
        int lines = 0;
        for (Map.Entry<ItemStack, Long> entry : storages) {
            if (lines >= 8) {
                lores.add(CMIChatColor.translate("&e------还有" + (storages.size() - lines) + "项------"));
                break;
            }
            lines++;
            lores.add(CMIChatColor.translate("&e" + ItemUtils.getItemName(entry.getKey()) + " - " + entry.getValue()));
        }
        itemStack.setLore(lores);
    }

    @Nonnull
    public static UUID getServerUUID(@Nonnull ItemStack itemStack) {
        UUID uuid = NBT.get(itemStack, x -> {
            return x.getUUID(SERVER_UUID_KEY);
        });
        if (uuid == null) {
            NBT.modify(itemStack, x -> {
                x.setUUID(SERVER_UUID_KEY, GuguSlimefunLib.getServerUUID());
            });
            return GuguSlimefunLib.getServerUUID();
        }

        return uuid;
    }

    public static boolean isCurrentServer(@Nonnull ItemStack itemStack) {
        return getServerUUID(itemStack).equals(GuguSlimefunLib.getServerUUID());
    }
}
