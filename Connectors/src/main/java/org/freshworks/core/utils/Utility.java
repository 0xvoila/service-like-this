package org.freshworks.core.utils;

import org.freshworks.core.constants.Constants;
import org.freshworks.core.env.Environment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static HashMap<String, String> getMetaDataByClass(Class<?> clazz){

        String postmanClassName = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);

        HashMap<String, String> data = new HashMap<>();
        data.put("postman", Constants.STEP_PATH + Environment.getValueByKey("service") + "." + postmanClassName);
        data.put("bean", Constants.BEAN_PATH + Environment.getValueByKey("service") + "." + postmanClassName);

        return data;
    }
}
