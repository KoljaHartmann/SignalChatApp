package Game;

import java.util.Map;

public class ChatMacros {

    private static final Map<String, String> playerNames = Map.of(
            "k4rlchen","Stefan",
            "K4rlchen","Stefan",
            "apehead","Damian",
            "BOBftl.","Lukas",
            "BOB ftl.","Lukas",
            "GomJabbar","Daniel",
            "Gom Jabbar","Daniel",
            "DartVader","Kolja",
            "Dart Vader","Kolja"
    );

    public static String getPlayerName(String player) {
        final String name = playerNames.get(player);
        return name != null ? name : player;
    }

    public static String getSimplePing(String player) { return "Ping @" + getPlayerName(player); }

    public static String getDraftMessage() { return "Ping @all. It's drafting time!"; }

    public static String getResearchMessage() { return "Ping @all. Please buy your cards!"; }

    public static String getResearchMessage(String player) { return "Ping @" + getPlayerName(player) + ". Kauf deine verdammten Karten!"; }

    public static String finalGreeneryPing(String player) { return "Ping @" + getPlayerName(player) + ", place your final Greenery"; }
}
