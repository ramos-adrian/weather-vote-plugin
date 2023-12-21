package com.chasistorcido.climavote;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClimaVote extends JavaPlugin {
    private final int VOTES_REQUIRED = 1;
    private String currentWeather = "soleado";
    HashMap<String, Integer> votes = new HashMap<>();
    ArrayList<Player> votedPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        // Registro de eventos
        getServer().getPluginManager().registerEvents(new WeatherChangeEventListener(this), this);

        // Programación de la lluvia aleatoria cada 5 minutos
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentWeather.equals("lluvia")) {
                    return;
                }

                Random rand = new Random();
                int chance = rand.nextInt(100);

                if (chance < 10) { // 10% de posibilidad de lluvia
                    getServer().dispatchCommand(getServer().getConsoleSender(), "weather world storm");
                    currentWeather = "lluvia";
                    votes.clear();
                    votedPlayers.clear();
                    getServer().broadcastMessage(ChatColor.GRAY + "¡Comenzó a llover! ¡Vota por el clima con /votar lluvia o /votar sol!");
                }
            }
        }.runTaskTimer(this, 0L, 6000L); // 5 minutos en ticks
    }

    @Override
    public void onDisable() {
        // Limpieza
        votes.clear();
        votedPlayers.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("clima")) {
            sender.sendMessage(ChatColor.GRAY + "El clima actual es " + currentWeather + ".");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (votedPlayers.contains(player)) {
            player.sendMessage(ChatColor.RED + "Ya has votado en esta ronda.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("votar")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Debes especificar el clima por el cual quieres votar: lluvia o sol.");
                return false;
            }

            String weather = args[0].toLowerCase();

            if (!weather.equals("lluvia") && !weather.equals("sol")) {
                player.sendMessage(ChatColor.RED + "Clima no válido. Debes votar por lluvia o sol.");
                return false;
            }

            if (!votes.containsKey(weather)) {
                votes.put(weather, 1);
            } else {
                votes.put(weather, votes.get(weather) + 1);
            }

            votedPlayers.add(player);

            player.sendMessage(ChatColor.GREEN + "Tu voto por " + weather + " ha sido registrado. ¡Faltan " + (VOTES_REQUIRED - votedPlayers.size()) + " votos más para cambiar el clima!");
            checkVotes();
            return true;
        }

        return false;
    }

    public void changeWeather(String weather) {
        getServer().dispatchCommand(getServer().getConsoleSender(), "weather world " + weather);
        currentWeather = weather;
        votes.clear();
        votedPlayers.clear();
        getServer().broadcastMessage(ChatColor.GRAY + "¡El clima ha sido cambiado a " + weather + "!");
    }

    public void checkVotes() {
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() >= VOTES_REQUIRED) {
                changeWeather(entry.getKey().equalsIgnoreCase("sol") ? "sun" : "storm");
                return;
            }
        }
    }

}