package org.astral.authBridge;

import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public final class AuthBridge extends JavaPlugin implements Listener {

    private static final String CHANNEL = "authmevelocity:main";

    @Override
    public void onEnable() {
        getServer().getMessenger()
                .registerOutgoingPluginChannel(this, CHANNEL);

        getServer().getPluginManager()
                .registerEvents(this, this);

        getLogger().info("AuthBridge habilitado.");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger()
                .unregisterOutgoingPluginChannel(this, CHANNEL);
    }

    private void sendLogin(@NotNull Player player) {
        if (!player.isOnline()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("LOGIN");
        out.writeUTF(player.getName());

        getLogger().info(
                "Enviando LOGIN para "
                        + player.getName()
        );

        player.sendPluginMessage(
                this,
                CHANNEL,
                out.toByteArray()
        );
    }

    @EventHandler
    public void onLogin(@NotNull LoginEvent event) {
        Player player = event.getPlayer();

        getServer().getScheduler().runTaskLater(
                this,
                () -> sendLogin(player),
                20L
        );
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        getServer().getScheduler().runTaskLater(
                this,
                () -> {
                    if (!player.isOnline()) {
                        return;
                    }

                    if (AuthMeApi.getInstance()
                            .isAuthenticated(player)) {
                        sendLogin(player);
                    }
                },
                20L
        );
    }

}