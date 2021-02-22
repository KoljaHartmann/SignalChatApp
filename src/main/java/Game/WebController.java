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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("BusyWait")
public class WebController {

    private static WebDriver driver;

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
                final WebElement divContainingCode = driver.findElement(By.className("_1yHR2"));
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

    @SuppressWarnings("SameParameterValue")
    private static void findChat(String chatName) {
        waitForElement(By.xpath("//*[contains(text(), '" + chatName + "')]")).click();
    }

    public synchronized static void sendWhatsAppMessage(String message) throws InterruptedException {
        findChat("e-Spirit & Associates");
        final WebElement parent = waitForElement(By.className("_1hRBM"));
        final WebElement textField = parent.findElement(By.className("_1awRl"));

        final String[] split = message.split("@[^\\s.!,]*");
        Matcher matcher = Pattern.compile("@(?<culprit>[^\\s.!,]*)").matcher(message);

        textField.sendKeys(split[0]);
        int hits = 0;
        while(matcher.find()) {
            hits++;
            textField.sendKeys("@" + matcher.group("culprit"));
            Thread.sleep(100);
            final WebElement pingList = driver.findElement(By.className("_2wjQC"));
            try {
                final WebElement culprit = pingList.findElement(By.xpath(".//*[contains(text(), '" + matcher.group("culprit") + "')]"));
                culprit.click();
                textField.sendKeys("\b");
            } catch (NoSuchElementException e) {
                System.out.println("Could not find player " + matcher.group("culprit") + " to ping.");
            }
            if (split.length > hits) {
                textField.sendKeys(split[hits]);
            }
        }
        waitForElement(By.className("_2Ujuu")).click();
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
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}