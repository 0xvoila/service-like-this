package org.freshworks.core.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import org.freshworks.core.constants.Constants;
import org.freshworks.core.env.Environment;
import org.freshworks.core.infra.Infra;
import org.freshworks.core.Annotations.FreshLookup;
import org.freshworks.core.utils.Utility;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.freshworks.core.constants.Constants.GETTER_METHOD_PREFIX;
import static org.freshworks.core.constants.Constants.JsonTypeInfo_As_PROPERTY;

public class Processor {

    HashMap<String, String> redis = new HashMap<>();
    Multimap<String, String> serviceAssetTable;

    public Processor(Multimap<String, String> serviceAssetTable){
        this.serviceAssetTable = serviceAssetTable;
    }
    public void process() {

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            while(true){
                if(Environment.getValueByKey(Constants.SYNC_STATUS_KEY).equals(Constants.SYNC_STATUS.TRAVERSE_SUCCESS)){
                    if ( Infra.kafka.isEmpty()){
                        break;
                    }
                }
                String s = Infra.kafka.take();
                checkNotNull(s, "Input object can not be null. It must be not null");

                JsonNode jNode = objectMapper.readTree(s);
                String mainStepPathAsString = jNode.get(Constants.BASE_BEAN).get(JsonTypeInfo_As_PROPERTY).asText();
                String mainStepObjectAsString = jNode.get(Constants.BASE_BEAN).toString();
                ArrayList<String> unwrappedStepsOfMainStep =  unwrapMainStep(mainStepObjectAsString);
                for(String  asset: serviceAssetTable.keys()) {

                    List<String> assetStepDependencyList = getAssetStepDependencyList(asset);
                    checkArgument(assetStepDependencyList.size() > 0, "A asset must be dependent on atleast one bean");

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
                        checkNotNull(freshLookup, "When a asset depends on multiple items at child node then join condition must be provided with Freshlookup annotation");

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
                            checkNotNull(fieldValue, "lookup field value can not be null");

                            redis.put(mainStepPathAsString + "_" + fieldValue,mainStepObjectAsString);
                        }
                        else{
                            Method getterMethod = lookupStepClass.getDeclaredMethod(GETTER_METHOD_PREFIX + getLookupField(lookupStepClass, freshLookup).substring(0, 1).toUpperCase()
                                    + getLookupField(lookupStepClass, freshLookup).substring(1));
                            fieldValue = getterMethod.invoke(mainStepClassObject);
                            checkNotNull(fieldValue, "lookup field value can not be null");

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

            Environment.setKeyValue(Constants.SYNC_STATUS_KEY, Constants.SYNC_STATUS.PROCESS_SUCCESS);
        }
        catch(Exception e){
            Environment.setKeyValue(Constants.SYNC_STATUS_KEY, Constants.SYNC_STATUS.PROCESS_FAILED);
        }

        Environment.setKeyValue(Constants.SYNC_STATUS_KEY, Constants.SYNC_STATUS.TOTAL_SUCCESS);
    }


    public String getLookupField(Class<?> lookupClass, FreshLookup freshLookup){

        String fieldName;
        String className = lookupClass.getName();
        if ( freshLookup.leftClass().getName().equals(className)){
            fieldName = freshLookup.leftClassField();
        }
        else if(freshLookup.rightClass().getName().equals(className)){
            fieldName = freshLookup.rightClassField();
        }
        else{
            fieldName =  null;
        }

        checkNotNull(fieldName, "lookup field name can not be determined");
        return fieldName;
    }

    public String getLookupClassName(Class<?> masterClass, FreshLookup freshLookup){

        // Here lookup class name could be same as that of master class name.
        // Here lookup class name could be different masterclass but lookup class would be the nested class of the master class
        String className;
        if ( freshLookup.leftClass().getName().startsWith(masterClass.getName())){
            className = freshLookup.leftClass().getName();
        }
        else if(freshLookup.rightClass().getName().startsWith(masterClass.getName())){
            className = freshLookup.rightClass().getName();
        }
        else {
            className = null;
        }

        checkNotNull(className, "lookup class name can not be null");
        return className;
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
