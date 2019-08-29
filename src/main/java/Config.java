/*

import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Config {

    public static JSONObject config = null;

    private static List<UUID> knownPlayers = new ArrayList<>();
    private static String uu = "91ea08e8-a436-495f-99b6-91fd1aed87f5";
    private static UUID stg = UUID.fromString(uu);


    public VelocityWelcome velocityWelcome;

    public Config(VelocityWelcome plugin) {velocityWelcome = plugin;}

    public static void setup(){
        if(!VelocityWelcome.configpath.toFile().exists()){
            VelocityWelcome.configpath.toFile().mkdirs();
        }
        if(!new File(VelocityWelcome.configpath.toString() + "/knownplayers.json").exists()) {
            try{
                writeInitialConfig();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    private static void writeInitialConfig() throws FileNotFoundException {
        JSONObject configFile = new JSONObject();

        configFile.put("Known players", knownPlayers);
        configFile.put("Server Name(for broadcast message)", "A Proxy Network!");

        knownPlayers.add(stg);
        PrintWriter printWriter = new PrintWriter(VelocityWelcome.configpath.toString() + "/knownplayers.json");
        printWriter.write(JsonWriter.formatJson(configFile.toJSONString()));

        printWriter.flush();
        printWriter.close();
    }

    public boolean addPlayerByUUID(UUID uuid){
        if(uuid.toString() != knownPlayers.toString()){
            knownPlayers.add(uuid);
            try{
                FileWriter file = new FileWriter(
            }

            return true;
        }
       return false;
    }
}
*/