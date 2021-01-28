package Game;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static Game.Phases.*;

public class ChatMakros {

    private static final Map<String, String> playerNames = Map.of(
            "k4rlchen","Stefan",
            "K4rlchen","Stefan",
            "apehead","Damian",
            "BOBftl.","Lukas",
            "BOB ftl.","Lukas",
            "GomJabbar","Daniel",
            "Gom Jabbar","Daniel",
            "DartVader","Kolja",
            "Dart Vader","Kolja"
    );

    public static String getPlayerName(String player) {
        final String name = playerNames.get(player);
        return name != null ? name : player;
    }

    //TODO logik konsolidieren mit activePlayers im MarsControler
    public static void evaluateSendingMessage(JSONObject lastJson, JSONObject currentJson) {
        String message = "";
        Phases currentPhase = MarsController.getPhase(currentJson);
        Phases lastPhase = MarsController.getPhase(lastJson);
        final ArrayList<String> currentPlayers = MarsController.getActivePlayers(currentJson);
        final ArrayList<String> lastActivePlayers = MarsController.getActivePlayers(lastJson);
        //final boolean philaresInGame = MarsController.isCorporationInGame("Philares", lastJson);

        if(currentPhase.equals(DRAFTING)) {
            if (lastActivePlayers.size() > 1 && currentPlayers.size() == 1) {
                message = ChatMakros.getMessage(currentPlayers.get(0));
            } else if (lastActivePlayers.size() == 1 && currentPlayers.size() > 1) {
                message = ChatMakros.getDraftMessage();
            }
        // start: testen über players ->  cardsInHandNbr > 0
            // gameAge: 1,
            // generation: 1,
            // Nicht start: initialDraft = false
        } else if (currentPhase.equals(RESEARCH)) {
            if (lastPhase != null && !lastPhase.equals(RESEARCH))
                message = ChatMakros.getResearchMessage();
        } else if (currentPhase.equals(ACTION)){
            if (lastPhase.equals(RESEARCH)) {
                message = ChatMakros.getMessage(currentPlayers.get(0));
            } else if (currentPlayers.size() == 1 && lastActivePlayers.size() > 0) {
                if (!currentPlayers.get(0).equals(lastActivePlayers.get(0))) {
                    message = ChatMakros.getMessage(currentPlayers.get(0));
                }
            }
            // Falls Philares im Spiel ist und der aktuelle Spieler zwei Aktionen gemacht hat und immer noch am Zug ist: Ping!
            /*
            if (philaresInGame) {
                if (MarsController.getAmountOfActionsForPlayer(currentPlayers.get(0), lastJson) == 2) {
                    message = ChatMakros.getMessage(MarsController.getPlayerToCorporation("Philares", lastJson));
                }
            }
             */
        }

        if (!message.isEmpty()) {
            WhatsAppController.sendWhatsAppMessage(message);
        }
    }

    public static String getMessage(String player) {
        return "Ping @" + getPlayerName(player);
    }

    public static String getDraftMessage() {
        return "Ping @all. It's drafting time!";
    }

    public static String getResearchMessage() { return "Ping @all. Please buy your cards!"; }
}
