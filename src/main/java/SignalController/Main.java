package SignalController;

import TerraformingMars.JsonEvaluator;
import TerraformingMars.MarsController;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main {

    private static String lastUsedUrl = "";
    private static JSONObject lastJson;
    private static JSONObject currentJson;

    public static void main(String[] args) {
        FileLogger.logInfo("Starting Chatbot");
        GlobalConfig globalConfig = GlobalConfig.getInstance();

        SignalController.connect();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                SignalController::receiveMessages, 0, 3, TimeUnit.SECONDS
        );

        // Mars Json check
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    try {
                        if (!lastUsedUrl.equals(globalConfig.getGameUrl())) {
                            System.out.println("GameUrl Changed. resetting lastJson");
                            lastJson = null;
                        }
                        lastUsedUrl = globalConfig.getGameUrl();

                        System.out.println("CurrentUrl: [" + globalConfig.getGameUrl() + "]");

                        currentJson = MarsController.readMarsJson(globalConfig.getGameUrl());
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
                        FileLogger.logError("Error in the Mars Json Check:", e);
                        FileLogger.logError("lastJson: " + lastJson);
                        FileLogger.logError("currentJson" + currentJson);
                        lastJson = currentJson;
                    }
                },
                50,
                1500,
                TimeUnit.MILLISECONDS
        );
    }
}
