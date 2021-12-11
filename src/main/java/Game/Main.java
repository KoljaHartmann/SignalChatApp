package Game;

import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main {
    //TODO MARS_URL als property
    //TODO Game.Phases.INITIAL_DRAFTING

    private static String defaultMarsUrl = "http://168.119.225.172:8080/api/spectator?id=s506f68cf7ef6";
    private static JSONObject lastJson;

    public static void main(String [ ] args) {
        System.out.println("Starting Chatbot");

        GlobalConfig globalConfig = GlobalConfig.getInstance();

        globalConfig.setMarsUrl(defaultMarsUrl);
        System.out.println(globalConfig);
        SignalController.connect();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                SignalController::keepalive, 0, 5, TimeUnit.MINUTES
        );
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            () -> {
                System.out.println(globalConfig.getMarsUrl());
                JSONObject currentJson = null;
                try {
                    currentJson = MarsController.readMarsJson(globalConfig.getMarsUrl());
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
    }
}
