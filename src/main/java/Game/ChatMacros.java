package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("SpellCheckingInspection")
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

    private static final ArrayList<String> researchPingList = new ArrayList<>(
            Arrays.asList(
                    "Karten oder 'n Stück Holz!",
                    "Karte her, sonst Taschen leer!",
                    "Kauf deine verdammten Karten!",
                    "Weniger Eierschaukeln, mehr Karten kaufen!",
                    "Nur die Karten kommen in' Garten!",
                    "Niemand hat die Absicht, für dich deine Karten zu kaufen!",
                    "Dies sind keine Fake News!"
            ));

    private static final ArrayList<String> draftMessageList = new ArrayList<>(
            Arrays.asList(
                    "Ping @all. It's drafting time!",
                    "Dröft"
            ));

    public static String getPlayerName(String player) {
        final String name = playerNames.get(player);
        return name != null ? name : player;
    }

    public static String getSimplePing(String player) { return "Ping @" + getPlayerName(player); }

    public static String getDraftMessage() {
        Random rand = new Random();
        return draftMessageList.get(rand.nextInt(draftMessageList.size()));
    }

    public static String getResearchMessage() { return "Ping @all. Please buy your cards!"; }

    public static String getResearchPing(String player) {
        Random rand = new Random();
        return "Ping @" + getPlayerName(player) + ". " + researchPingList.get(rand.nextInt(researchPingList.size()));
    }

    public static String finalGreeneryPing(String player) { return "Ping @" + getPlayerName(player) + ", place your final Greenery"; }
}
