package Game;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MarsController {

    public static JSONObject readMarsJson(String url) {
        if (url == null || url.isEmpty()) {
            System.out.println("No url configured");
            return null;
        }
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            System.out.println("Problems reading JSON from " + url);
            e.printStackTrace();
        }
        return new JSONObject();
    }

}
