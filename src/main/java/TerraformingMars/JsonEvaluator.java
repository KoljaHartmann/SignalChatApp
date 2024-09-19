package TerraformingMars;

import SignalController.FileLogger;
import SignalController.GlobalConfig;
import SignalController.SignalController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;

import static TerraformingMars.Phases.*;

public class JsonEvaluator {

    private static String lastUsedUrl = "";
    private static long lastPingToMars = 0;
    private static JSONObject lastJson;
    private static JSONObject currentJson;
    private static final GlobalConfig globalConfig = GlobalConfig.getInstance();

    //TODO Enum zum Aktuellen Zustand statt Chatmessagechaos
    private static void evaluateSendingMessage(JSONObject lastJsonObject, JSONObject currentJsonObject) {
        String message = "";
        Phases currentPhase = getPhase(currentJsonObject);
        Phases lastPhase = getPhase(lastJsonObject);
        final ArrayList<String> currentPlayers = getActivePlayers(currentJsonObject);
        final ArrayList<String> lastActivePlayers = getActivePlayers(lastJsonObject);

        if (MarsController.getMarsGameFinished()) {
            return;
        } else if (currentPhase.equals(DRAFTING)) {
            if (lastActivePlayers.size() > 1 && currentPlayers.size() == 1) {
                message = ChatMacros.getSimplePing(currentPlayers.get(0));
            } else if (lastActivePlayers.size() == 1 && currentPlayers.size() > 1) {
                message = ChatMacros.getDraftMessage();
            }
        } else if (currentPhase.equals(RESEARCH)) {
            if (lastPhase != null && !lastPhase.equals(RESEARCH)) {
                message = ChatMacros.getResearchMessage();
            } else if (lastActivePlayers.size() > 1 && currentPlayers.size() == 1) {
                message = ChatMacros.getResearchPing(currentPlayers.get(0));
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
            if (lastPhase != null && !lastPhase.equals(PRODUCTION)) {
                message = ChatMacros.finalGreeneryPing(currentPlayers.get(0));
            } else if (currentPlayers.size() == 1 && lastActivePlayers.size() > 0) {
                if (!currentPlayers.get(0).equals(lastActivePlayers.get(0))) {
                    message = ChatMacros.finalGreeneryPing(currentPlayers.get(0));
                }
            }
        } else if (currentPhase.equals(PRELUDES)) {
            if (lastPhase.equals(RESEARCH)) {
                message = ChatMacros.getSimplePing(currentPlayers.get(0));
            } else if (currentPlayers.size() == 1 && lastActivePlayers.size() > 0) {
                if (!currentPlayers.get(0).equals(lastActivePlayers.get(0))) {
                    message = ChatMacros.getSimplePing(currentPlayers.get(0));
                }
            }
        } else if (currentPhase.equals(END)) {
            message = getWinnerMessage(currentJsonObject);
            MarsController.setMarsGameFinished(true);
            lastJson = null;
            currentJson = null;
        }
        if (!message.isEmpty() && (globalConfig.getSignalMarsChatGroup() != null)) {
            SignalController.sendMessage(message, globalConfig.getSignalMarsChatGroup());
            if (currentPlayers.size() == 1) {
                MarsController.storeActivePlayer(currentPlayers.get(0));
            } else {
                MarsController.storeActivePlayer("");
            }
        } else {
            if(reminderPingReasonable()) {
                if (!MarsController.getActivePlayer().isEmpty()) {
                    SignalController.sendMessage(ChatMacros.getReminderPing(MarsController.getActivePlayer(), currentPhase), globalConfig.getSignalMarsChatGroup());
                    MarsController.storePingTime();
                } else {
                    //TODO mehrere Spieler
                }
            }
        }
    }

    private static boolean reminderPingReasonable() {
        //ping again after 5 1/2 hours
        if (LocalTime.now().getHour() > 8) {
            return Instant.now().getEpochSecond() - MarsController.getLastPingTime() > 19800;
        }
        return false;
    }

    private static ArrayList<String> getActivePlayers(JSONObject jsonObject) {
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

    private static String getWinnerMessage(JSONObject jsonObject) {
        final JSONArray players = (JSONArray) jsonObject.get("players");
        String winner = "";
        int winnerVP = 0;
        int winnerMC = 0;
        boolean tieBreakerWin = false;
        boolean doubleTie = false;
        String secondPlace = "";

        for (Object playerObject : players) {
            final JSONObject player = (JSONObject) playerObject;
            final JSONObject pointsBreakdown = (JSONObject) player.get("victoryPointsBreakdown");
            int playerVP = (int) pointsBreakdown.get("total");
            if (playerVP > winnerVP) {
                winner = (String) player.get("name");
                winnerVP = playerVP;
                winnerMC = (int) player.get("megaCredits");
                tieBreakerWin = false;
                doubleTie = false;
            } else if (playerVP == winnerVP) {
                int megaCredits = (int) player.get("megaCredits");
                if (megaCredits > winnerMC) {
                    winner = (String) player.get("name");
                    winnerMC = (int) player.get("megaCredits");
                    tieBreakerWin = true;
                    doubleTie = false;
                } else if (megaCredits == winnerMC) {
                    secondPlace = (String) player.get("name");
                    winnerMC = (int) player.get("megaCredits");
                    tieBreakerWin = true;
                    doubleTie = true;
                }
            }
        }
        if (doubleTie) {
            return ChatMacros.getDrawCelebrations(winner, secondPlace);
        } else {
            return ChatMacros.getWinnerCelebrations(winner, winnerVP, tieBreakerWin);
        }
    }

    private static Phases getPhase(JSONObject jsonObject) {
        final JSONObject game = (JSONObject) jsonObject.get("game");
        final String phase = (String) game.get("phase");
        return Phases.valueOf(phase.toUpperCase());
    }

    public static void processGameState() {
        if (lastPingToMars >= Instant.now().getEpochSecond()) {
            FileLogger.logInfo("Avoiding DDOS. Last ping to mars was " + Instant.ofEpochSecond(lastPingToMars));
            return;
        }
        try {
            if (!lastUsedUrl.equals(globalConfig.getGameUrl())) {
                System.out.println("GameUrl Changed. resetting lastJson");
                lastJson = null;
            }
            lastUsedUrl = globalConfig.getGameUrl();
            System.out.println("CurrentUrl: [" + globalConfig.getGameUrl() + "]");
            currentJson = MarsController.readMarsJson(globalConfig.getGameUrl());
            if (currentJson == null || currentJson.isEmpty()) {
                FileLogger.logInfo("No Json found");
                return;
            } else if (lastJson == null || lastJson.isEmpty()) {
                FileLogger.logInfo("First call to Mars");
            } else {
                System.out.println("Phase: " + JsonEvaluator.getPhase(currentJson) + ",  lastActivePlayers: " + JsonEvaluator.getActivePlayers(lastJson) + " currentPlayers: " + JsonEvaluator.getActivePlayers(currentJson));
                JsonEvaluator.evaluateSendingMessage(lastJson, currentJson);
            }
            lastJson = currentJson;
            lastPingToMars = Instant.now().getEpochSecond();
        } catch (Throwable e) {
            FileLogger.logError("Error in the Mars Json Check:", e);
            FileLogger.logError("lastJson: " + lastJson);
            FileLogger.logError("currentJson" + currentJson);
            lastJson = currentJson;
            lastPingToMars = Instant.now().getEpochSecond();
        }
    }

    public static void resetLastPingToMars() {
        lastPingToMars = Instant.now().getEpochSecond() + 1;
    }

}
