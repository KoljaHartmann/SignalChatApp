package Game;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Game.Phases.DRAFTING;
import static Game.Phases.RESEARCH;

public class Main {
    //TODO failsaves
    //TODO keepalive whatsapp
    //TODO MARS_URL als property
    //TODO npe bei path var catchen
    //TODO logging verbessern

    private static final String MARS_URL = "http://168.119.225.172:8080/api/player?id=92f963bfe615";

    private static JSONObject lastJson;

    public static void main(String [ ] args) {
        System.out.println("Starting Chatbot");
        Controller.connectToWhatsapp();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            () -> {
                try {
                    final JSONObject currentJson = Controller.readMarsJson(MARS_URL);
                    final ArrayList<String> currentPlayers = MarsController.getActivePlayers(currentJson);
                    final ArrayList<String> lastActivePlayers = MarsController.getActivePlayers(lastJson);
                    System.out.println("Phase: " + MarsController.getPhase(currentJson) + ",  lastActivePlayers: " + lastActivePlayers + " currentPlayers: " + currentPlayers);
                    ChatMakros.evaluateSendingMessage(lastJson, currentJson);
                    lastJson = currentJson;
                } catch (Exception e) {
                    e.printStackTrace();
                    Controller.close();
                }
            },
            10,
            1500,
            TimeUnit.MILLISECONDS
        );
    }
}
