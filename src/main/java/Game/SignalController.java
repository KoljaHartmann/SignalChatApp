package Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SignalController {

    synchronized public static void sendMessage(String message, String groupId) {

        GlobalConfig globalConfig = GlobalConfig.getInstance();

        if (groupId != null && globalConfig.getSignalCliPath() != null
                && globalConfig.getSignalUsername() != null) {

            ArrayList<String> cmdList = new ArrayList<>();

            cmdList.add(globalConfig.getSignalCliPath());
            cmdList.add("-u");
            cmdList.add(globalConfig.getSignalUsername());
            cmdList.add("send");
            cmdList.add("-m");
            cmdList.add(message);
            cmdList.add("-g");
            cmdList.add(groupId);

            try {
                Process proc = Runtime.getRuntime().exec(cmdList.toArray(new String[0]));

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(proc.getErrorStream()));

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));
                // Read the output from the command
                String line;
                System.out.println("INFO");
                while ((line = stdInput.readLine()) != null) {
                    System.out.println(line);
                }

                System.out.println("ERROR");
                while ((line = stdError.readLine()) != null) {
                    System.out.println(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: Unable to send message. Mandatory globalConfig Values not set. \n\t" + globalConfig);
        }
    }

    public static void connect() {
        System.out.println("connect, attempt accepting configured group invitations");

        GlobalConfig globalConfig = GlobalConfig.getInstance();

        String baseCommand = String.format("%s -u %s updateGroup -g ",
                globalConfig.getSignalCliPath(),
                globalConfig.getSignalUsername());

        try {
            if(globalConfig.getSignalConfigGroup() != null && !globalConfig.getSignalConfigGroup().isEmpty()) {
                Runtime.getRuntime().exec(baseCommand + globalConfig.getSignalConfigGroup());
            }
            if(globalConfig.getSignalSendGroup() != null && !globalConfig.getSignalSendGroup().isEmpty()) {
                Runtime.getRuntime().exec(baseCommand + globalConfig.getSignalSendGroup());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    synchronized public static void receiveMessages() {
        System.out.println("receiving messages");

        GlobalConfig globalConfig = GlobalConfig.getInstance();
        if (globalConfig.getSignalCliPath() != null
                && globalConfig.getSignalUsername() != null) {

            String command = String.format("%s -u %s receive",
                    globalConfig.getSignalCliPath(),
                    globalConfig.getSignalUsername());

            try {
                Process proc = Runtime.getRuntime().exec(command);
                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(proc.getErrorStream()));

                // Read the output from the command
                String line;
                String groupId = null;
                String body = null;
                boolean groupFound = false;
                while ((line = stdInput.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("Envelope")) {
                        System.out.println("START");
                        groupId = null;
                        body = null;
                        groupFound = false;
                    }
                    if (line.startsWith("Body:")) {
                        body = line.replace("Body:", "").trim();
                    }
                    if (line.startsWith("Group info:")) {
                        groupFound = true;
                    }
                    if (groupFound && line.startsWith("Id:")) {
                        groupId = line.replace("Id:", "").trim();
                        groupFound = false;
                    }

                    if (line.isEmpty()) {
                        handleIncomingMessage(groupId, body);
                        groupId = null;
                        body = null;
                    }

                    System.out.println(line);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static private void handleIncomingMessage(String groupId, String body) {
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        if (groupId != null && body != null && groupId.equals(globalConfig.getSignalConfigGroup())) {
            String[] bodyParts = body.trim().split(" ");
            if (bodyParts.length == 2) {
                String command = bodyParts[0].trim();
                String parameter = bodyParts[1].trim();

                if (command.equals("setGameUrl") || command.equals("Url")) {
                    String url = parseUrl(parameter, groupId);
                    if (url != null) {
                        System.out.println("setting GameUrl to [" + url + "]");
                        globalConfig.setGameUrl(url);
                    }
                } else {
                    System.out.println("ERROR: unknown Command [" + command + "] parameter: [" + parameter + "]");
                    sendMessage("ERROR: unknown Command [" + command + "] parameter: [" + parameter + "]", groupId);
                }
            } else {
                System.out.println("Unable to handle message, too many parts: " + body);
            }

        }

    }

    private static String parseUrl(String url, String groupId) {
        if (url.contains("/api/player")) {
            return url;
        } else if(url.contains("/player")) {
            return url.replace("/player", "/api/player");
        } else {
            sendMessage("ERROR: Could not parse url " + url, groupId);
            return null;
        }
    }

}
