package org.m2sec.common.models;

import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class Parameters<T> extends LinkedHashMap<String, List<T>> {

    public Parameters(Map<String, List<T>> map) {
        super(map);
    }

    public Parameters<T> put(String key, T value) {
        this.put(key, new ArrayList<>(List.of(value)));
        return this;
    }

    public Parameters<T> add(String key, T value) {
        this.add(key, value, true, false);
        return this;
    }

    public Parameters<T> add(String key, T value, boolean allowDuplicate, boolean keyLowerCase) {
        if (keyLowerCase) {
            key = key.toLowerCase();
        }
        List<T> values = super.get(key);
        if (values == null) {
            this.put(key, new ArrayList<>(List.of(value)));
        } else if (allowDuplicate && !values.contains(value)) {
            values.add(value);
        }
        return this;
    }

    public Parameters<T> merge(Parameters<T> p) {
        for (Map.Entry<String, List<T>> entry : p.entrySet()) {
            for (T value : entry.getValue()) {
                this.add(entry.getKey(), value);
            }
        }
        return this;
    }

    @Nullable
    public T getFirst(String key) {
        List<T> values = super.get(key);
        if (values == null) {
            return null;
        } else {
            return values.get(0);
        }
    }

    public Map<String, T> toSimple() {
        HashMap<String, T> retVal = new HashMap<>();
        for (String key : keySet()) {
            retVal.put(key, getFirst(key));
        }
        return retVal;
    }
}
