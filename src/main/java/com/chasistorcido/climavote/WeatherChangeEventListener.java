package com.chasistorcido.climavote;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeEventListener implements Listener {
    private ClimaVote plugin;

    public WeatherChangeEventListener(ClimaVote plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) { // Si se está cambiando a lluvia
            plugin.getServer().broadcastMessage(ChatColor.GRAY + "¡Comenzó a llover! ¡Vota por el clima con /votar lluvia o /votar sol!");
            plugin.votes.clear();
            plugin.votedPlayers.clear();
        }
    }
}
