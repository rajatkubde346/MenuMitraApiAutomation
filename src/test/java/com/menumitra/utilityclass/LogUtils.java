package com.menumitra.utilityclass;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class LogUtils {

    // Log4j Logger instance - using the root logger for the package
	private static final String LOG_FILE_PATH = "src/test/resources/log/log4j.log";
    private static final String LOG4J_CONFIG_PATH = "log4j.xml";

    // Static initializer block to configure Log4j once
    static {
        try {
            DOMConfigurator.configure(LOG4J_CONFIG_PATH);
            Logger.getLogger(LogUtils.class).info("Log4j configured from static initializer");
        } catch (Exception e) {
            System.err.println("Error configuring Log4j in static initializer: " + e.getMessage());
        }
    }

    /**
     * Returns a logger instance for the given class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz);
    }

    /**
     * Initializes Log4j configuration and appends blank lines for session separation.
     */
    public static void initializeLogger() {
        try {
            addBlankLinesToLogFile(LOG_FILE_PATH, 3);
            Logger logger = getLogger(LogUtils.class);
            logger.info("=== Logger initialized with session markers ===");
        } catch (Exception e) {
            System.err.println("Error initializing Log4j: " + e.getMessage());
        }
    }

    private static void addBlankLinesToLogFile(String logFilePath, int lineCount) throws customException, IOException {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            for (int i = 0; i < lineCount; i++) {
                writer.write(System.lineSeparator());
            }
            writer.write("===== MenuMitra Execution Start (" + new Date() + ") =====" + System.lineSeparator());
        } catch (IOException e) {
            throw new customException("Error writing session marker to log file: " + e.getMessage());
        }
    }

    // Logging wrappers using caller class for flexibility
    public static void info(String message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        logger.info(message);
    }

    public static void warn(String message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        logger.warn(message);
    }

    public static void error(String message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        logger.error(message);
    }

    public static void fatal(String message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        logger.fatal(message);
    }

    public static void debug(String message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        logger.debug(message);
    }

    public static void startTest(String testName) {
        Logger logger = Logger.getLogger(getCallerClassName());
        logger.info("========== " + testName + " START ==========");
    }

    public static void endTest(String testName) {
        Logger logger = Logger.getLogger(getCallerClassName());
        logger.info("========== " + testName + " END ==========");
    }

    // Structured log methods for success, failure, exception — externally called with logger
    public static void success(Logger logger, String message) {
        logger.info("[SUCCESS] " + message);
    }

    public static void failure(Logger logger, String message) {
        logger.error("[FAILURE] " + message);
    }

    public static void exception(Logger logger, String message, Exception e) {
        logger.error("[EXCEPTION] " + message + ": " + e.getMessage(), e);
    }

    /**
     * Gets the class name of the method that called the logging method.
     */
    private static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 4) {
            return stackTrace[3].getClassName();
        }
        return LogUtils.class.getName();
    }

}
