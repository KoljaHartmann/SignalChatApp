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
        final ScheduledFuture<?>[] signalThread = {Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                SignalController::processIncomingMessages, 0, 3, TimeUnit.SECONDS
        )};

        // Mars Json check
        final ScheduledFuture<?>[] marsThread = {Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                JsonEvaluator::processGameState, 50, 1500, TimeUnit.MILLISECONDS
        )};

        // Scheduled room cleanup each day at 10:45am
        int currentSeconds = LocalTime.now().toSecondOfDay();
        int scheduledSeconds = 38700;
        int delay = scheduledSeconds > (currentSeconds + 5) ? scheduledSeconds - currentSeconds : (scheduledSeconds + 86400) - currentSeconds;
        //TODO nächste Reinigung ins file loggen
        System.out.println("Current time in seconds: " + currentSeconds + ". Scheduling next room clean up in " + delay + " seconds.");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                RoboRockController::cleanRoom, delay, 86400, TimeUnit.SECONDS
        );

        // Check if Signal and Mars Threads are still alive
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                //TODO das irgendwie save machen, erstmal loggen, checken was da passiert und so.
                () -> {
                    try {
                        System.out.println("Current state of signal thread:\n" + signalThread[0] + "\n" + signalThread[0].get() + "\n" + signalThread[0].getDelay(TimeUnit.MILLISECONDS));
                        if (signalThread[0].isDone()) {
                            FileLogger.logWarning("Signal Thread is down. Restarting..");
                            signalThread[0] = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                                    SignalController::processIncomingMessages, 0, 3, TimeUnit.SECONDS
                            );
                        }
                        System.out.println("Current state of mars thread:\n" + marsThread[0] + "\n" + marsThread[0].get() + "\n" + marsThread[0].getDelay(TimeUnit.MILLISECONDS));
                        if (marsThread[0].isDone()) {
                            FileLogger.logWarning("Mars Thread is down. Restarting..");
                            SignalController.sendMessage("Oh, da ist mir wohl ein kleines Malheur passiert. Ich werde mich darum kümmern."
                                    , GlobalConfig.getInstance().getSignalMarsChatGroup());
                            marsThread[0] = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                                    JsonEvaluator::processGameState, 50, 1500, TimeUnit.MILLISECONDS
                            );
                        }
                    } catch (Throwable e) {
                        FileLogger.logError(e);
                    }
                }, 5, 10, TimeUnit.SECONDS
        );
    }
}
