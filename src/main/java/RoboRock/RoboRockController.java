package RoboRock;

import RoboRock.Enums.Zone;
import SignalController.FileLogger;
import SignalController.GlobalConfig;
import SignalController.SignalController;
import okhttp3.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RoboRockController {

    private static ScheduledFuture<?> scheduledRoomCleanup;

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
        String rockyUrl = GlobalConfig.getInstance().getRockyUrl();
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
        } catch (Exception e) {
            FileLogger.logError(e);
            return null;
        }
    }

    private static Response sendPlainCommand(String command) {
        String rockyUrl = GlobalConfig.getInstance().getRockyUrl();
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

    public static boolean stopNextScheduledRoomCleanup() {
        return scheduledRoomCleanup != null && scheduledRoomCleanup.cancel(true);
    }

    public static void cleanRoom() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        FileLogger.logInfo("Room clean up started. We have " + dayOfWeek);
        if (dayOfWeek == DayOfWeek.MONDAY) {
            sendSignalMessage("Heute ist der Flur an der Reihe, in einer Viertelstunde sauge ich den Flur.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.FLUR);}, 15, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.TUESDAY) {
            sendSignalMessage("Heute ist das Wohnzimmer an der Reihe, in einer Viertelstunde sauge ich das Wohnzimmer.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.WOHNZIMMER);}, 15, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.WEDNESDAY) {
            sendSignalMessage("Heute ist die Küche an der Reihe, in einer Viertelstunde sauge ich die Küche.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.KUECHE);}, 15, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.THURSDAY) {
            sendSignalMessage("Heute ist der Flur an der Reihe, in einer Viertelstunde sauge ich den Flur.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.FLUR);}, 15, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.FRIDAY) {
            sendSignalMessage("Heute ist das Schlafzimmer an der Reihe, in einer Viertelstunde sauge ich das Schlafzimmer.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.SCHLAFZIMMER);}, 15, TimeUnit.MINUTES);
//          sendSignalMessage("Heute ist das Arbeitszimmer an der Reihe, wärend der Mittagspause sauge ich das Arbeitszimmer.");
//          scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.MULTIZIMMER);}, 90, TimeUnit.MINUTES);
        }
    }

    private static void sendSignalMessage(String message) {
        SignalController.sendMessage(message, GlobalConfig.getInstance().getSignalRockyGroup());
    }
}
