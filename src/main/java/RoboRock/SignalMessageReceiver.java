package RoboRock;

import RoboRock.Enums.Command;
import SignalController.SignalController;
import SignalController.GlobalConfig;
import okhttp3.Response;

import java.util.*;

import static RoboRock.Enums.BasicCommand.*;
import static RoboRock.Enums.CombinedZone.*;
import static RoboRock.Enums.Zone.*;

public class SignalMessageReceiver {

    //TODO Pause
    static Map<List<String>, Command> patternMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(List.of("wo bist du"), FIND),
            new AbstractMap.SimpleEntry<>(List.of("wohnung", "alles"), CLEAN),
            new AbstractMap.SimpleEntry<>(List.of("stop", "anhalten"), STOP),
            new AbstractMap.SimpleEntry<>(List.of("zurück", "laden"), CHARGE),
            new AbstractMap.SimpleEntry<>(List.of("küche"), KUECHE),
            new AbstractMap.SimpleEntry<>(List.of("schlafzimmer"), SCHLAFZIMMER),
            new AbstractMap.SimpleEntry<>(List.of("wohnzimmer"), WOHNZIMMER),
            new AbstractMap.SimpleEntry<>(List.of("flur", "bad"), FLUR),
            new AbstractMap.SimpleEntry<>(List.of("esszimmer", "küchentisch", "esstisch"), ESSZIMMER),
            new AbstractMap.SimpleEntry<>(List.of("essbereich"), ESSBEREICH),
            new AbstractMap.SimpleEntry<>(List.of("wohnbereich", "wohnen"), WOHNBEREICH),
            new AbstractMap.SimpleEntry<>(List.of("heute nicht", "nein", "überpringen", "aussetzen", "lass mal"), CANCEL_SCHEDULE)
    );

    public static void receive(String body) {
        for (List<String> list : patternMap.keySet()) {
            for (String keyWord : list) {
                if (body.toLowerCase(Locale.ROOT).contains(keyWord)) {
                    triggerCommand(patternMap.get(list));
                    return;
                }
            }
        }
        SignalController.sendMessage("Diesen Befehl kann ich nicht verstehen :/", GlobalConfig.getInstance().getSignalRockyGroup());
    }

    private static void triggerCommand(Command command) {
        String groupId = GlobalConfig.getInstance().getSignalRockyGroup();
        Response response;
        if (command == FIND) {
            response = RoboRockController.find();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Hier bin ich :)", groupId);
            }
        } else if (command == CLEAN) {
            response = RoboRockController.cleanAll();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Alles klar, ich sauge die ganze Wohnung.", groupId);
            }
        } else if (command == STOP) {
            response = RoboRockController.stop();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Oh, ich werde sofort anhalten.", groupId);
            }
        } else if (command == CHARGE) {
            response = RoboRockController.backToDock();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich fahre zur Basisstation zurück.", groupId);
            }
        } else if (command == ESSZIMMER) {
            response = RoboRockController.cleanZone(ESSZIMMER);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge nur das Esszimmer.", groupId);
            }
        } else if (command == KUECHE) {
            response = RoboRockController.cleanZone(KUECHE);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge nur die Küche.", groupId);
            }
        } else if (command == SCHLAFZIMMER) {
            response = RoboRockController.cleanZone(SCHLAFZIMMER);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge nur das Schlafzimmer.", groupId);
            }
        } else if (command == WOHNZIMMER) {
            response = RoboRockController.cleanZone(WOHNZIMMER);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge nur das Wohnzimmer.", groupId);
            }
        } else if (command == FLUR) {
            response = RoboRockController.cleanZone(FLUR);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge nur den Flur.", groupId);
            }
        } else if (command == WOHNBEREICH) {
            response = RoboRockController.cleanZone(WOHNZIMMER, ESSZIMMER, FLUR);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge den Wohnbereich.", groupId);
            }
        } else if (command == ESSBEREICH) {
            response = RoboRockController.cleanZone(KUECHE, ESSZIMMER);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge den Essbereich.", groupId);
            }
        } else if (command == CANCEL_SCHEDULE) {
            boolean canceled = RoboRockController.stopNextScheduledRoomCleanup();
            if (canceled) {
                SignalController.sendMessage("Okay, ich werde heute nicht saugen.", groupId);
            } else {
                SignalController.sendMessage("Aktuell ist keine Reinigung geplant.", groupId);
            }
            return;
        } else {
            SignalController.sendMessage("Kolja hat Mist gebaut und den command " + command + " nicht sauber implementiert.", groupId);
            return;
        }
        if (response == null) {
            SignalController.sendMessage("Rocky antwortet nicht. Eventuell ist der Staubsauger ausgeschaltet.", groupId);
        } else if (response.code() != 200) {
            SignalController.sendMessage("Leider gibt es einen Fehler. Der Fehlercode ist " + response.code(), groupId);
        }
    }

}
