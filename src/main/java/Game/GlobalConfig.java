package Game;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GlobalConfig {

    private final static String SIGNAL_USERNAME_ENV = "SIGNAL_USERNAME";
    private final static String SIGNAL_SEND_GROUP_ENV = "SIGNAL_SEND_GROUP";
    private final static String SIGNAL_CONFIG_GROUP_ENV = "SIGNAL_CONFIG_GROUP";
    private final static String SIGNAL_CLI_PATH_ENV = "SIGNAL_CLI_PATH";

    private static GlobalConfig instance;

    private String gameUrl;
    private String signalUsername;
    private String signalSendGroup;
    private String signalConfigGroup;
    private String signalCliPath;

    private Lock lock;

    private GlobalConfig() {
        this.gameUrl = "";
        this.signalUsername = System.getenv(SIGNAL_USERNAME_ENV);
        this.signalSendGroup = System.getenv(SIGNAL_SEND_GROUP_ENV);
        this.signalConfigGroup = System.getenv(SIGNAL_CONFIG_GROUP_ENV);
        this.signalCliPath = System.getenv(SIGNAL_CLI_PATH_ENV);
        System.out.println("Config Created: \n" + this);
        this.lock = new ReentrantLock();
    }

    public static GlobalConfig getInstance() {
        if (instance == null) {
            instance = new GlobalConfig();
        }
        return instance;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public String getSignalSendGroup() {
        return signalSendGroup;
    }

    public void setSignalSendGroup(String signalSendGroup) {
        this.signalSendGroup = signalSendGroup;
    }

    public String getSignalUsername() {
        return signalUsername;
    }

    public void setSignalUsername(String signalUsername) {
        this.signalUsername = signalUsername;
    }

    public String getSignalConfigGroup() {
        return signalConfigGroup;
    }

    public void setSignalConfigGroup(String signalConfigGroup) {
        this.signalConfigGroup = signalConfigGroup;
    }

    public String getSignalCliPath() {
        return signalCliPath;
    }

    public void setSignalCliPath(String signalCliPath) {
        this.signalCliPath = signalCliPath;
    }

    public String toString() {
        return String.format("GlobalConfig: " +
                "\n\t URL: [%s]" +
                "\n\t Signal Username [%s]" +
                "\n\t Signal Target [%s]" +
                "\n\t Signal Config [%s]", gameUrl, signalUsername, signalSendGroup, signalConfigGroup);
    }

    public Lock getLock() {
        return lock;
    }
}
