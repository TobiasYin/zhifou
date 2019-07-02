package util;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class EncodeAndDecode {
    private static OneToOneMap<Character, Character> saltMap = getSalt();
    private static Base64.Encoder encoder = Base64.getEncoder();
    private static Base64.Decoder decoder = Base64.getDecoder();

    public static String encode(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Character changed = saltMap.get(c);
            if (changed != null) sb.append(changed);
            else sb.append(c);
        }
        return encoder.encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String s) {
        s = new String(decoder.decode(s), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Character changed = saltMap.getByValue(c);
            if (changed != null) sb.append(changed);
            else sb.append(c);
        }
        return sb.toString();
    }

    private static OneToOneMap<Character, Character> getSalt() {
        ArrayList<Character> objs = new ArrayList<>();
        for (int i = 32; i <= 126; i++)
            objs.add((char) i);
        ArrayList<Character> origin = (ArrayList<Character>) objs.clone();
        shuffle(objs);
        OneToOneMap<Character, Character> map = new OneToOneMap<>();
        for (int i = 0; i < origin.size(); i++) {
            char key = origin.get(i);
            char value = objs.get(i);
            map.put(key, value);
        }
        return map;
    }

    private static <T> void shuffle(ArrayList<T> list) {
        shuffle(list, list.size() * 4);
    }

    private static <T> void shuffle(ArrayList<T> list, int n) {
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            int index_1 = r.nextInt(list.size());
            int index_2 = r.nextInt(list.size());
            T temp = list.get(index_1);
            list.set(index_1, list.get(index_2));
            list.set(index_2, temp);
        }
    }

    public static class OneToOneMap<K, V> implements Map<K, V> {
        private Map<K, V> keyMap = new HashMap<>();
        private Map<V, K> valueMap = new HashMap<>();

        @Override
        public int size() {
            return keyMap.size();
        }

        @Override
        public boolean isEmpty() {
            return keyMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return keyMap.containsKey(key);
        }


        @Override
        public boolean containsValue(Object value) {
            return valueMap.containsKey(value);
        }

        @Override
        public V get(Object key) {
            return keyMap.get(key);
        }

        public K getByValue(V value) {
            return valueMap.get(value);
        }

        @Override
        public V put(K key, V value) {
            if (!containsKey(key) && !containsValue(value)) {
                keyMap.put(key, value);
                valueMap.put(value, key);
            } else if (!containsKey(key) && containsValue(value)) {
                K oldKey = valueMap.get(value);
                keyMap.remove(oldKey);
                keyMap.put(key, value);
                valueMap.put(value, key);
            } else if (containsKey(key) && !containsValue(value)) {
                V oldValue = keyMap.get(key);
                valueMap.remove(oldValue);
                keyMap.put(key, value);
                valueMap.put(value, key);
            } else {
                if (keyMap.get(key).equals(value)) {
                    return value;
                } else {
                    keyMap.remove(key);
                    valueMap.remove(value);
                    keyMap.put(key, value);
                    valueMap.put(value, key);
                }
            }
            return value;
        }

        @Override
        public V remove(Object key) {
            V oldValue = keyMap.get(key);
            valueMap.remove(oldValue);
            keyMap.remove(key);
            return oldValue;
        }

        public K removeByValue(V value) {
            K oldKey = valueMap.get(value);
            keyMap.remove(oldKey);
            valueMap.remove(value);
            return oldKey;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            for (K k : m.keySet()) {
                put(k, m.get(k));
            }
        }

        @Override
        public void clear() {
            valueMap.clear();
            keyMap.clear();
        }

        @Override
        public Set<K> keySet() {
            return keyMap.keySet();
        }

        public Set<V> valueSet() {
            return valueMap.keySet();
        }

        @Override
        public Collection<V> values() {
            return valueSet();
        }

        public Collection<K> keys() {
            return keySet();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return keyMap.entrySet();
        }
    }
}
