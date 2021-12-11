package Game;

import java.io.IOException;

public class SignalController {
    public static void sendMessage(String message) {
        System.out.println("msg" + message);
        try {
            Runtime.getRuntime().exec("wsl ~/signal-cli/signal-cli-0.9.2/bin/signal-cli  -u +491622834315 send -m \"" + message + "\" +4915788382196");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO
    }

    public static void connect() {
        System.out.println("connect");
        //TODO
    }

    public static void keepalive() {
        System.out.println("Keepalive, receiving messages");
        try {
            Runtime.getRuntime().exec("wsl ~/signal-cli/signal-cli-0.9.2/bin/signal-cli  -u +491622834315 receive");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
