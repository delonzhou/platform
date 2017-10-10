package com.common.pay.common;

import java.util.Map;

public class MapUtil {

    public static <K, V> void putIfNotNull(Map<K, V> map, K key, V value) {
        if (value != null) {
            map.put(key, value);
        }
    }

}
