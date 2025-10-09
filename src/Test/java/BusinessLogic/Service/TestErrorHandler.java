package BusinessLogic.Service;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Utility per gestire il logging durante i test, specialmente per errori attesi
 * Permette di silenziare temporaneamente i log per test di scenari negativi
 */
public class TestErrorHandler {

    private static boolean suppressErrorLogs = false;
    private static final Logger errorServiceLogger = Logger.getLogger("BusinessLogic.Service.ErrorService");

    /**
     * Sopprime temporaneamente i log di errore per test di scenari negativi
     */
    public static void suppressErrorLogs() {
        suppressErrorLogs = true;
        setErrorServiceLogLevel(Level.OFF);
    }

    /**
     * Riattiva i log di errore dopo test di scenari negativi
     */
    public static void enableErrorLogs() {
        suppressErrorLogs = false;
        setErrorServiceLogLevel(Level.WARNING);
    }

    /**
     * Esegue un test con errori soppressi
     */
    public static void runWithSuppressedErrors(Runnable testCode) {
        suppressErrorLogs();
        try {
            testCode.run();
        } finally {
            enableErrorLogs();
        }
    }

    /**
     * Esegue un test che prevede un'eccezione specifica
     */
    public static <T extends Exception> T expectException(Class<T> expectedType, Runnable testCode) {
        suppressErrorLogs();
        try {
            testCode.run();
            throw new AssertionError("Expected exception " + expectedType.getSimpleName() + " was not thrown");
        } catch (Exception e) {
            if (expectedType.isInstance(e)) {
                return expectedType.cast(e);
            } else {
                throw new AssertionError("Expected " + expectedType.getSimpleName() +
                                       " but got " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            }
        } finally {
            enableErrorLogs();
        }
    }

    private static void setErrorServiceLogLevel(Level level) {
        errorServiceLogger.setLevel(level);

        // Imposta anche il livello per tutti gli handler
        for (Handler handler : errorServiceLogger.getHandlers()) {
            if (level == Level.OFF) {
                handler.setLevel(Level.OFF);
            } else {
                handler.setLevel(Level.WARNING);
            }
        }
    }

    /**
     * Logger handler che filtra i messaggi durante i test
     */
    public static class TestAwareHandler extends java.util.logging.Handler {
        private final Handler delegateHandler;

        public TestAwareHandler(Handler delegate) {
            this.delegateHandler = delegate;
        }

        @Override
        public void publish(LogRecord record) {
            // Sopprime i log se siamo in modalità test con errori attesi
            if (!suppressErrorLogs || record.getLevel().intValue() > Level.WARNING.intValue()) {
                delegateHandler.publish(record);
            }
        }

        @Override
        public void flush() {
            delegateHandler.flush();
        }

        @Override
        public void close() throws SecurityException {
            delegateHandler.close();
        }
    }
}
