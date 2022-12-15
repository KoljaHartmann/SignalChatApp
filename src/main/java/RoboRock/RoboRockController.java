package RoboRock;

import RoboRock.Enums.Zone;
import SignalController.FileLogger;
import SignalController.GlobalConfig;
import okhttp3.*;

import java.io.IOException;

public class RoboRockController {

    static final String rockyUrl = GlobalConfig.getInstance().getRockyUrl();

    public static Response stop() {
        return sendPlainCommand("stop_cleaning");
    }

    public static Response backToDock() {
        return sendPlainCommand("drive_home");
    }

    public static Response cleanAll() {
        return sendPlainCommand("start_cleaning");
    }

    public static Response find() {
        return sendPlainCommand("find_robot");
    }

    public static Response cleanZone(Zone zone) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create("[" + zone.getNumber() + "]", mediaType);
            Request request = new Request.Builder()
                    .url(rockyUrl + "/api/start_cleaning_zones_by_id")
                    .method("PUT", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            return client.newCall(request).execute();
        } catch (IOException e) {
            FileLogger.logError(e);
            return null;
        }
    }

    private static Response sendPlainCommand(String command) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create("empty", mediaType);
            Request request = new Request.Builder()
                    .url(rockyUrl + "/api/" + command)
                    .method("PUT", body)
                    .build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            FileLogger.logError(e);
            return null;
        }
    }



}
