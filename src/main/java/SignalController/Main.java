package SignalController;

import TerraformingMars.JsonEvaluator;

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

        // Check if Signal and Mars Threads are still alive
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> {
                    try {
                        System.out.println("Check state of threads");
                        if (signalThread[0].isDone()) {
                            FileLogger.logWarning("Signal Thread is down. Restarting..");
                            signalThread[0] = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                                    SignalController::processIncomingMessages, 0, 3, TimeUnit.SECONDS
                            );
                        }
                        if (marsThread[0].isDone()) {
                            FileLogger.logWarning("Mars Thread is down. Restarting..");
                            SignalController.sendMessage("Oh, da ist mir wohl ein kleines Malleur passiert. Ich werde mich darum kümmern."
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
