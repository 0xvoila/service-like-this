package org.freshworks.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.checkerframework.checker.units.qual.A;
import org.freshworks.Faker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.freshworks.Constants.GETTER_METHOD_PREFIX;
import static org.freshworks.Constants.JsonTypeInfo_As_PROPERTY;

/**
 * Hello world!
 *
 */
public class App 
{

    HashMap<String, String> redis = new HashMap<>();
    Multimap<String, String> connectorConfigItemTable;



    public static void main( String[] args ) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {

        ArrayList<HashMap<String, String>> connectorConfig = new ArrayList<>();
        HashMap<String, String> x = new HashMap<>();
        x.put("org.freshworks.connectors.okta", "org.freshworks.configitems.okta");
        connectorConfig.add(x);
//        x = new HashMap<>();
//        x.put("org.freshworks.connectors.onelogin", "org.freshworks.configitems.onelogin");
//        connectorConfig.add(x);

        App app = new App();
        ScanConnector scanConnector = new ScanConnector();
        HashMap<String, TreeNode<String>> dagMap = scanConnector.scanner(connectorConfig);
        ScanConfigItem scanConfigItem = new ScanConfigItem();
        app.connectorConfigItemTable = scanConfigItem.scanner(connectorConfig,dagMap);

        app.consume();
    }

    public void consume() throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException {

        ObjectMapper objectMapper = new ObjectMapper();

        while(true){
            String s = getFromKafka();
            JsonNode jNode = objectMapper.readTree(s);
            String mainStepPathAsString = jNode.get("connectorClass").get(JsonTypeInfo_As_PROPERTY).asText();
            String mainStepObjectAsString = jNode.get("connectorClass").toString();
            ArrayList<String> unwrappedStepsOfMainStep =  unwrapMainStep(mainStepObjectAsString);
            for(String  configItem: connectorConfigItemTable.keys()) {

                List<String> configItemStepDependencyList = getConfigItemStepDependencyList(configItem);

                if (isConfigItemDependOnSingleStep(configItemStepDependencyList) && isConfigItemDependOnThisStep(configItemStepDependencyList, mainStepPathAsString)){
                    Class<?> configItemClass =  Class.forName(configItem);
                    List<Method> setterMethods = Utility.getAllSetters(configItemClass);
                    Object configItemClassObject = configItemClass.newInstance();
                    HashMap<String, Object> unwrappedStepClassMap = unwrappedMainStepToClassMap(unwrappedStepsOfMainStep);
                    for (Method method: setterMethods) {
                        Class<?> [] configItemMethodParameterList = method.getParameterTypes();
                        method.invoke(configItemClassObject,unwrappedStepClassMap.get(configItemMethodParameterList[0].getName()));
                    }
                    System.out.println(objectMapper.writeValueAsString(configItemClassObject));
                }
                else if (!isConfigItemDependOnSingleStep(configItemStepDependencyList) && isConfigItemDependOnThisStep(configItemStepDependencyList,mainStepPathAsString)){
//                    Check if dependency List objects are present in the redis or not

                    Object mainStepClassObject = objectMapper.readValue(mainStepObjectAsString, Class.forName(mainStepPathAsString));
                    Class<?> mainStepClass = Class.forName(mainStepPathAsString);
                    Method getterMethod = mainStepClass.getDeclaredMethod(GETTER_METHOD_PREFIX + "id".substring(0, 1).toUpperCase()
                            + "id".substring(1));
                    Object fieldValue = getterMethod.invoke(mainStepClassObject);
                    redis.put(mainStepPathAsString + "_" + fieldValue,mainStepObjectAsString);

//                  Now check if it exists in
                    Boolean found = false;
                    ArrayList<String> configItemStepDependencyObjectListAsString = new ArrayList<>();
                    for(String f : configItemStepDependencyList){
                        if(redis.get(f + "_" + fieldValue) == null){
                            found = false;
                            break;
                        }
                        else{
                            found = true;
                            configItemStepDependencyObjectListAsString.add(redis.get(f + "_" + fieldValue));
                        }
                    }

                    if(found){
                        Class<?> configItemClass =  Class.forName(configItem);
                        List<Method> setterMethods = Utility.getAllSetters(configItemClass);
                        Object configItemClassObject = configItemClass.newInstance();
                        unwrappedStepsOfMainStep = new ArrayList<>();
                        for( int i=0; i< configItemStepDependencyObjectListAsString.size(); i++){
                            unwrappedStepsOfMainStep.addAll(unwrapMainStep(configItemStepDependencyObjectListAsString.get(i)));
                        }

                        HashMap<String, Object> unwrappedStepClassMap = unwrappedMainStepToClassMap(unwrappedStepsOfMainStep);
                        for (Method method: setterMethods) {
                            Class<?> [] configItemMethodParameterList = method.getParameterTypes();
                            Object[] object = new Object[configItemMethodParameterList.length];
                            for(int i =0; i< configItemMethodParameterList.length; i++){
                                object[i] = unwrappedStepClassMap.get(configItemMethodParameterList[i].getName());
                            }
                            method.invoke(configItemClassObject,object);
                        }

                        System.out.println(objectMapper.writeValueAsString(configItemClassObject));
                    }
                }
            }
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

    public Boolean isConfigItemDependOnSingleStep(List<String> configItemStepDependencyList){
        if (configItemStepDependencyList.size() == 1){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean isConfigItemDependOnThisStep(List<String> configItemStepDependencyList, String mainStepPathAsString){
        return configItemStepDependencyList.contains(mainStepPathAsString);
    }

    public List<String> getConfigItemStepDependencyList(String configItem){
        return (List<String>) connectorConfigItemTable.get(configItem);
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

    public String getFromKafka() throws JsonProcessingException {

        Random rand = new Random();
        int randomNum = rand.nextInt((100 - 2) + 1) + 2;

        if ( randomNum < 5 ){
            return Faker.generateApplication();
        }
        else if ( randomNum > 5 && randomNum < 10){
            return Faker.generateServicePrincipal();
        }
        else if (randomNum > 10 && randomNum < 50){
            return Faker.generateUsage();
        }
        else{
            return Faker.generateUser();
        }

    }


    public void test(){

        TreeNode<String> application = new ArrayMultiTreeNode<>("Application");
        TreeNode<String> user = new ArrayMultiTreeNode<>("User");
        TreeNode<String> usage = new ArrayMultiTreeNode<>("Usage");

        application.add(user);
        application.add(usage);

        System.out.println(user.root().data());
        System.out.println(user.commonAncestor(usage));


    }

}