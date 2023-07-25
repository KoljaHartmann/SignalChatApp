package SignalController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class FileLogger {

    private static final Logger logger = Logger.getLogger(FileLogger.class.getName());
    private static LocalDate localDate;

    private static void checkLogger(String message) {
        System.out.println(message);
        if (logger.getHandlers().length < 1 || !LocalDate.now().equals(localDate)) {
            initLogger();
        }
    }


    private static void initLogger() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        localDate = now.toLocalDate();
        for (Handler handler : logger.getHandlers()) {
            handler.close();
            logger.removeHandler(handler);
        }
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(System.getProperty("user.dir")
                    + "/Log/" + now.format(formatter) + ".log");
            fileHandler.setFormatter(new FileFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            if (fileHandler != null) {
                fileHandler.close();
            }
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm:ss");
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
                        + LocalDateTime.now().format(formatter)
                        + " || "
                        + record.getMessage() + "\n";
            } else {
                StringWriter sw = new StringWriter();
                thrown.printStackTrace(new PrintWriter(sw));
                return record.getLevel()
                        + whiteSpace
                        + LocalDateTime.now().format(formatter)
                        + " || "
                        + record.getMessage() + "\n"
                        + "        "
                        + sw;
            }
        }
    }

}
