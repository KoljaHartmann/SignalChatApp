package Game;

import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;


@SuppressWarnings("BusyWait")
public class WhatsAppController {

    private static WebDriver driver;

    public synchronized static void connectToWhatsapp() {
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


    private static void findChat(String chatName) {
        waitForElement(By.xpath("//*[contains(text(), '" + chatName + "')]")).click();
    }

    public synchronized static void sendWhatsAppMessage(String message) {
        findChat("e-Spirit & Associates");
        final WebElement parent = waitForElement(By.className("_1hRBM"));
        final WebElement textField = parent.findElement(By.className("_1awRl"));
        textField.sendKeys(message);
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

    public synchronized static String getMarsUrl() {
        findChat("MarsConfig");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) { }
        final List<WebElement> messages = driver.findElements(By.className("_1wlJG"));
        for (WebElement message : Lists.reverse(messages)) {
            final WebElement messageBody = message.findElement(By.className("_1VzZY"));
            final String text = messageBody.findElement(By.tagName("span")).getText();
            if (text.startsWith("url:") || text.startsWith("Url:")) {
                final String[] split = text.substring(4).trim().split("/");
                return "http://" + split[0] + "/api/" + split[1];
            }
        }
        return "";
    }
}