package Game;

import org.json.JSONArray;
import org.json.JSONObject;

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


}
