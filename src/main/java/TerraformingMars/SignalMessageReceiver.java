package TerraformingMars;

import SignalController.GlobalConfig;
import SignalController.SignalController;

public class SignalMessageReceiver {

    public static void receive(String groupId, String body) {
        String[] bodyParts = body.trim().split(" ");
        if (bodyParts.length == 2) {
            String command = bodyParts[0].trim();
            String parameter = bodyParts[1].trim();

            if (command.equals("setGameUrl") || command.equals("Url")) {
                String url = parseUrl(parameter, groupId);
                if (url != null) {
                    System.out.println("setting GameUrl to [" + url + "]");
                    GlobalConfig.getInstance().setGameUrl(url);
                    SignalController.sendMessage("GameUrl configured successfully", groupId);
                }
            } else {
                System.out.println("ERROR: unknown Command [" + command + "] parameter: [" + parameter + "]");
                SignalController.sendMessage("ERROR: unknown Command [" + command + "] parameter: [" + parameter + "]", groupId);
            }
        } else {
            System.out.println("Unable to handle message, must contain exactly two parts: " + body);
            SignalController.sendMessage("Unable to handle message, must contain exactly two parts: " + body, groupId);
        }
    }

    private static String parseUrl(String url, String groupId) {
        if (url.contains("/api/player")) {
            return url;
        } else if(url.contains("/player")) {
            return url.replace("/player", "/api/player");
        } else {
            SignalController.sendMessage("ERROR: Could not parse url " + url, groupId);
            return null;
        }
    }
}
