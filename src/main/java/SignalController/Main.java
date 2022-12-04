package SignalController;

import TerraformingMars.JsonEvaluator;

import java.util.concurrent.Executors;
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
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                JsonEvaluator::processGameState, 50, 1500, TimeUnit.MILLISECONDS
        );
    }
}
