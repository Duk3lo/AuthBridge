package org.astral.authBridge;

import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import fr.xephi.authme.events.RestoreSessionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public final class AuthBridge extends JavaPlugin implements Listener {

    private static final String CHANNEL = "authmevelocity:main";
    private AuthMeApi authMeApi;

    @Override
    public void onEnable() {
        this.authMeApi = AuthMeApi.getInstance();

        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("AuthBridge habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
    }

    private void sendVelocityAction(@NotNull Player player, String action) {
        if (!player.isOnline()) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(action);
        out.writeUTF(player.getName());

        player.sendPluginMessage(this, CHANNEL, out.toByteArray());
        getLogger().info("Estado [" + action + "] enviado al proxy para " + player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(@NotNull LoginEvent event) {
        getServer().getScheduler().runTask(this, () ->
                sendVelocityAction(event.getPlayer(), "LOGIN")
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSessionRestore(@NotNull RestoreSessionEvent event) {
        getServer().getScheduler().runTask(this, () ->
                sendVelocityAction(event.getPlayer(), "LOGIN")
        );
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogout(@NotNull LogoutEvent event) {
        getServer().getScheduler().runTask(this, () ->
                sendVelocityAction(event.getPlayer(), "LOGOUT")
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, () -> {
            if (player.isOnline() && authMeApi.isAuthenticated(player)) {
                sendVelocityAction(player, "LOGIN");
            }
        }, 3L);
    }
}