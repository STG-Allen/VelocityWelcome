import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.text.Component;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Config {
    public static ConfigurationNode playersNode;
    public static ConfigurationNode configNode;
    public static GsonConfigurationLoader configLoader;
    public static GsonConfigurationLoader playerLoader;
    public static Path playersPath;
    public static Path configPath;

    public static SimpleDateFormat formatter = new SimpleDateFormat("mm-dd-yyyy HH:mm:ss");
    public static Date date = new Date(System.currentTimeMillis());

    private static WatchService _watchService;
    private static WatchKey _key;

    private static boolean isJson;

    private static ScheduledTask task;

    public VelocityWelcome velocityWelcome;

    public Config(boolean isJson, VelocityWelcome vw){
        this.isJson = isJson;
        velocityWelcome = vw;
    }

    public static void setupConfig(){
        try {
                playersPath = Paths.get(VelocityWelcome.configpath + "/knownplayers.json");
                configPath = Paths.get(VelocityWelcome.configpath + "/config.json");

                if (!Files.exists(playersPath)) {
                    Files.createDirectories(VelocityWelcome.configpath);
                    Files.createFile(playersPath);
                }

                if (!Files.exists(configPath)) {
                    Files.createFile(configPath);
                }

                configLoader = GsonConfigurationLoader.builder().setPath(configPath).build();
                configNode = configLoader.load();

                playerLoader = GsonConfigurationLoader.builder().setPath(playersPath).build();
                playersNode = playerLoader.load();

                String message = configNode.getNode("Velocity Welcome", "Message").getString();
                if(message == null){
                    VelocityWelcome.logger.info("Setting default welcome message");
                    setDefaultMessage();
                }

                //VelocityWelcome.server.getConsoleCommandSource().sendMessage(getMessage());

                _watchService = VelocityWelcome.configpath.getFileSystem().newWatchService();
                _key = VelocityWelcome.configpath.register(_watchService, StandardWatchEventKinds.ENTRY_MODIFY);



        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //get uuid's from knownplayers.json
    public static List<String> getUUID(){
        Set<Object> objectList = playersNode.getNode("Known players").getChildrenMap().keySet();
        List<String> uuids = new ArrayList<>();

        for(Object object : objectList){
            String uu = (String) object;

            uuids.add(uu);
        }
        return uuids;
    }
    //Sets the default config
    public static void setDefaultMessage()throws IOException{
        configNode.getNode("Velocity Welcome", "Message").setValue("Welcome to the server, ");
        configNode.getNode("Velocity Welcome", "Name Color ('mc colorcodes)'").setValue("&r");
        configLoader.save(configNode);
    }

    //Sets the message, will be used later on in a command
    public static void setMessage(String text) throws IOException {
        if (text == null) {
            configNode.getNode("Velocity Welcome", "Message").setValue("Welcome to Milspec, ");
            configLoader.save(configNode);
        }
        else{
            configNode.getNode("Velocity Welcome", "Message").setValue(text);
            configLoader.save(configNode);
        }
    }

    //Get the message

    public static String getMessage(){
        String text = configNode.getNode("Velocity Welcome", "Message").getString();
        return text;
    }
    public static String getPlayerColor(String name){
        String color = configNode.getNode("Velocity Welcome", "Name Color ('mc colorcodes)'").getString();
        name = color + name;
        return name;
    }

    public static void setLastSeen(String uuid)throws IOException{
        playersNode.getNode("Known players", uuid, "last seen").setValue(formatter.format(date));
        playerLoader.save(playersNode);
    }

    //used to set color codes *not supported by velocity*
    public static Component color(String text){
        return isJson ? GsonComponentSerializer.INSTANCE.deserialize(text) : LegacyComponentSerializer.INSTANCE.deserialize(text, '&');
    }

    public static void getPlayerFromFile(UUID uuid, String name){

       if(getUUID().contains(uuid.toString())){
           return;
       }else{
           VelocityWelcome.server.broadcast(color(getMessage()).append(color(getPlayerColor(name))));
           String playerUUID = uuid.toString();
           addPlayer(playerUUID, name);
       }
    }

    public static boolean addPlayer(String playerid, String name){
       try {
           playersNode.getNode("Known players", playerid, "name").setValue(name);
           playersNode.getNode("Known players", playerid, "join date").setValue(formatter.format(date));
           playerLoader.save(playersNode);
           return true;
       }catch(IOException e){
           e.printStackTrace();
       }
       return false;
    }
    public static Runnable checkFileUpdate(){
        return () -> {
            try{
                for(WatchEvent<?> event : _key.pollEvents()){
                    final Path changedFilePath = (Path) event.context();

                    if(changedFilePath.toString().contains("knownplayers.json")){
                        playersNode = playerLoader.load();
                        VelocityWelcome.logger.info("Detected changes in the filesystem!, reloading");
                    }
                    if(changedFilePath.toString().contains("config.json")){
                        configNode = configLoader.load();
                        VelocityWelcome.logger.info("Detected changes in the config! reloading");
                    }
                    _key.reset();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        };
    }
}
