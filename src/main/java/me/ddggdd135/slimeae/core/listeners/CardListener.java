package me.ddggdd135.slimeae.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.ddggdd135.slimeae.api.interfaces.ICardHolder;

public class CardListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ICardHolder.cache.remove(e.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ICardHolder.cache.remove(e.getBlock().getLocation());
    }
}
