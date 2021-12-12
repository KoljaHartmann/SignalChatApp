package Game;

import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    //TODO MARS_URL als property
    //TODO Game.Phases.INITIAL_DRAFTING

    private static String lastUsedUrl = "";
    private static JSONObject lastJson;

    public static void main(String[] args) {
        System.out.println("Starting Chatbot");

        GlobalConfig globalConfig = GlobalConfig.getInstance();

        SignalController.connect();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                SignalController::receiveMessages, 0, 20, TimeUnit.SECONDS
        );
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    globalConfig.getLock().lock();
                    if (!lastUsedUrl.equals(globalConfig.getGameUrl())) {
                        System.out.println("GameUrl Changed. resetting lastJson");
                        lastJson = null;
                    }
                    lastUsedUrl = globalConfig.getGameUrl();


                    System.out.println("CurrentUrl: [" + globalConfig.getGameUrl() + "]");
                    JSONObject currentJson = null;
                    try {
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
                        System.out.println("lastJson: " + lastJson);
                        System.out.println("currentJson" + currentJson);
                        lastJson = currentJson;
                        System.out.println(e.getMessage());
                    } finally {
                        globalConfig.getLock().unlock();
                    }
                },
                50,
                1500,
                TimeUnit.MILLISECONDS
        );
    }
}
