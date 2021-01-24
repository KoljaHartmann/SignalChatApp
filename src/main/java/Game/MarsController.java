package Game;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MarsController {

    public static ArrayList<String> getActivePlayers(JSONObject jsonObject) {
        ArrayList<String> activePlayers = new ArrayList<>();
        ArrayList<String> draftPlayers = new ArrayList<>();
        final JSONArray players = (JSONArray) jsonObject.get("players");

        for (Object playerObject : players) {
            JSONObject player = (JSONObject) playerObject;
            if (player.has("needsToDraft") && (boolean) player.get("needsToDraft")) {
                draftPlayers.add((String) (player.get("name")));
            }
            if (player.has("isActive") && (boolean) player.get("isActive")) {
                activePlayers.add((String) (player.get("name")));
            }
        }
        return draftPlayers.size() > 0 ? draftPlayers : activePlayers;
    }

    public static Phases getPhase(JSONObject jsonObject) {
        final String phase = (String) jsonObject.get("phase");
        return Phases.valueOf(phase.toUpperCase());
    }

    public static JSONObject readMarsJson(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

}
