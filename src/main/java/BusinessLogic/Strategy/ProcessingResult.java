package BusinessLogic.Strategy;

import java.util.Map;
import java.util.HashMap;

public record ProcessingResult<T>(T data, Map<String, Object> metadata) {
    public ProcessingResult(T data) {
        this(data, new HashMap<>());
    }

    public ProcessingResult(T data, Map<String, Object> metadata) {
        this.data = data;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    public T getValue() {
        return data;
    }
}
