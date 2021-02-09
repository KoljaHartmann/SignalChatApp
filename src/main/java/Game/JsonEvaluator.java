package Game;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static Game.Phases.*;
import static Game.Phases.RESEARCH;

public class JsonEvaluator {

    public static void evaluateSendingMessage(JSONObject lastJson, JSONObject currentJson) {
        String message = "";
        Phases currentPhase = getPhase(currentJson);
        Phases lastPhase = getPhase(lastJson);
        final ArrayList<String> currentPlayers = getActivePlayers(currentJson);
        final ArrayList<String> lastActivePlayers = getActivePlayers(lastJson);

        if (currentPhase.equals(DRAFTING)) {
            if (lastActivePlayers.size() > 1 && currentPlayers.size() == 1) {
                message = ChatMacros.getSimplePing(currentPlayers.get(0));
            } else if (lastActivePlayers.size() == 1 && currentPlayers.size() > 1) {
                message = ChatMacros.getDraftMessage();
            }
        } else if (currentPhase.equals(RESEARCH)) {
            if (lastPhase != null && !lastPhase.equals(RESEARCH)) {
                message = ChatMacros.getResearchMessage();
            } else if (lastActivePlayers.size() > 1 && currentPlayers.size() == 1) {
                message = ChatMacros.getSimplePing(currentPlayers.get(0));
            }
        } else if (currentPhase.equals(ACTION)){
            if (lastPhase.equals(RESEARCH)) {
                message = ChatMacros.getSimplePing(currentPlayers.get(0));
            } else if (currentPlayers.size() == 1 && lastActivePlayers.size() > 0) {
                if (!currentPlayers.get(0).equals(lastActivePlayers.get(0))) {
                    message = ChatMacros.getSimplePing(currentPlayers.get(0));
                }
            }
        } else if (currentPhase.equals(PRODUCTION)) {
            if (lastPhase != null && !lastPhase.equals(RESEARCH)) {
                message = ChatMacros.finalGreeneryPing(currentPlayers.get(0));
            } else if (currentPlayers.size() == 1 && lastActivePlayers.size() > 0) {
                if (!currentPlayers.get(0).equals(lastActivePlayers.get(0))) {
                    message = ChatMacros.finalGreeneryPing(currentPlayers.get(0));
                }
            }
        }

        if (!message.isEmpty()) {
            WebController.sendWhatsAppMessage(message);
        }
    }

    public static ArrayList<String> getActivePlayers(JSONObject jsonObject) {
        final ArrayList<String> activePlayers = new ArrayList<>();
        final JSONArray players = (JSONArray) jsonObject.get("players");

        for (Object playerObject : players) {
            final JSONObject player = (JSONObject) playerObject;
            final JSONObject timer = (JSONObject) player.get("timer");
            if ((boolean) timer.get("running")) {
                activePlayers.add((String) player.get("name"));
            }

        }
        return activePlayers;
    }

    public static Phases getPhase(JSONObject jsonObject) {
        final String phase = (String) jsonObject.get("phase");
        return Phases.valueOf(phase.toUpperCase());
    }

}
