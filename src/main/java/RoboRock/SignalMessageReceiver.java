package RoboRock;

import RoboRock.Enums.Command;
import SignalController.SignalController;
import okhttp3.Response;

import java.util.*;

import static RoboRock.Enums.BasicCommand.*;
import static RoboRock.Enums.Zone.*;

public class SignalMessageReceiver {

    static Map<List<String>, Command> patternMap = Map.of(
            List.of("wo bist du"), FIND,
            List.of("wohnung", "alles"), CLEAN,
            List.of("stop", "anhalten"), STOP,
            List.of("zurück", "laden"), CHARGE,
            List.of("arbeitszimmer", "multizimmer", "kinderzimmer"), MULTIZIMMER,
            List.of("küche"), KUECHE,
            List.of("schlafzimmer"), SCHLAFZIMMER,
            List.of("wohnzimmer"), WOHNZIMMER,
            List.of("flur", "bad"), FLUR_BAD
    );

    public static void receive(String groupId, String body) {
        for (List<String> list : patternMap.keySet()) {
            for (String keyWord : list) {
                if (body.toLowerCase(Locale.ROOT).contains(keyWord)) {
                    triggerCommand(patternMap.get(list), groupId);
                    return;
                }
            }
        }
        SignalController.sendMessage("Diesen Befehl kann ich nicht verstehen :/", groupId);
    }

    private static void triggerCommand(Command command, String groupId) {
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
        } else if (command == MULTIZIMMER) {
            response = RoboRockController.cleanZone(MULTIZIMMER);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge nur das Multizimmer.", groupId);
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
        } else if (command == FLUR_BAD) {
            response = RoboRockController.cleanZone(FLUR_BAD);
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich sauge den Flur und das Bad.", groupId);
            }
        } else {
            response = null;
            SignalController.sendMessage("Kolja hat Mist gebaut und den command " + command + " nicht sauber implementiert.", groupId);
        }

        if (response != null && response.code() != 200) {
            SignalController.sendMessage("Leider habe ich einen Fehler :(", groupId);
        }
    }

}
