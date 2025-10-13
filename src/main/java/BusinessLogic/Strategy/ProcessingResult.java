package BusinessLogic.Strategy;

import java.util.Map;
import java.util.HashMap;

public class ProcessingResult<T> {
    private final T data;
    private final Map<String, Object> metadata;

    public ProcessingResult(T data) {
        this.data = data;
        this.metadata = new HashMap<>();
    }

    public ProcessingResult(T data, Map<String, Object> metadata) {
        this.data = data;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    public T getData() {
        return data;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    // Metodo legacy per compatibilità
    public T getValue() {
        return data;
    }
}
