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

        // Scheduled room cleanup each day at 10:45am
        int currentSeconds = LocalTime.now().toSecondOfDay();
        int scheduledSeconds = 38700;
        int nextCleanup = scheduledSeconds > (currentSeconds + 5) ? scheduledSeconds - currentSeconds : (scheduledSeconds + 86400) - currentSeconds;
        System.out.println("Current time in seconds: " + currentSeconds + ". Scheduling next room clean up in " + nextCleanup + " seconds.");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                RoboRockController::cleanRoom, nextCleanup, 86400, TimeUnit.SECONDS
        );


        // Periodical Logger
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    long delay = marsThread.getDelay(TimeUnit.MILLISECONDS);
                    boolean done = marsThread.isDone();
                    boolean cancelled = marsThread.isCancelled();
                    String s = marsThread.toString();
                    FileLogger.logInfo(String.format("State of Mars Thread: Delay = %s; Done = %s; Cancelled = %s; String = %s", delay, done, cancelled, s));
                }, 10, 10, TimeUnit.SECONDS
        );
    }
}
