package Game;

public class GlobalConfig {

    private final static String SIGNAL_USERNAME_ENV = "SIGNAL_USERNAME";
    private final static String SIGNAL_TARGET_USERNAME_ENV = "SIGNAL_TARGET_USERNAME";
    private final static String SIGNAL_CONFIG_USERNAME_ENV = "SIGNAL_CONFIG_USERNAME";
    private static GlobalConfig instance;
    private String marsUrl;
    private String signalUsername;
    private String signalTargetUsername;
    private String signalConfigUsername;

    private GlobalConfig() {
        this.marsUrl = "";
        this.signalUsername = System.getenv(SIGNAL_USERNAME_ENV);
        this.signalTargetUsername = System.getenv(SIGNAL_TARGET_USERNAME_ENV);
        this.signalConfigUsername = System.getenv(SIGNAL_CONFIG_USERNAME_ENV);
        System.out.println();
    }

    public static GlobalConfig getInstance() {
        if (instance == null) {
            instance = new GlobalConfig();
        }
        return instance;
    }

    public String getMarsUrl() {
        return marsUrl;
    }

    public void setMarsUrl(String marsUrl) {
        this.marsUrl = marsUrl;
    }

    public String getSignalTargetUsername() {
        return signalTargetUsername;
    }

    public void setSignalTargetUsername(String signalTargetUsername) {
        this.signalTargetUsername = signalTargetUsername;
    }

    public String getSignalUsername() {
        return signalUsername;
    }

    public void setSignalUsername(String signalUsername) {
        this.signalUsername = signalUsername;
    }

    public String getSignalConfigUsername() {
        return signalConfigUsername;
    }

    public void setSignalConfigUsername(String signalConfigUsername) {
        this.signalConfigUsername = signalConfigUsername;
    }

    public String toString() {
        return String.format("GlobalConfig: " +
                "\n\t URL: [%s]" +
                "\n\t Signal Username [%s]" +
                "\n\t Signal Target [%s]" +
                "\n\t Signal Config [%s]", marsUrl, signalUsername, signalTargetUsername, signalConfigUsername);
    }
}
