package Game;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@SuppressWarnings("BusyWait")
public class Controller {

    private static WebDriver driver;

    public static void connectToWhatsapp() {
        System.out.println("trying to connect to WhatsApp");
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
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

    public static void close() {
        driver.close();
    }

    public static void sendWhatsAppMessage(String message) {
        waitForElement(By.xpath("//*[contains(text(), 'e-Spirit & Associates')]")).click();
        final WebElement parent = waitForElement(By.className("_1hRBM"));
        final WebElement textField = parent.findElement(By.className("_1awRl"));
        textField.sendKeys(message);
        waitForElement(By.className("_2Ujuu")).click();
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readMarsJson(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static void sendTestMessage() {
        System.out.println("Sending test message");
        final WebElement chat = waitForElement(By.xpath("//*[contains(text(), 'Laura <3')]"));


        System.out.println("found chat");
        try {
            System.out.println("enabled: " + chat.isEnabled());
            System.out.println("displayed:" + chat.isDisplayed());
            chat.click();
        } catch (Exception e) {
            System.out.println("something went wrong!");
            e.printStackTrace();
        }
        System.out.println("clicked chat");

        final WebElement parent = waitForElement(By.className("_1hRBM"));
        final WebElement textField = parent.findElement(By.className("_1awRl"));
        textField.sendKeys("<3");
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
                if (counter > 50) {
                    System.out.println("I give up!");
                    throw e;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
            }
            counter++;
        }
    }
}