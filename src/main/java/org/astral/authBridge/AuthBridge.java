package org.astral.authBridge;

import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AuthBridge extends JavaPlugin implements Listener {

    private static final String CHANNEL = "authmevelocity:main";

    @Override
    public void onEnable() {
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
        player.sendPluginMessage(this, CHANNEL, out.toByteArray());
        getLogger().info("Acción " + action + " enviada a Velocity para " + player.getName());
    }

    @EventHandler
    public void onLogin(@NotNull LoginEvent event) {
        getServer().getScheduler().runTask(this, () ->
                sendVelocityAction(event.getPlayer(), "LOGIN")
        );
    }

    @EventHandler
    public void onLogout(@NotNull LogoutEvent event) {
        getServer().getScheduler().runTask(this, () ->
                sendVelocityAction(event.getPlayer(), "LOGOUT")
        );
    }
}