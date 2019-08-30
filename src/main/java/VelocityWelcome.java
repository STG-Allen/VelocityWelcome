import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.slf4j.Logger;
/*
import rocks.milspecsg.msrepository.APIConfigurationModule;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;
*/

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(description = "Simple welcome plugin for velocity", authors = "STG_Allen", id = "velocitywelcome", version = "1.0")
public class VelocityWelcome {

    public static ProxyServer server;
    public static Path configpath;
    public static Logger logger;

    public ScheduledTask task;


    @Inject
    public Injector velocityRootInjector;

    private Injector injector = null;


    @Inject
    public VelocityWelcome(ProxyServer proxyServer, @DataDirectory Path path, Logger log) {
        logger = log;
        configpath = path;
        this.server = proxyServer;
        logger.info("Loading config...");
        Config.setupConfig();
        logger.info("Config loaded!");
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent e){
       //schedule();
    }

    @Subscribe
    public void onPlayerLogin(ServerConnectedEvent e) {
        UUID playerUUID = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getGameProfile().getName();
        Config.getPlayerFromFile(playerUUID, name);
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent e) throws IOException {
        String playerid = e.getPlayer().getUniqueId().toString();
        Config.setLastSeen(playerid);
    }

    public void schedule(){
        logger.info("Starting file system watchService");
        this.task = server.getScheduler().buildTask(this, () -> Config.checkFileUpdate()).repeat(2, TimeUnit.SECONDS).schedule();
    }
}