package SignalController;

import TerraformingMars.MarsController;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;
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
    private final static String TM_GAME_PING_TIMESTAMP_ENV = "TM_GAME_PING_TIMESTAMP";
    private final static String TM_GAME_ACTIVE_PLAYER_ENV = "TM_GAME_ACTIVE_PLAYER";

    // RoboRock
    private final static String SIGNAL_ROCKY_GROUP_ENV = "SIGNAL_ROCKY_GROUP";
    private final static String ROCKY_URL_ENV = "ROCKY_URL";

    private static GlobalConfig instance;

    private String gameUrl;
    private long pingTimestamp;
    private String activePlayer;
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
        this.pingTimestamp = System.getenv(TM_GAME_PING_TIMESTAMP_ENV) != null ? Long.parseLong(System.getenv(TM_GAME_PING_TIMESTAMP_ENV)) : Instant.now().getEpochSecond();
        this.activePlayer = System.getenv(TM_GAME_ACTIVE_PLAYER_ENV) != null ? System.getenv(TM_GAME_ACTIVE_PLAYER_ENV) : "";
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

    public long getPingTimestamp() {
        return pingTimestamp;
    }

    public void setPingTimestamp(long pingTimestamp) {
        this.pingTimestamp = pingTimestamp;
        try {
            writePingTimestampInServiceEnvFile(pingTimestamp);
        } catch (IOException e) {
            SignalController.sendMessage("Could not edit env file for update Timestamp: " + e.getCause() + " " + e.getMessage() + " " + e, signalMarsConfigGroup);
        }
    }

    private void writePingTimestampInServiceEnvFile(long pingTimestamp) throws IOException {
        Path envPath = Paths.get(this.serviceEnvPath);
        String envVariable = TM_GAME_PING_TIMESTAMP_ENV + "=" + pingTimestamp;

        List<String> rows = Files.readAllLines(envPath);
        List<String> newRows = new ArrayList<>();
        for (String row : rows) {
            if (row.startsWith(TM_GAME_PING_TIMESTAMP_ENV)) {
                newRows.add(envVariable);
            } else {
                newRows.add(row);
            }
        }
        Files.write(envPath, newRows);
    }

    public String getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
        try {
            writeActivePlayerInServiceEnvFile(activePlayer);
        } catch (IOException e) {
            SignalController.sendMessage("Could not edit env file for update Active Player: " + e.getCause() + " " + e.getMessage() + " " + e, signalMarsConfigGroup);
        }
    }

    private void writeActivePlayerInServiceEnvFile(String activePlayer) throws IOException {
        Path envPath = Paths.get(this.serviceEnvPath);
        String envVariable = TM_GAME_ACTIVE_PLAYER_ENV + "=" + activePlayer;

        List<String> rows = Files.readAllLines(envPath);
        List<String> newRows = new ArrayList<>();
        for (String row : rows) {
            if (row.startsWith(TM_GAME_ACTIVE_PLAYER_ENV)) {
                newRows.add(envVariable);
            } else {
                newRows.add(row);
            }
        }
        Files.write(envPath, newRows);
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
                "\n\t Ping Timestamp [%s]" +
                "\n\t Active Player [%s]" +
                "\n\t Signal Cli Path [%s]" +
                "\n\t Signal Username [%s]" +
                "\n\t Rocky Chat Group [%s]" +
                "\n\t Rocky Url [%s]" +
                "\n\t Mars Chat Group [%s]" +
                "\n\t Mars Config Group[%s]", gameUrl, pingTimestamp, activePlayer, signalCliPath, signalUsername, signalRockyGroup, rockyUrl, signalMarsChatGroup, signalMarsConfigGroup);
    }


}
