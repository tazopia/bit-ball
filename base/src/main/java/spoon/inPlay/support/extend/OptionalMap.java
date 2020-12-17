package spoon.inPlay.support.extend;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class OptionalMap<V> extends HashMap<String, V> {

    public Optional<V> optional(String... key) {
        return Optional.ofNullable(get(key));
    }

    public Optional<V> optional(String key) {
        return Optional.ofNullable(get(key));
    }

    public V get(String key) {
        return super.get(convertKey(key));
    }

    public V get(String... key) {
        return super.get(convertKey(key));
    }

    @Override
    public V put(String key, V value) {
        return super.put(convertKey(key), value);
    }

    public V put(String key1, String key2, V value) {
        return super.put(convertKey(key1, key2), value);
    }

    public boolean containsKey(String key) {
        return super.containsKey(convertKey(key));
    }

    public boolean containsKey(String... key) {
        return super.containsKey(convertKey(key));
    }

    private String convertKey(String key) {
        return key.trim().toLowerCase();
    }

    private String convertKey(String... keys) {
        return Arrays.stream(keys).map(String::trim).map(String::toLowerCase).collect(Collectors.joining("|"));
    }

}
