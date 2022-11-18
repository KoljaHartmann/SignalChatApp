package RoboRock;

import SignalController.SignalController;
import okhttp3.Response;

import java.util.Locale;

public class SignalMessageReceiver {

    //TODO hübsch machen
    public static void receive(String groupId, String body) {
        if (body.toLowerCase(Locale.ROOT).contains("wo bist du")) {
            Response response = RoboRockController.find();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Hier bin ich :)", groupId);
            } else {
                SignalController.sendMessage("Leider habe ich einen Fehler :(", groupId);
            }
        } else if (body.toLowerCase(Locale.ROOT).contains("wohnung")) {
            Response response = RoboRockController.cleanAll();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Alles klar, ich sauge die ganze Wohnung.", groupId);
            } else {
                SignalController.sendMessage("Leider habe ich einen Fehler :(", groupId);
            }
        } else if (body.toLowerCase(Locale.ROOT).contains("stop") || body.toLowerCase(Locale.ROOT).contains("anhalten")) {
            Response response = RoboRockController.stop();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Oh, ich werde sofort anhalten.", groupId);
            } else {
                SignalController.sendMessage("Leider habe ich einen Fehler :(", groupId);
            }
        } else if (body.toLowerCase(Locale.ROOT).contains("zurück") || body.toLowerCase(Locale.ROOT).contains("laden") || body.toLowerCase(Locale.ROOT).contains("strom")) {
            Response response = RoboRockController.backToDock();
            if (response != null && response.code() == 200) {
                SignalController.sendMessage("Okay, ich fahre zur Basisstation zurück.", groupId);
            } else {
                SignalController.sendMessage("Leider habe ich einen Fehler :(", groupId);
            }
        } else {
            SignalController.sendMessage("Diesen Befehl kann ich nicht verstehen :/", groupId);
        }
    }
}
