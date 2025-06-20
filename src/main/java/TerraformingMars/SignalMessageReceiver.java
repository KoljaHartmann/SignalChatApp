package TerraformingMars;

import SignalController.GlobalConfig;
import SignalController.SignalController;
import SignalController.FileLogger;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SignalMessageReceiver {

    public static void receive(String groupId, String body) {
        String lowerCaseBody = body.toLowerCase(Locale.ROOT);
        if (lowerCaseBody.startsWith("setgameurl ") || lowerCaseBody.startsWith("url")) {
            setGameUrl(lowerCaseBody, groupId);
        } else if (lowerCaseBody.startsWith("echo ")) {
            String message = body.substring(5);
            System.out.println("echo " + message);
            SignalController.sendMessage(message, groupId);
        } else if (lowerCaseBody.startsWith("fw ")) {
            String message = body.substring(3);
            System.out.println("fw " + message);
            SignalController.sendMessage(message, GlobalConfig.getInstance().getSignalMarsChatGroup());
        } else if (lowerCaseBody.equals("kill!")) {
            FileLogger.logInfo("Attempting to kill mars thread.");
            ScheduledFuture<?> marsThread = GlobalConfig.getInstance().getMarsThread();
            if (marsThread != null) {
                marsThread.cancel(true);
                GlobalConfig.getInstance().setMarsThread(null);
            }
            SignalController.sendMessage("SILENCE! I KILL YOU!", groupId);
        } else if (lowerCaseBody.equals("restart")) {
            FileLogger.logInfo("Attempting to kill mars thread.");
            ScheduledFuture<?> marsThread = GlobalConfig.getInstance().getMarsThread();
            if (marsThread != null) {
                marsThread.cancel(true);
            }
            ScheduledFuture<?> restartedMarsThread = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                    JsonEvaluator::processGameState, 50, 1500, TimeUnit.MILLISECONDS
            );
            GlobalConfig.getInstance().setMarsThread(restartedMarsThread);
            SignalController.sendMessage("Habe versucht neu zu starten :)", groupId);
        } else {
            System.out.println("ERROR: unknown Command " + lowerCaseBody);
            SignalController.sendMessage("ERROR: unknown Command " + lowerCaseBody, groupId);
        }
    }

    private static void setGameUrl(String lowerCaseBody, String groupId) {
        String[] split = lowerCaseBody.split(" ");
        if (split.length == 2) {
            String url = parseUrl(split[1], groupId);
            if (url != null) {
                System.out.println("setting GameUrl to [" + url + "]");
                GlobalConfig.getInstance().setGameUrl(url);
                try {
                    GlobalConfig.getInstance().writeGameUrlInServiceEnvFile(url);
                    SignalController.sendMessage("GameUrl configured successfully", groupId);
                } catch (IOException e) {
                    SignalController.sendMessage("Could not edit env file: " + e.getCause() + " " + e.getMessage() + " " + e, groupId);
                }
            }
        } else {
            SignalController.sendMessage("Falsches Format du Bob", groupId);
        }
    }

    private static String parseUrl(String url, String groupId) {
        if (url.contains("/api/player")) {
            return url;
        } else if(url.contains("/player")) {
            return url.replace("/player", "/api/player");
        } else {
            SignalController.sendMessage("ERROR: Could not parse url " + url, groupId);
            return null;
        }
    }
}
