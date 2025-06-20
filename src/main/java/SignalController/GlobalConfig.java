package SignalController;

import TerraformingMars.MarsController;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import java.util.concurrent.ScheduledFuture;

public class GlobalConfig {

    private final static String SIGNAL_USERNAME_ENV = "SIGNAL_USERNAME";
    private final static String SIGNAL_CLI_PATH_ENV = "SIGNAL_CLI_PATH";
    private final static String SERVICE_ENV_PATH_ENV = "SERVICE_ENV_PATH";

    // Terraforming Mars
    private final static String SIGNAL_MARS_CHAT_GROUP_ENV = "SIGNAL_MARS_CHAT_GROUP";
    private final static String SIGNAL_MARS_CONFIG_GROUP_ENV = "SIGNAL_MARS_CONFIG_GROUP";
    private final static String TM_GAME_URL_ENV = "TM_GAME_URL";

    // RoboRock
    private final static String SIGNAL_ROCKY_GROUP_ENV = "SIGNAL_ROCKY_GROUP_ENV";
    private final static String ROCKY_URL_ENV = "ROCKY_URL";

    private static GlobalConfig instance;

    private String gameUrl;
    private final String signalUsername;
    private final String signalMarsChatGroup;
    private final String signalMarsConfigGroup;
    private final String signalCliPath;
    private final String serviceEnvPath;
    private final String signalRockyGroup;
    private final String rockyUrl;
    private ScheduledFuture<?> marsThread;

    private GlobalConfig() {
        this.gameUrl = System.getenv(TM_GAME_URL_ENV) != null ? System.getenv(TM_GAME_URL_ENV) : "";
        this.signalUsername = System.getenv(SIGNAL_USERNAME_ENV);
        this.signalMarsChatGroup = System.getenv(SIGNAL_MARS_CHAT_GROUP_ENV);
        this.signalMarsConfigGroup = System.getenv(SIGNAL_MARS_CONFIG_GROUP_ENV);
        this.signalCliPath = System.getenv(SIGNAL_CLI_PATH_ENV);
        this.serviceEnvPath = System.getenv(SERVICE_ENV_PATH_ENV);
        this.signalRockyGroup = System.getenv(SIGNAL_ROCKY_GROUP_ENV);
        this.rockyUrl = System.getenv(ROCKY_URL_ENV);
        System.out.println("Config Created: \n" + this);
    }

    public static GlobalConfig getInstance() {
        if (instance == null) {
            instance = new GlobalConfig();
        }
        return instance;
    }

    public void writeGameUrlInServiceEnvFile(String url) throws IOException {
        Path envPath = Paths.get(this.serviceEnvPath);
        String envVariable = TM_GAME_URL_ENV + "=" + url;

        List<String> rows = Files.readAllLines(envPath);
        List<String> newRows = new ArrayList<>();
        for (String row : rows) {
            if (row.startsWith(TM_GAME_URL_ENV)) {
                newRows.add(envVariable);
            } else {
                newRows.add(row);
            }
        }
        Files.write(envPath, newRows);
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
        MarsController.setMarsGameFinished(false);
    }

    public void setMarsThread(ScheduledFuture<?> marsThread) {
        this.marsThread = marsThread;
    }

    public ScheduledFuture<?> getMarsThread() {
        return marsThread;
    }

    public String getSignalMarsChatGroup() {
        return signalMarsChatGroup;
    }

    public String getSignalUsername() {
        return signalUsername;
    }

    public String getSignalMarsConfigGroup() {
        return signalMarsConfigGroup;
    }

    public String getSignalRockyGroup() {
        return signalRockyGroup;
    }

    public String getRockyUrl() {
        return rockyUrl;
    }

    public String getSignalCliPath() {
        return signalCliPath;
    }

    public String toString() {
        return String.format("GlobalConfig: " +
                "\n\t Mars URL: [%s]" +
                "\n\t Signal Cli Path [%s]" +
                "\n\t Signal Username [%s]" +
                "\n\t Rocky Chat Group [%s]" +
                "\n\t Rocky Url [%s]" +
                "\n\t Mars Chat Group [%s]" +
                "\n\t Mars Config Group[%s]", gameUrl, signalCliPath, signalUsername, signalRockyGroup, rockyUrl, signalMarsChatGroup, signalMarsConfigGroup);
    }


}
