package RoboRock;

import SignalController.GlobalConfig;
import okhttp3.*;

public class RoboRockController {

    static final String rockyUrl = GlobalConfig.getInstance().getRockyUrl();

    public static Response stop() {
        return sendCommand("stop_cleaning");
    }

    public static Response backToDock() {
        return sendCommand("drive_home");
    }

    public static Response cleanAll() {
        return sendCommand("start_cleaning");
    }

    public static Response find() {
        return sendCommand("find_robot");
    }

    private static Response sendCommand(String command) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "empty");
            Request request = new Request.Builder()
                    .url(rockyUrl + "/api/" + command)
                    .method("PUT", body)
                    .build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



}
