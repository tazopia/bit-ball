package spoon.support.extend;

import java.util.HashMap;

public class DefaultHashMap<K, V> extends HashMap<K, V> {

    private V defaultValue;

    public DefaultHashMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object key) {
        return containsKey(key) ? super.get(key) : defaultValue;
    }
}
