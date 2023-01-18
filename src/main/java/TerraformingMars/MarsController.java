package TerraformingMars;

import SignalController.FileLogger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class MarsController {


    private static boolean marsGameFinished = true;
    private static String activePlayer = "";
    private static long lastPingTime = 0;

    public static JSONObject readMarsJson(String url) {
        if (url == null || url.isEmpty()) {
            System.out.println("No url configured");
            return null;
        }
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            FileLogger.logError("Problems reading JSON from " + url, e);
            return null;
        }
    }

    public static boolean getMarsGameFinished() {
        return marsGameFinished;
    }

    public static void setMarsGameFinished(boolean finished) {
        marsGameFinished = finished;
    }

    public static void storeActivePlayer(String player) {
        activePlayer = player;
        lastPingTime = Instant.now().getEpochSecond();
    }

    public static String getActivePlayer() {
        return activePlayer;
    }

    public static long getLastPingTime() {
        return lastPingTime;
    }

    public static void storePingTime() {
        lastPingTime = Instant.now().getEpochSecond();
    }
}
