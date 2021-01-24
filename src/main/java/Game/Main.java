package Game;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    //TODO MARS_URL als property
    //TODO npe bei path var catchen
    //TODO nur jar übertragen
    //TODO headless als property

    //TODO echter ping
    //TODO final greenery
    //TODO initial buy

    private static String MARS_URL = "";

    private static JSONObject lastJson;

    public static void main(String [ ] args) {
        System.out.println("Starting Chatbot");
        WhatsAppController.connectToWhatsapp();

        //check config in for new Mars Url
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    try {
                        MARS_URL = WhatsAppController.getMarsUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                        WhatsAppController.close();
                    }
                },
                0,
                15,
                TimeUnit.SECONDS
        );

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            () -> {
                System.out.println(MARS_URL);
                JSONObject currentJson = null;
                try {
                    currentJson = MarsController.readMarsJson(MARS_URL);
                    if (lastJson == null || lastJson.isEmpty()) {
                        System.out.println("First call to Mars");
                        lastJson = currentJson;
                    }
                    final ArrayList<String> currentPlayers = MarsController.getActivePlayers(currentJson);
                    final ArrayList<String> lastActivePlayers = MarsController.getActivePlayers(lastJson);
                    System.out.println("Phase: " + MarsController.getPhase(currentJson) + ",  lastActivePlayers: " + lastActivePlayers + " currentPlayers: " + currentPlayers);
                    ChatMakros.evaluateSendingMessage(lastJson, currentJson);
                    lastJson = currentJson;
                } catch (Exception e) {
                    System.out.println(lastJson);
                    System.out.println(currentJson);
                    System.out.println(e.getMessage());
                }
            },
            50,
            1500,
            TimeUnit.MILLISECONDS
        );
    }
}
