package SignalController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.*;

public class FileLogger {

    private static final Logger logger = Logger.getLogger(FileLogger.class.getName());
    private static DayOfWeek dayOfWeek;

    private static void checkLogger(String message) {
        System.out.println(message);
        if (logger.getHandlers().length < 1 || !LocalDate.now().getDayOfWeek().equals(dayOfWeek)) {
            initLogger();
        }
    }


    private static void initLogger() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        LocalDate now = LocalDate.now();
        dayOfWeek = now.getDayOfWeek();
        try {
            FileHandler fileHandler = new FileHandler(System.getProperty("user.dir")
                    + "/Log/" + dateFormat.format(now) + ".log");
            fileHandler.setFormatter(new FileFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logInfo(String message) {
        checkLogger(message);
        logger.info(message);
    }

    public static void logWarning(String message) {
        checkLogger(message);
        logger.warning(message);
    }

    public static void logError(String message) {
        checkLogger(message);
        logger.severe(message);
    }

    public static void logError(String message, Throwable e) {
        checkLogger(message);
        logger.log(Level.SEVERE, message, e);
        e.printStackTrace();
    }

    public static void logError(Throwable e) {
        checkLogger(e.getMessage());
        logger.log(Level.SEVERE, e.getMessage(), e);
        e.printStackTrace();
    }

    private static class FileFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            SimpleDateFormat logTime = new SimpleDateFormat("dd.MM HH:mm:ss");
            Throwable thrown = record.getThrown();
            String whiteSpace;
            if (record.getLevel().equals(Level.INFO)) {
                whiteSpace = "    ";
            } else if (record.getLevel().equals(Level.SEVERE)) {
                whiteSpace = "  ";
            } else {
                whiteSpace = " ";
            }
            if (thrown == null) {
                return record.getLevel()
                        + whiteSpace
                        + logTime.format(LocalTime.now())
                        + " || "
                        + record.getMessage() + "\n";
            } else {
                StringWriter sw = new StringWriter();
                thrown.printStackTrace(new PrintWriter(sw));
                return record.getLevel()
                        + whiteSpace
                        + logTime.format(LocalTime.now())
                        + " || "
                        + record.getMessage() + "\n"
                        + "        "
                        + sw;
            }
        }
    }

}
