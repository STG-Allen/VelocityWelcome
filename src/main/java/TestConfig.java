import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestConfig {
    public static ConfigurationNode players;
    public static Path playersPath;
    public static Path configDir = VelocityWelcome.configpath;
    public static GsonConfigurationLoader playerLoader;


    private static WatchService _watchService;
    private static WatchKey _key;

    public VelocityWelcome velocityWelcome;


    public TestConfig(VelocityWelcome vw){
        velocityWelcome = vw;
    }



    public static void setupConfig(){
        try {
            playersPath = Paths.get(VelocityWelcome.configpath + "/knownplayers.json");

            if (!Files.exists(playersPath)) {
                Files.createDirectories(configDir);
                Files.createFile(playersPath);
            }

            playerLoader = GsonConfigurationLoader.builder().setPath(playersPath).build();
            players = playerLoader.load();

            _watchService = configDir.getFileSystem().newWatchService();
            _key = configDir.register(_watchService, StandardWatchEventKinds.ENTRY_MODIFY);


           // return true;

        }catch (IOException e){
            e.printStackTrace();
        }
     //   return false;
    }

    public static List<String> getUUID(){
        Set<Object> objectList = players.getNode("Known players").getChildrenMap().keySet();
        List<String> uuids = new ArrayList<>();

        for(Object object : objectList){
            String uu = (String) object;

            uuids.add(uu);
        }
        return uuids;
    }


    public static void getPlayerFromFile(UUID uuid, String name){

       if(getUUID().contains(uuid.toString())){
        //   VelocityWelcome.server.getConsoleCommandSource().sendMessage(TextComponent.of("Found an existing player!"));
           return;
       }else{
           VelocityWelcome.server.getConsoleCommandSource().sendMessage(TextComponent.of("Welcome to the server, " + name));
           VelocityWelcome.server.broadcast(TextComponent.of("Welcome to the server, ").color(TextColor.GREEN).append(TextComponent.of(name).color(TextColor.GOLD)));
          // VelocityWelcome.server.getConsoleCommandSource().sendMessage(TextComponent.of(getUUID().toString()));
           String playerUUID = uuid.toString();
           addPlayer(playerUUID, name);
       }
    }

    public static boolean addPlayer(String playerid, String name){
       try {
           players.getNode("Known players", playerid, "name").setValue(name);
           playerLoader.save(players);
           return true;
       }catch(IOException e){
           e.printStackTrace();
       }
       return false;
    }
    private Runnable checkFileUpdate(){
        return new Runnable() {
            @Override
            public void run() {
                try{
                    for(WatchEvent<?> event : _key.pollEvents()){
                        final Path changedFilePath = (Path) event.context();

                        if(changedFilePath.toString().contains("knownplayers.json")){
                            players = playerLoader.load();
                        }
                        _key.reset();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
    }
}
