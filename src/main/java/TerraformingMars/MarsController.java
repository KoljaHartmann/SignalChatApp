package TerraformingMars;

import SignalController.FileLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.time.Instant;

public class MarsController {


    private static boolean marsGameFinished = false;
    private static String activePlayer = "";
    private static long lastPingTime = 0;

    /*
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
     */

    public static JSONObject readMarsJson(String url) {
        if (url == null || url.isEmpty()) {
            System.out.println("No url configured");
            return null;
        }
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url(url).get().build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (response.code() == 200 && body != null) {
                return new JSONObject(body.string());
            } else {
                FileLogger.logError("Problems reading JSON from " + url + ". Response code: " + response.code() + ". Body: " + body);
                return null;
            }
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
