package Game;

import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    //TODO MARS_URL als property
    //TODO headless als property
    //TODO qr code in console

    private static String marsUrl = "";
    private static final boolean headless = true;

    private static JSONObject lastJson;

    public static void main(String [ ] args) {
        System.out.println("Starting Chatbot");
        WebController.connectToWhatsapp(headless);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            () -> {
                System.out.println(marsUrl);
                JSONObject currentJson = null;
                try {
                    currentJson = WebController.readMarsJson(marsUrl);
                    if (currentJson == null) {
                        System.out.println("No Json found");
                    } else if (lastJson == null || lastJson.isEmpty()) {
                        System.out.println("First call to Mars");
                    } else {
                        System.out.println("Phase: " + JsonEvaluator.getPhase(currentJson) + ",  lastActivePlayers: " + JsonEvaluator.getActivePlayers(lastJson) + " currentPlayers: " + JsonEvaluator.getActivePlayers(currentJson));
                        JsonEvaluator.evaluateSendingMessage(lastJson, currentJson);
                    }
                    lastJson = currentJson;
                } catch (Exception e) {
                    System.out.println("lastJson: " + lastJson);
                    System.out.println("currentJson" + currentJson);
                    lastJson = currentJson;
                    System.out.println(e.getMessage());
                }
            },
            50,
            1500,
            TimeUnit.MILLISECONDS
        );

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    System.out.println("Looking for new config");
                    try {
                        String apiUrl = WebController.readConfigUrl();
                        if (apiUrl != null && !marsUrl.equals(apiUrl)) {
                            System.out.println("New url! " + apiUrl);
                            marsUrl = apiUrl;
                        }
                    } catch (Exception e) {
                        System.out.println("Issues looking for config: " + e.getMessage());
                    }
                },
                3,
                30,
                TimeUnit.SECONDS
        );
    }
}
