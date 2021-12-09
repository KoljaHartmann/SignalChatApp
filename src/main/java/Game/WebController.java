package Game;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebController {

    private static WebDriver driver;

    private static final String CHAT_FIELD_PARENT = "_1LbR4";
    private static final String CHAT_FIELD = "_13NKt";
    private static final String SEND_MESSAGE = "_4sWnG";
    private static final String PING_LIST = "_3lPuS";
    private static final String MESSAGE_CONTAINER = "_1Gy50";
    private static final String QR_CODE_FIELD = "_2UwZ_";

    public synchronized static void connectToWhatsapp(boolean headless) {
        System.out.println("trying to connect to WhatsApp");
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        if (headless) {
            firefoxBinary.addCommandLineOptions("--headless");
        }
        System.setProperty("webdriver.gecko.driver", System.getenv("FIREFOX_PATH"));
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        driver = new FirefoxDriver(firefoxOptions);
        driver.get("https://web.whatsapp.com/");
        System.out.println("Connected to WhatsApp");

        readQRCode();
    }

    private static void readQRCode() {
        String dataRef = "";
        while (true) {
            try {
                final WebElement divContainingCode = driver.findElement(By.className(QR_CODE_FIELD));
                String attribute = divContainingCode.getAttribute("data-ref");
                if (attribute != null && !attribute.equals(dataRef)) {
                    System.out.println(attribute);
                    dataRef = attribute;
                }
                Thread.sleep(100);
            } catch (Exception e) {
                if (!dataRef.isEmpty()) {
                    System.out.println("I'm out!");
                    break;
                }
                e.printStackTrace();
            }
        }
    }

    public synchronized static void sendMessage(String message) throws InterruptedException {
        waitForElement(By.xpath("//*[contains(text(), 'e-Spirit & Associates')]")).click();
        final WebElement parent = waitForElement(By.className(CHAT_FIELD_PARENT));
        final WebElement textField = parent.findElement(By.className(CHAT_FIELD));

        final String[] split = message.split("@[^\\s.!,]*");
        Matcher matcher = Pattern.compile("@(?<culprit>[^\\s.!,]*)").matcher(message);

        textField.sendKeys(split[0]);
        int hits = 0;
        while(matcher.find()) {
            hits++;
            textField.sendKeys("@" + matcher.group("culprit"));
            try {
                Thread.sleep(100);
                final WebElement pingList = driver.findElement(By.className(PING_LIST));
                final WebElement culprit = pingList.findElement(By.xpath(".//*[contains(text(), '" + matcher.group("culprit") + "')]"));
                culprit.click();
            } catch (NoSuchElementException e) {
                System.out.println("Could not find player " + matcher.group("culprit") + " to ping.");
            }
            if (split.length > hits) {
                textField.sendKeys(split[hits]);
            }
        }
        waitForElement(By.className(SEND_MESSAGE)).click();
    }

    private static WebElement waitForElement(By searchQuery) {
        int counter = 0;
        while (true) {
            try {
                final WebElement element = driver.findElement(searchQuery);
                System.out.println("Found " + searchQuery.toString());
                return element;
            } catch (NoSuchElementException e) {
                System.out.println("did not found " + searchQuery.toString() + ". counter is " + counter);
                if (counter > 20) {
                    System.out.println("I give up after " + counter + " tries!");
                    throw e;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
            }
            counter++;
        }
    }

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

    public synchronized static String readConfigUrl() {
        String url = getUrlMessage();
        if (url.startsWith("http")) {
            if (url.contains("/api/")) {
                return url;
            } else {
                final String[] split = url.split("/player");
                if (split.length == 2) {
                    return split[0] + "/api/player" + split[1];
                } else {
                    throw new RuntimeException("Unexpected url! " + url);
                }
            }
        }
        return null;
    }

    private static String getUrlMessage() {
        try {
            driver.findElement(By.xpath("//*[contains(text(), 'MarsConfig')]")).click();
            Thread.sleep(100);
            final List<WebElement> messages = driver.findElements(By.className(MESSAGE_CONTAINER));
            final WebElement latestMessage = messages.get(messages.size() - 1);
            final WebElement urlField = latestMessage.findElement(By.xpath(".//*[contains(text(), 'url')]"));
            final String urlConfig = urlField.getText();
            return urlConfig.replaceFirst("url:*", "");
        } catch (Exception ignored) {
            System.out.println("No new url found");
        }
        return "";
    }
}