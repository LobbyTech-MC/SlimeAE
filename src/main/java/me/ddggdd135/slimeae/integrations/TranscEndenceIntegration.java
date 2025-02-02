package me.ddggdd135.slimeae.integrations;

import org.bukkit.Bukkit;

import me.ddggdd135.slimeae.api.interfaces.Integration;

public class TranscEndenceIntegration implements Integration {
    @Override
    public boolean isLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled("TranscEndence");
    }
}
