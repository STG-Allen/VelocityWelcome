import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Plugin(description = "Simple welcome plugin for velocity", authors = "STG_Allen", id = "velocitywelcome", version = "1.0")
public class VelocityWelcome {

    public static ProxyServer server;
    public static Path configpath;
    public static Logger logger;
    //  public TestConfig config;


    @Inject
    public VelocityWelcome(ProxyServer proxyServer, @DataDirectory Path path, Logger log) {
        logger = log;
        configpath = path;
        this.server = proxyServer;
        logger.info("Loading config...");
        TestConfig.setupConfig();
        logger.info("Config loaded!");
    }

    @Subscribe
    public void onPlayerLogin(ServerConnectedEvent e) {
        UUID playerUUID = e.getPlayer().getUniqueId();
        server.getConsoleCommandSource().sendMessage(TextComponent.of(playerUUID.toString()));
        String name = e.getPlayer().getGameProfile().getName();

        TestConfig.getPlayerFromFile(e.getPlayer().getUniqueId(), e.getPlayer().getUsername());


        }
    @Subscribe
    public void onPlayerQuit(DisconnectEvent e) throws IOException {
        TestConfig.playerLoader.load();
    }
}
