package SignalController;

import RoboRock.RoboRockController;
import TerraformingMars.JsonEvaluator;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        FileLogger.logInfo("Starting Signal chat bot");

        // Process incoming Signal messages
        SignalController.connect();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                SignalController::processIncomingMessages, 0, 3, TimeUnit.SECONDS
        );

        // Mars Json check
        ScheduledFuture<?> marsThread = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                JsonEvaluator::processGameState, 50, 1500, TimeUnit.MILLISECONDS
        );
        GlobalConfig.getInstance().setMarsThread(marsThread);

        // Scheduled room cleanup each day at 10:45am
        int currentSeconds = LocalTime.now().toSecondOfDay();
        int scheduledSeconds = 38700;
        int nextCleanup = scheduledSeconds > (currentSeconds + 5) ? scheduledSeconds - currentSeconds : (scheduledSeconds + 86400) - currentSeconds;
        System.out.println("Current time in seconds: " + currentSeconds + ". Scheduling next room clean up in " + nextCleanup + " seconds.");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                RoboRockController::cleanRoom, nextCleanup, 86400, TimeUnit.SECONDS
        );

        //TODO: An einem Beispiel überprüfen wie ich laufende Threads manipulieren kann. Einen Thread mit wait lang laufen lassen. Dann aus einem anderen Thread heraus, rausfindne wie groß der Stack ist, das aktuelle Ding killen und einen neuen machen.
        //TODO mars ping vernünftig machen, verstehen. Mit Timeout

        // Periodical Logger
        /*
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    FileLogger.logInfo("Current delay: " + marsThread.getDelay(TimeUnit.MILLISECONDS));
                    if (marsThread.getDelay(TimeUnit.MILLISECONDS) < -300000) {
                        FileLogger.logInfo("Ping to Mars stalled for 30 seconds. Canceling Thread and clearing queue.");
                        JsonEvaluator.resetLastPingToMars();
                        marsThread.cancel(true);
                        SignalController.sendMessage("Mars Queue stalled", GlobalConfig.getInstance().getSignalMarsConfigGroup());
                    }
                }, 1, 1, TimeUnit.MINUTES
        );

         */
    }
}
