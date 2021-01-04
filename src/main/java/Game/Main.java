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
    //TODO repo
    //TODO MARS_URL als property
    //TODO npe bei path var catchen
    //TODO logging verbessern

    private static final String MARS_URL = "http://168.119.225.172:8080/api/player?id=92f963bfe615";

    private static List<String> lastActivePlayers = new ArrayList<>();
    private static Phases lastPhase;


    public static void main(String [ ] args) {
        System.out.println("Starting Chatbot");
        Controller.connectToWhatsapp();

        /*
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    try {
                        Controller.sendTestMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Controller.close();
                    }
                },
                10,
                15,
                TimeUnit.SECONDS
        );
         */
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            () -> {
                try {
                    final JSONObject jsonObject = Controller.readMarsJson(MARS_URL);
                    final ArrayList<String> currentPlayers = MarsController.getActivePlayers(jsonObject);
                    System.out.println("Phase: " + MarsController.getPhase(jsonObject) + ",  lastActivePlayers: " + lastActivePlayers + " currentPlayers: " + currentPlayers);
                    evaluateSendingMessage(currentPlayers, jsonObject);
                    lastActivePlayers = currentPlayers;
                    lastPhase = MarsController.getPhase(jsonObject);
                } catch (Exception e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                    System.out.println(e.getClass());
                    Controller.close();
                }
            },
            10,
            1500,
            TimeUnit.MILLISECONDS
        );
    }

    private static void evaluateSendingMessage(ArrayList<String> currentPlayers, JSONObject jsonObject) {
        String message = "";
        if(MarsController.getPhase(jsonObject).equals(DRAFTING)) {
            if (lastActivePlayers.size() > 1 && currentPlayers.size() == 1) {
                message = ChatMakros.getMessage(currentPlayers.get(0));
            } else if (lastActivePlayers.size() == 1 && currentPlayers.size() > 1) {
                message = ChatMakros.getDraftMessage();
            }
        } else if (MarsController.getPhase(jsonObject).equals(RESEARCH)) {
            if (lastPhase != null && !lastPhase.equals(RESEARCH))
            message = ChatMakros.getResearchMessage();
        } else if (currentPlayers.size() == 1 && lastActivePlayers.size() > 0) {
            if (!currentPlayers.get(0).equals(lastActivePlayers.get(0))) {
                message = ChatMakros.getMessage(currentPlayers.get(0));
            }
        }

        if (!message.isEmpty()) {
            Controller.sendWhatsAppMessage(message);

        }
    }
}
