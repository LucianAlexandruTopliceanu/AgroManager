package BusinessLogic.Service;

import java.util.logging.Logger;
import java.util.logging.Level;


public class TestErrorHandler {

    private static final Logger errorServiceLogger = Logger.getLogger("BusinessLogic.Service.ErrorService");

    public static void runWithSuppressedErrors(Runnable testCode) {
        Level originalLevel = errorServiceLogger.getLevel();

        try {
            // Sopprime i log temporaneamente
            errorServiceLogger.setLevel(Level.OFF);
            testCode.run();
        } finally {
            // Ripristina il livello originale
            errorServiceLogger.setLevel(originalLevel != null ? originalLevel : Level.WARNING);
        }
    }


    public static <T extends Exception> T expectException(Class<T> expectedType, Runnable testCode) {
        Level originalLevel = errorServiceLogger.getLevel();

        try {
            errorServiceLogger.setLevel(Level.OFF);
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
            errorServiceLogger.setLevel(originalLevel != null ? originalLevel : Level.WARNING);
        }
    }
}
