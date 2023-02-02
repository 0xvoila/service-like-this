package org.freshworks.core.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import org.freshworks.core.constants.Constants;
import org.freshworks.core.infra.Infra;
import org.freshworks.core.Annotations.FreshLookup;
import org.freshworks.core.utils.Utility;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.freshworks.core.constants.Constants.GETTER_METHOD_PREFIX;
import static org.freshworks.core.constants.Constants.JsonTypeInfo_As_PROPERTY;

public class Processor {

    HashMap<String, String> redis = new HashMap<>();
    Multimap<String, String> serviceAssetTable;

    public Processor(Multimap<String, String> serviceAssetTable){
        this.serviceAssetTable = serviceAssetTable;
    }
    public void process() throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException, InterruptedException {


        ObjectMapper objectMapper = new ObjectMapper();

        while(true){
            String s = Infra.kafka.take();
            JsonNode jNode = objectMapper.readTree(s);
            String mainStepPathAsString = jNode.get(Constants.BASE_BEAN).get(JsonTypeInfo_As_PROPERTY).asText();
            String mainStepObjectAsString = jNode.get(Constants.BASE_BEAN).toString();
            ArrayList<String> unwrappedStepsOfMainStep =  unwrapMainStep(mainStepObjectAsString);
            for(String  asset: serviceAssetTable.keys()) {

                List<String> assetStepDependencyList = getAssetStepDependencyList(asset);

                if (isAssetDependOnSingleStep(assetStepDependencyList) && isAssetDependOnThisStep(assetStepDependencyList, mainStepPathAsString)){
                    Class<?> assetClass =  Class.forName(asset);
                    List<Method> setterMethods = Utility.getAllSetters(assetClass);
                    Object assetClassObject = assetClass.newInstance();
                    HashMap<String, Object> unwrappedStepClassMap = unwrappedMainStepToClassMap(unwrappedStepsOfMainStep);
                    for (Method method: setterMethods) {
                        Class<?> [] assetMethodParameterList = method.getParameterTypes();
                        method.invoke(assetClassObject,unwrappedStepClassMap.get(assetMethodParameterList[0].getName()));
                    }
                    System.out.println(objectMapper.writeValueAsString(assetClassObject));
                }
                else if (!isAssetDependOnSingleStep(assetStepDependencyList) && isAssetDependOnThisStep(assetStepDependencyList,mainStepPathAsString)){
//                  Check if dependency List objects are present in the redis or not
                    Class<?> assetClass =  Class.forName(asset);
                    FreshLookup freshLookup = assetClass.getAnnotation(FreshLookup.class);
                    Object mainStepClassObject = objectMapper.readValue(mainStepObjectAsString, Class.forName(mainStepPathAsString));
                    Class<?> mainStepClass = Class.forName(mainStepPathAsString);
                    Class<?> lookupStepClass = Class.forName(getLookupClassName(mainStepClass, freshLookup));
                    Class<?>[] nestedClassList = mainStepClass.getDeclaredClasses();

                    Object fieldValue = null;
                    if(nestedClassList.length > 0 && !lookupStepClass.getName().equals(mainStepClass.getName())){

                        // It means that lookup step class is nested class of the main step class
                        // Here extract the object of lookupStepClass from mainStepClassObject
                        String[] lookupClassNameSplit = lookupStepClass.getName().split("\\$");
                        String nestedClassNameAsString = lookupClassNameSplit[lookupClassNameSplit.length - 1];
                        Method getterMethod = mainStepClass.getDeclaredMethod(GETTER_METHOD_PREFIX + nestedClassNameAsString.substring(0, 1).toUpperCase()
                                + nestedClassNameAsString.substring(1));
                        Object nestedClassObject = getterMethod.invoke(mainStepClassObject);

                        getterMethod = lookupStepClass.getDeclaredMethod(GETTER_METHOD_PREFIX + getLookupField(lookupStepClass, freshLookup).substring(0, 1).toUpperCase()
                                + getLookupField(lookupStepClass, freshLookup).substring(1));

                        fieldValue = getterMethod.invoke(nestedClassObject);
                        redis.put(mainStepPathAsString + "_" + fieldValue,mainStepObjectAsString);
                    }
                    else{
                        Method getterMethod = lookupStepClass.getDeclaredMethod(GETTER_METHOD_PREFIX + getLookupField(lookupStepClass, freshLookup).substring(0, 1).toUpperCase()
                                + getLookupField(lookupStepClass, freshLookup).substring(1));
                        fieldValue = getterMethod.invoke(mainStepClassObject);
                        redis.put(mainStepPathAsString + "_" + fieldValue,mainStepObjectAsString);
                    }

//                  Now check if it exists in
                    Boolean found = false;
                    ArrayList<String> assetStepDependencyObjectListAsString = new ArrayList<>();
                    for(String f : assetStepDependencyList){
                        if(redis.get(f + "_" + fieldValue) == null){
                            found = false;
                            break;
                        }
                        else{
                            found = true;
                            assetStepDependencyObjectListAsString.add(redis.get(f + "_" + fieldValue));
                        }
                    }

                    if(Boolean.TRUE.equals(found)){
                        List<Method> setterMethods = Utility.getAllSetters(assetClass);
                        Object assetClassObject = assetClass.newInstance();
                        unwrappedStepsOfMainStep = new ArrayList<>();
                        for( int i=0; i< assetStepDependencyObjectListAsString.size(); i++){
                            unwrappedStepsOfMainStep.addAll(unwrapMainStep(assetStepDependencyObjectListAsString.get(i)));
                        }

                        HashMap<String, Object> unwrappedStepClassMap = unwrappedMainStepToClassMap(unwrappedStepsOfMainStep);
                        for (Method method: setterMethods) {
                            Class<?> [] assetMethodParameterList = method.getParameterTypes();
                            Object[] object = new Object[assetMethodParameterList.length];
                            for(int i =0; i< assetMethodParameterList.length; i++){
                                object[i] = unwrappedStepClassMap.get(assetMethodParameterList[i].getName());
                            }
                            method.invoke(assetClassObject,object);
                        }

                        System.out.println(objectMapper.writeValueAsString(assetClassObject));
                    }
                }
            }
        }
    }


    public String getLookupField(Class<?> lookupClass, FreshLookup freshLookup){

        String className = lookupClass.getName();
        if ( freshLookup.leftClass().getName().equals(className)){
            return freshLookup.leftClassField();
        }
        else if(freshLookup.rightClass().getName().equals(className)){
            return freshLookup.rightClassField();
        }
        else{
            return null;
        }
    }

    public String getLookupClassName(Class<?> masterClass, FreshLookup freshLookup){

        // Here lookup class name could be same as that of master class name.
        // Here lookup class name could be different masterclass but lookup class would be the nested class of the master class

        if ( freshLookup.leftClass().getName().startsWith(masterClass.getName())){
            return freshLookup.leftClass().getName();
        }
        else if(freshLookup.rightClass().getName().startsWith(masterClass.getName())){
            return freshLookup.rightClass().getName();
        }
        else {
            return null;
        }
    }

    public ArrayList<String> unwrapMainStep(String mainStepObjectAsString) throws IOException {
        ArrayList<String> unwrappedMainStepAsString = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.readTree(mainStepObjectAsString);
        ArrayList<JsonNode> unWrapMainStepByJsonNode = unWrapMainStepByJsonNode(jNode, new ArrayList<JsonNode>());

//        Here adding the main jNode
        unWrapMainStepByJsonNode.add(jNode);

        for (JsonNode j: unWrapMainStepByJsonNode) {
            unwrappedMainStepAsString.add(j.toString());
        }

        return unwrappedMainStepAsString;
    }

    private ArrayList<JsonNode> unWrapMainStepByJsonNode(JsonNode jNode, ArrayList<JsonNode> x){

        Iterator<Map.Entry<String,JsonNode>> it = jNode.fields();
        while(it.hasNext()){
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getValue().isObject() && entry.getValue().has(JsonTypeInfo_As_PROPERTY)){
                x.add(entry.getValue());
                unWrapMainStepByJsonNode(entry.getValue(), x);
            }
        }
        return x;
    }

    public Boolean isAssetDependOnSingleStep(List<String> assetStepDependencyList){
        if (assetStepDependencyList.size() == 1){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean isAssetDependOnThisStep(List<String> assetStepDependencyList, String mainStepPathAsString){
        return assetStepDependencyList.contains(mainStepPathAsString);
    }

    public List<String> getAssetStepDependencyList(String asset){
        return (List<String>) serviceAssetTable.get(asset);

    }

    public HashMap<String, Object> unwrappedMainStepToClassMap(ArrayList<String> unwrappedStepsOfMainStep) throws ClassNotFoundException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> unwrappedStepClassMap = new HashMap<>();
        for (String eachStep: unwrappedStepsOfMainStep) {
            Object o = objectMapper.readValue(eachStep, Class.forName(objectMapper.readTree(eachStep).get(JsonTypeInfo_As_PROPERTY).asText()));
            unwrappedStepClassMap.put(o.getClass().getName(), o);
        }
        return unwrappedStepClassMap;
    }

}
