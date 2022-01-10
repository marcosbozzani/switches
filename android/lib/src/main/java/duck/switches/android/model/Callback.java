package duck.switches.android.model;

public interface Callback<T> {
    void execute(T value);
}
