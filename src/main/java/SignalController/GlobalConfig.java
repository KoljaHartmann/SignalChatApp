package SignalController;

public class GlobalConfig {

    private final static String SIGNAL_USERNAME_ENV = "SIGNAL_USERNAME";
    private final static String SIGNAL_SEND_GROUP_ENV = "SIGNAL_SEND_GROUP";
    private final static String SIGNAL_CONFIG_GROUP_ENV = "SIGNAL_CONFIG_GROUP";
    private final static String SIGNAL_CLI_PATH_ENV = "SIGNAL_CLI_PATH";
    private final static String TM_GAME_URL_ENV = "TM_GAME_URL";

    private static GlobalConfig instance;

    private String gameUrl;
    private final String signalUsername;
    private final String signalSendGroup;
    private final String signalConfigGroup;
    private final String signalCliPath;

    private GlobalConfig() {
        this.gameUrl = System.getenv(TM_GAME_URL_ENV) != null ? System.getenv(TM_GAME_URL_ENV) : "";
        this.signalUsername = System.getenv(SIGNAL_USERNAME_ENV);
        this.signalSendGroup = System.getenv(SIGNAL_SEND_GROUP_ENV);
        this.signalConfigGroup = System.getenv(SIGNAL_CONFIG_GROUP_ENV);
        this.signalCliPath = System.getenv(SIGNAL_CLI_PATH_ENV);
        System.out.println("Config Created: \n" + this);
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

    public String getSignalUsername() {
        return signalUsername;
    }

    public String getSignalConfigGroup() {
        return signalConfigGroup;
    }

    public String getSignalCliPath() {
        return signalCliPath;
    }

    public String toString() {
        return String.format("GlobalConfig: " +
                "\n\t URL: [%s]" +
                "\n\t Signal Cli Path [%s]" +
                "\n\t Signal Username [%s]" +
                "\n\t Signal Target [%s]" +
                "\n\t Signal Config [%s]", gameUrl, signalCliPath, signalUsername, signalSendGroup, signalConfigGroup);
    }

}
