package TerraformingMars;

import SignalController.FileLogger;
import SignalController.GlobalConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.time.Instant;

public class MarsController {


    private static boolean marsGameFinished = false;

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
        GlobalConfig.getInstance().setActivePlayer(player);
        GlobalConfig.getInstance().setPingTimestamp(Instant.now().getEpochSecond());
    }

    public static String getActivePlayer() {
        return GlobalConfig.getInstance().getActivePlayer();
    }

    public static long getLastPingTime() {
        return GlobalConfig.getInstance().getPingTimestamp();
    }

    public static void storePingTime() {
        GlobalConfig.getInstance().setPingTimestamp(Instant.now().getEpochSecond());
    }
}
