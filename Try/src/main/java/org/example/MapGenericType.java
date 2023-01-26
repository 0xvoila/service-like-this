package org.example.dev;

import java.util.HashMap;

public class MapGenericType <K, V>{

    HashMap<K, V> localHashMap = new HashMap<>();

    public void put(K key, V value){
        localHashMap.put(key, value);
    }

    public V get(K key){
        return localHashMap.get(key);
    }

    public static void main(String args[]){
        MapGenericType<String, String> f = new MapGenericType<String, String>();
        f.put("name","amit");
        System.out.println(f.get("name"));

        MapGenericType<String, Integer> ff = new MapGenericType<String, Integer>();
        ff.put("roll_number", 1010);
        System.out.println(ff.get("roll_number"));

    }
}
