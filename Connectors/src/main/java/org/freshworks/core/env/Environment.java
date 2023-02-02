package org.freshworks.core.env;

import java.util.HashMap;

public class Environment {

    private Environment(){

    }
    private static final HashMap<String, Object> env = new HashMap<>();

    public static void setKeyValue(String key, Object value){
        env.put(key, value);
    }

    public static Object getValueByKey(String key){
        return env.get(key);
    }
}
