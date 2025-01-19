package me.ddggdd135.slimeae.core.slimefun;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import me.ddggdd135.guguslimefunlib.api.abstracts.TickingBlock;
import me.ddggdd135.guguslimefunlib.api.interfaces.InventoryBlock;
import me.ddggdd135.guguslimefunlib.libraries.colors.CMIChatColor;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.ItemRequest;
import me.ddggdd135.slimeae.api.interfaces.IMEObject;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.core.NetworkInfo;
import me.ddggdd135.slimeae.core.items.MenuItems;
import me.ddggdd135.slimeae.utils.ItemUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class METerminal extends TickingBlock implements IMEObject, InventoryBlock {
    public static final Comparator<Map.Entry<ItemStack, Integer>> ALPHABETICAL_SORT = Comparator.comparing(
            itemStackIntegerEntry -> CMIChatColor.stripColor(ItemUtils.getItemName(itemStackIntegerEntry.getKey())),
            Collator.getInstance(Locale.CHINA)::compare);

    public static final Comparator<Map.Entry<ItemStack, Integer>> NUMERICAL_SORT = Map.Entry.comparingByValue();
    public static final String PAGE_KEY = "page";
    public static final String SORT_KEY = "sort";
    public static final String FILTER_KEY = "filter";

    public int[] getBackgroundSlots() {
        return new int[] {17, 26};
    }

    public int[] getDisplaySlots() {
        return new int[] {
            0, 1, 2, 3, 4, 5, 6, 7,
            9, 10, 11, 12, 13, 14, 15, 16,
            18, 19, 20, 21, 22, 23, 24, 25,
            27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43,
            45, 46, 47, 48, 49, 50, 51, 52
        };
    }

    public int getInputSlot() {
        return 8;
    }

    public int getChangeSort() {
        return 26;
    }

    public int getFilter() {
        return 35;
    }

    public int getPagePrevious() {
        return 44;
    }

    public int getPageNext() {
        return 53;
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem item, @Nonnull SlimefunBlockData data) {
        BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
        if (blockMenu == null) return;
        if (blockMenu.hasViewer()) updateGui(block);
        NetworkInfo info = SlimeAEPlugin.getNetworkData().getNetworkInfo(block.getLocation());
        if (info == null) return;
        ItemStack itemStack = blockMenu.getItemInSlot(getInputSlot());
        if (itemStack != null && !itemStack.getType().isAir()) info.getStorage().pushItem(itemStack);
    }

    public METerminal(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        createPreset(this);
    }

    public int getPage(Block block) {
        String value = StorageCacheUtils.getData(block.getLocation(), PAGE_KEY);
        if (value == null || Integer.parseInt(value) < 0) return 0;
        return Integer.parseInt(value);
    }

    public void setPage(Block block, int value) {
        if (value < 0) {
            StorageCacheUtils.setData(block.getLocation(), PAGE_KEY, "0");
            return;
        }
        StorageCacheUtils.setData(block.getLocation(), PAGE_KEY, String.valueOf(value));
    }

    public Comparator<Map.Entry<ItemStack, Integer>> getSort(Block block) {
        String value = StorageCacheUtils.getData(block.getLocation(), SORT_KEY);
        if (value == null) return ALPHABETICAL_SORT;
        int id = Integer.parseInt(value);
        if (id == 0) return ALPHABETICAL_SORT;
        if (id == 1) return NUMERICAL_SORT;
        return ALPHABETICAL_SORT;
    }

    public void setSort(Block block, int value) {
        if (value < 0 || value > 1) {
            StorageCacheUtils.setData(block.getLocation(), SORT_KEY, "0");
            return;
        }
        StorageCacheUtils.setData(block.getLocation(), SORT_KEY, String.valueOf(value));
    }

    @Nonnull
    public String getFilter(@Nonnull Block block) {
        String filter = StorageCacheUtils.getData(block.getLocation(), FILTER_KEY);
        if (filter == null) {
            setFilter(block, "");
            return "";
        }

        return filter;
    }

    public void setFilter(@Nonnull Block block, @Nonnull String filter) {
        StorageCacheUtils.setData(block.getLocation(), FILTER_KEY, filter);
    }

    public void updateGui(@Nonnull Block block) {
        BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
        if (blockMenu == null) return;

        // 清空显示槽
        for (int slot : getDisplaySlots()) {
            blockMenu.replaceExistingItem(slot, MenuItems.Empty);
        }

        NetworkInfo info = SlimeAEPlugin.getNetworkData().getNetworkInfo(block.getLocation());
        if (info == null) return;

        IStorage networkStorage = info.getStorage();
        Map<ItemStack, Integer> storage = networkStorage.getStorage();

        // 获取过滤器
        String filter = getFilter(block).toLowerCase(Locale.ROOT);

        // 过滤和排序逻辑
        List<Map.Entry<ItemStack, Integer>> items = new ArrayList<>(storage.entrySet());
        if (!filter.isEmpty()) {
            items.removeIf(x -> {
                String itemType = x.getKey().getType().toString().toLowerCase(Locale.ROOT);
                if (itemType.startsWith(filter)) {
                    return false;
                }
                String name =
                        ChatColor.stripColor(ItemUtils.getItemName(x.getKey()).toLowerCase(Locale.ROOT));

                if (name == null) return true;
                return !name.contains(filter);
            });
        }

        // 对整个列表进行排序
        items.sort(getSort(block));

        // 计算分页
        int page = getPage(block);
        int maxPage = (int) Math.max(0, Math.ceil(items.size() / (double) getDisplaySlots().length) - 1);
        if (page > maxPage) {
            page = maxPage;
            setPage(block, page);
        }

        // 显示当前页的物品
        int startIndex = page * getDisplaySlots().length;
        int endIndex = Math.min(startIndex + getDisplaySlots().length, items.size());

        for (int i = 0; i < getDisplaySlots().length && (i + startIndex) < endIndex; i++) {
            Map.Entry<ItemStack, Integer> entry = items.get(i + startIndex);
            ItemStack itemStack = entry.getKey();
            if (itemStack == null || itemStack.getType().isAir()) continue;

            int slot = getDisplaySlots()[i];
            blockMenu.replaceExistingItem(slot, ItemUtils.createDisplayItem(itemStack, entry.getValue()));
        }
    }

    @Override
    public void onNetworkUpdate(Block block, NetworkInfo networkInfo) {}

    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void init(@Nonnull BlockMenuPreset preset) {
        for (int slot : getBackgroundSlots()) {
            preset.addItem(slot, ChestMenuUtils.getBackground());
            preset.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }
        preset.setSize(54);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block block) {
        menu.replaceExistingItem(getPageNext(), MenuItems.PAGE_NEXT_STACK);
        menu.addMenuClickHandler(getPageNext(), (player, i, cursor, clickAction) -> {
            setPage(block, getPage(block) + 1);
            updateGui(block);
            return false;
        });

        menu.replaceExistingItem(getPagePrevious(), MenuItems.PAGE_PREVIOUS_STACK);
        menu.addMenuClickHandler(getPagePrevious(), (player, i, cursor, clickAction) -> {
            setPage(block, getPage(block) - 1);
            updateGui(block);
            return false;
        });

        menu.replaceExistingItem(getChangeSort(), MenuItems.CHANGE_SORT_STACK);
        menu.addMenuClickHandler(getChangeSort(), (player, i, cursor, clickAction) -> {
            Comparator<Map.Entry<ItemStack, Integer>> sort = getSort(block);
            if (sort == ALPHABETICAL_SORT) setSort(block, 1);
            if (sort == NUMERICAL_SORT) setSort(block, 0);
            return false;
        });

        menu.replaceExistingItem(getFilter(), MenuItems.FILTER_STACK);
        menu.addMenuClickHandler(getFilter(), (player, i, cursor, clickAction) -> {
            if (clickAction.isRightClicked()) {
                setFilter(block, "");
            } else {
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "请输入你想要过滤的物品名称(显示名)或类型");
                ChatUtils.awaitInput(player, filter -> {
                    if (filter.isBlank()) {
                        return;
                    }
                    setFilter(block, filter.toLowerCase(Locale.ROOT));
                    player.sendMessage(ChatColor.GREEN + "已启用过滤器");
                });
            }
            return false;
        });

        for (int slot : getDisplaySlots()) {
            menu.replaceExistingItem(slot, MenuItems.Empty);
            menu.addMenuClickHandler(slot, new ChestMenu.AdvancedMenuClickHandler() {
                @Override
                public boolean onClick(
                        InventoryClickEvent inventoryClickEvent,
                        Player player,
                        int i,
                        ItemStack cursor,
                        ClickAction clickAction) {
                    NetworkInfo info = SlimeAEPlugin.getNetworkData().getNetworkInfo(block.getLocation());
                    if (info == null) return false;
                    IStorage networkStorage = info.getStorage();
                    Inventory playerInventory = player.getInventory();
                    ItemStack itemStack = menu.getItemInSlot(i);
                    if (itemStack != null
                            && !itemStack.getType().isAir()
                            && !SlimefunUtils.isItemSimilar(itemStack, MenuItems.Empty, true, false)) {
                        ItemStack template = ItemUtils.getDisplayItem(itemStack).clone();
                        template.setAmount(template.getMaxStackSize());
                        if (clickAction.isShiftClicked()
                                && InvUtils.fits(
                                        playerInventory,
                                        template,
                                        IntStream.range(0, 36).toArray())) {
                            playerInventory.addItem(
                                    networkStorage.tryTakeItem(new ItemRequest(template, template.getMaxStackSize())));
                        } else if (!clickAction.isShiftClicked()
                                        && cursor.getType().isAir()
                                || (SlimefunUtils.isItemSimilar(template, cursor, true, false)
                                        && cursor.getAmount() + 1 <= cursor.getMaxStackSize())) {
                            ItemStack[] gotten = networkStorage.tryTakeItem(new ItemRequest(template, 1));
                            if (gotten.length != 0) {
                                ItemStack newCursor = gotten[0];
                                newCursor.add(cursor.getAmount());
                                player.setItemOnCursor(newCursor);
                            }
                        }
                    }
                    updateGui(block);
                    return false;
                }

                @Override
                public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                    return false;
                }
            });
        }
    }
}
