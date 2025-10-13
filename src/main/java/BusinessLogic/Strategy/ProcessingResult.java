package BusinessLogic.Strategy;


public class ProcessingResult<T> {
    private final T value;

    public ProcessingResult(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
