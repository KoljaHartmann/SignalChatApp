package RoboRock;

import RoboRock.Enums.Zone;
import SignalController.FileLogger;
import SignalController.GlobalConfig;
import SignalController.SignalController;
import okhttp3.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public static Response cleanZone(Zone... zones) {
        String rockyUrl = GlobalConfig.getInstance().getRockyUrl();
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
            String joinedZones = Arrays.stream(zones)
                    .map(e -> e.getNumber() == null ? "null" : e.getNumber().toString())
                    .collect(Collectors.joining(","));
            RequestBody body = RequestBody.create("[" + joinedZones + "]", mediaType);
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
            sendSignalMessage("Heute ist der Wohnbereich an der Reihe, um neun Uhr sauge ich den Wohnbereich.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.WOHNZIMMER, Zone.ESSZIMMER, Zone.FLUR);}, 45, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.TUESDAY) {
            sendSignalMessage("Heute ist der Schlafzimmer an der Reihe, um neun Uhr sauge ich das Schlafzimmer.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.SCHLAFZIMMER);}, 45, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.WEDNESDAY) {
            sendSignalMessage("Heute ist der Essbereich an der Reihe, um neun Uhr sauge ich den Essbereich.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.KUECHE, Zone.ESSZIMMER);}, 45, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.THURSDAY) {
            sendSignalMessage("Heute ist das Wohnzimmer an der Reihe, um neun Uhr sauge ich das Wohnzimmer.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.WOHNZIMMER);}, 45, TimeUnit.MINUTES);
        } else if (dayOfWeek == DayOfWeek.FRIDAY) {
            sendSignalMessage("Heute ist das Esszimmer an der Reihe, um neun Uhr sauge ich das Esszimmer.");
            scheduledRoomCleanup = Executors.newSingleThreadScheduledExecutor().schedule(() -> {cleanZone(Zone.ESSZIMMER);}, 45, TimeUnit.MINUTES);
        }
    }

    private static void sendSignalMessage(String message) {
        SignalController.sendMessage(message, GlobalConfig.getInstance().getSignalRockyGroup());
    }
}
