package BusinessLogic.Strategy;


public class ProcessingResult<T> {
    private final T value;
    private final String formattedOutput;

    public ProcessingResult(T value, String formattedOutput) {
        this.value = value;
        this.formattedOutput = formattedOutput;
    }

    public T getValue() {
        return value;
    }

    public String getFormattedOutput() {
        return formattedOutput;
    }
}
