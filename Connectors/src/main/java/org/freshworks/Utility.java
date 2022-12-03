package org.freshworks;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static List<Method> getAllSetters(Class<?> c){
        Method[] allMethods = c.getDeclaredMethods();
        List<Method> setters = new ArrayList<Method>();
        for(Method method : allMethods) {
            if(method.getName().startsWith("set")) {
                setters.add(method);
            }
        }

        return setters;
    }
}
