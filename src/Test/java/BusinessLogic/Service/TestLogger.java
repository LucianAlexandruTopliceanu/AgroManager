package BusinessLogic.Service;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

/**
 * Logger specializzato per test con output pulito e strutturato
 * Implementa le best practice per logging di test
 */
public class TestLogger {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private final Logger logger;
    private final String testClass;

    public TestLogger(Class<?> testClass) {
        this.testClass = testClass.getSimpleName();
        this.logger = Logger.getLogger(testClass.getName());
        configureLogger();
    }

    private void configureLogger() {
        // Rimuovi handler esistenti per evitare duplicati
        logger.setUseParentHandlers(false);

        // Crea handler personalizzato per test
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new TestFormatter());

        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    /**
     * Log di inizio test suite
     */
    public void startTestSuite(String suiteName) {
        logger.info(String.format("%s┌─ %s %s%s",
            ANSI_CYAN, suiteName, "TEST SUITE", ANSI_RESET));
    }

    /**
     * Log di fine test suite
     */
    public void endTestSuite(String suiteName, int totalTests, int passed, int failed) {
        String status = failed == 0 ? ANSI_GREEN + "✓ PASSED" : ANSI_RED + "✗ FAILED";
        logger.info(String.format("%s└─ %s - %d/%d tests %s%s",
            ANSI_CYAN, suiteName, passed, totalTests, status, ANSI_RESET));
    }

    /**
     * Log di inizio test
     */
    public void startTest(String testName) {
        logger.info(String.format("%s  ├─ %s%s", ANSI_BLUE, testName, ANSI_RESET));
    }

    /**
     * Log di test completato con successo
     */
    public void testPassed(String testName) {
        logger.info(String.format("%s  │  ✓ %s%s", ANSI_GREEN, testName, ANSI_RESET));
    }

    /**
     * Log di test fallito
     */
    public void testFailed(String testName, String error) {
        logger.severe(String.format("%s  │  ✗ %s - %s%s", ANSI_RED, testName, error, ANSI_RESET));
    }

    /**
     * Log di setup/teardown
     */
    public void setup(String message) {
        logger.fine(String.format("%s  │  ⚙ %s%s", ANSI_YELLOW, message, ANSI_RESET));
    }

    /**
     * Log di operazione importante durante test
     */
    public void operation(String operation, Object result) {
        logger.info(String.format("%s  │    → %s: %s%s", ANSI_BLUE, operation, result, ANSI_RESET));
    }

    /**
     * Log di errore atteso (per test negativi)
     */
    public void expectedError(String testName, String expectedErrorType) {
        logger.info(String.format("%s  │    ⚠ Errore atteso (%s)%s", ANSI_YELLOW, expectedErrorType, ANSI_RESET));
    }

    /**
     * Log di assertion importante
     */
    public void assertion(String description, boolean passed) {
        String status = passed ? ANSI_GREEN + "✓" : ANSI_RED + "✗";
        logger.info(String.format("%s  │    %s %s%s", ANSI_BLUE, status, description, ANSI_RESET));
    }

    /**
     * Formatter personalizzato per output pulito
     */
    private static class TestFormatter extends SimpleFormatter {
        @Override
        public String format(java.util.logging.LogRecord record) {
            return record.getMessage() + System.lineSeparator();
        }
    }
}
