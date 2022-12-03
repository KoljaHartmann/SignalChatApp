package SignalController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.*;

public class FileLogger {

    private static final Logger logger = Logger.getLogger(FileLogger.class
            .getName());

    private static void initLogger() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        Date time = new GregorianCalendar().getTime();
        try {
            FileHandler fileHandler = new FileHandler(System.getProperty("user.dir")
                    + "/Log/" + dateFormat.format(time) + ".log");
            fileHandler.setFormatter(new FileFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logInfo(String message) {
        if (logger.getHandlers().length < 1) {
            initLogger();
        }
        logger.info(message);
    }

    public static void logWarning(String message) {
        if (logger.getHandlers().length < 1) {
            initLogger();
        }
        logger.warning(message);
    }

    public static void logError(String message) {
        if (logger.getHandlers().length < 1) {
            initLogger();
        }
        logger.severe(message);
    }

    public static void logError(String message, Exception e) {
        if (logger.getHandlers().length < 1) {
            initLogger();
        }
        logger.log(Level.SEVERE, message, e);
    }

    public static void logError(Exception e) {
        if (logger.getHandlers().length < 1) {
            initLogger();
        }
        logger.log(Level.SEVERE, e.getMessage(), e);
    }

    private static class FileFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            SimpleDateFormat logTime = new SimpleDateFormat("HH:mm:ss");
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(record.getMillis());
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
                        + logTime.format(calendar.getTime())
                        + " || "
                        + record.getMessage() + "\n";
            } else {
                StringWriter sw = new StringWriter();
                thrown.printStackTrace(new PrintWriter(sw));
                return record.getLevel()
                        + whiteSpace
                        + logTime.format(calendar.getTime())
                        + " || "
                        + record.getMessage() + "\n"
                        + "        "
                        + sw;
            }
        }
    }

}
