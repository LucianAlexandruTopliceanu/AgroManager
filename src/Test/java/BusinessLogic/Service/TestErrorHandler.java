package BusinessLogic.Service;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Utility semplificata per gestire il logging durante i test.
 * Permette di sopprimere temporaneamente i log per test di scenari negativi.
 *
 * Principio SOLID: Single Responsibility - gestisce solo la soppressione dei log di test
 */
public class TestErrorHandler {

    private static final Logger errorServiceLogger = Logger.getLogger("BusinessLogic.Service.ErrorService");

    /**
     * Esegue un test con log di errore soppressi.
     * Utile per test di scenari negativi dove gli errori sono attesi.
     *
     * @param testCode il codice del test da eseguire
     */
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

    /**
     * Esegue un test che prevede un'eccezione specifica.
     * Sopprime i log e verifica che l'eccezione corretta sia lanciata.
     *
     * @param expectedType il tipo di eccezione attesa
     * @param testCode il codice del test da eseguire
     * @return l'eccezione catturata del tipo atteso
     * @throws AssertionError se l'eccezione non viene lanciata o è del tipo sbagliato
     */
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
