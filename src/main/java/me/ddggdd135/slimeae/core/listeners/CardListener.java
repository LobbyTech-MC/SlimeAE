package me.ddggdd135.slimeae.core.listeners;

import me.ddggdd135.slimeae.api.interfaces.ICardHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
public class CardListener implements Listener {
    @EventHandler
    @Async
    public void onBlockBreak(BlockBreakEvent e) {
        ICardHolder.cache.remove(e.getBlock().getLocation());
    }

    @EventHandler
    @Async
    public void onBlockPlace(BlockPlaceEvent e) {
        ICardHolder.cache.remove(e.getBlock().getLocation());
    }
}
