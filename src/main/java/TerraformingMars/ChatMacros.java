package TerraformingMars;

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
            "Gomfunkel","Daniel",
            "DartVader","Kolja",
            "Dart Vader","Kolja"
    );

    private static final ArrayList<String> researchPingList = new ArrayList<>(
            Arrays.asList(
                    "Karten oder 'n Stück Holz!",
                    "Karte her, sonst Taschen leer!",
                    "Da brat' mir einer 'nen Storch und die Beine recht knusprig! Los, spiel!"
                    "Kauf deine verdammten Karten!",
                    "Weniger Eierschaukeln, mehr Karten kaufen!",
                    "Nur die Karten kommen in' Garten!",
                    "Ich muss dich leider informieren: Vom Warten werden die Karten nicht besser!",
                    "Niemand hat die Absicht, für dich deine Karten zu kaufen!",
                    "Dies sind keine Fake News!",
                    "Was letzte Preis?"
            ));

    private static final ArrayList<String> draftMessageList = new ArrayList<>(
            Arrays.asList(
                    "Ping @all. Es darf gedraftet werden.",
                    "Ping @all. Bitte draften Sie jetzt.",
                    "Ping @all. Eine neue Draftrunde startet.",
                    "Euer Volk wünscht einen Draft!",
                    "Draften",
                    "Dröft"
            ));

    private static final ArrayList<String> reminderMessageList = new ArrayList<>(
            Arrays.asList(
                    "Darf ich Sie an Ihren Zug erinnern?",
                    "Ich mache dir ein Angebot, das du nicht ablehnen kannst. Mach deinen Zug. Oder jemand wird sich darum kümmern, capice?",
                    "Ich möchte Sie höflichst daran erinnern, dass Sie am Zug sind.",
                    "Ihr Tag ist stehts so stressig, vielleicht möchten Sie sich mit einem Zug in Terraforming Mars erfreuen?",
                    "Ein ausgesprochen schwieriger Zug, nicht wahr?",
                    "Wenn ich Ihnen einen Tipp geben darf, ich würde empfehlen zu passen.",
                    "'Gut Ding will Weile haben', sagt der Volksmund! Nur allzu lang verweilen sollten Sie bei Ihrem Zug nicht!"
            ));

    private static final Random rand = new Random();

    public static String getPlayerName(String player) {
        final String name = playerNames.get(player);
        return name != null ? name : player;
    }

    public static String getSimplePing(String player) { return "Ping @" + getPlayerName(player); }

    public static String getDraftMessage() {
        return draftMessageList.get(rand.nextInt(draftMessageList.size()));
    }

    public static String getResearchMessage() {
        return "Ping @all. Karten kaufen.";
    }

    public static String getResearchPing(String player) {
        return "Ping @" + getPlayerName(player) + ". " + researchPingList.get(rand.nextInt(researchPingList.size()));
    }

    public static String finalGreeneryPing(String player) { return "Ping @" + getPlayerName(player) + ", bitte plazieren Sie Ihre letzte Grünfläche."; }

    public static String getWinnerCelebrations(String winner, int winnerVP, boolean tieBreakerWin) {
        if (tieBreakerWin) {
            return getPlayerName(winner) + " gewinnt über den Tiebreaker. Was ein knapper Sieg!";
        } else {
            return getPlayerName(winner) + " gewinnt mit " + winnerVP + " Punkten. Gratulation!";
        }
    }

    public static String getDrawCelebrations(String winner, String secondPlace) {
        return "Unglaublich. Ein Gleichstand zwischen " + getPlayerName(winner) + " und " + getPlayerName(secondPlace) + "! Herzlichen Glückwunsch!";
    }

    public static String getReminderPing(String player) {
        return "Ping @" + getPlayerName(player) + ". " + reminderMessageList.get(rand.nextInt(reminderMessageList.size()));
    }
}
