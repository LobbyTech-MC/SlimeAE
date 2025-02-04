package me.ddggdd135.slimeae.core.slimefun;

import static me.ddggdd135.slimeae.core.slimefun.METerminal.ALPHABETICAL_SORT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import me.ddggdd135.guguslimefunlib.GuguSlimefunLib;
import me.ddggdd135.guguslimefunlib.libraries.colors.CMIChatColor;
import me.ddggdd135.guguslimefunlib.libraries.nbtapi.NBTItem;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.MEStorageCellCache;
import me.ddggdd135.slimeae.api.ResultWithItem;
import me.ddggdd135.slimeae.utils.ItemUtils;

/**
 * ME物品存储元件类
 * 用于存储物品的基本单元
 */
public class MEItemStorageCell extends SlimefunItem implements NotPlaceable {
    public static final String UUID_KEY = "uuid";
    public static final String SERVER_UUID_KEY = "server_uuid";
    private int size;

    public MEItemStorageCell(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int size) {
        super(itemGroup, item, recipeType, recipe);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static int getSize(@Nonnull ItemStack itemStack) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (!(slimefunItem instanceof MEItemStorageCell meItemStorageCell)) {
            return 0;
        } else return meItemStorageCell.getSize();
    }

    @Nullable public static ResultWithItem<MEStorageCellCache> getStorage(@Nonnull ItemStack itemStack) {
        if (!(SlimefunItem.getByItem(itemStack) instanceof MEItemStorageCell)) return null;
        if (SlimefunItem.getByItem(itemStack) instanceof MECreativeItemStorageCell)
            return new ResultWithItem<>(new MEStorageCellCache(itemStack), itemStack);
        return MEStorageCellCache.getMEStorageCellCache(itemStack);
    }

    public static void saveStorage(@Nonnull ItemStack itemStack) {
        if (SlimefunItem.getByItem(itemStack) instanceof MECreativeItemStorageCell) return;
        //        NBTItem nbtItem = new NBTItem(itemStack);
        //        if (nbtItem.hasTag(ITEM_STORAGE_KEY)) nbtItem.removeKey(ITEM_STORAGE_KEY);
        //        NBTCompoundList list = nbtItem.getCompoundList(ITEM_STORAGE_KEY);
        //        list.clear();
        //        list.addAll(ItemUtils.toNBT(getStorage(itemStack).getStorage()));
        //        nbtItem.applyNBT(itemStack);

        SlimeAEPlugin.getStorageCellDataController()
                .updateAsync(getStorage(itemStack).getResult());
    }

    /**
     * 更新存储元件的物品列表
     *
     * @param itemStack 存储元件物品
     */
    public static ItemStack updateLore(@Nonnull ItemStack itemStack) {
        if (SlimefunItem.getByItem(itemStack) instanceof MECreativeItemStorageCell) return itemStack;
        ResultWithItem<MEStorageCellCache> result = MEStorageCellCache.getMEStorageCellCache(itemStack);
        MEStorageCellCache meStorageCellCache = result.getResult();
        ItemStack toReturn = result.getItemStack();
        List<String> lores = new ArrayList<>();
        List<Map.Entry<ItemStack, Integer>> storages = meStorageCellCache.getStorage().entrySet().stream()
                .sorted(ALPHABETICAL_SORT)
                .toList();
        int lines = 0;
        for (Map.Entry<ItemStack, Integer> entry : storages) {
            if (lines >= 8) {
                lores.add(CMIChatColor.translate("&e------还有" + (storages.size() - lines) + "项------"));
                break;
            }
            lines++;
            lores.add(CMIChatColor.translate("&e" + ItemUtils.getItemName(entry.getKey()) + " - " + entry.getValue()));
        }
        toReturn.setLore(lores);
        return toReturn;
    }

    @Nonnull
    public static ResultWithItem<UUID> getServerUUID(@Nonnull ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        UUID uuid = nbtItem.getUUID(SERVER_UUID_KEY);
        if (uuid == null) {
            nbtItem.setUUID(SERVER_UUID_KEY, GuguSlimefunLib.getServerUUID());
            return new ResultWithItem<>(GuguSlimefunLib.getServerUUID(), nbtItem.getItem());
        }

        return new ResultWithItem<>(uuid, itemStack);
    }

    public static ResultWithItem<Boolean> isCurrentServer(@Nonnull ItemStack itemStack) {
        ResultWithItem<UUID> result = getServerUUID(itemStack);
        return new ResultWithItem<>(result.getResult().equals(GuguSlimefunLib.getServerUUID()), result.getItemStack());
    }
}
