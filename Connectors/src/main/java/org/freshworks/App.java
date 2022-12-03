package org.freshworks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.freshworks.connectors.okta.Application;
import org.freshworks.connectors.okta.ServicePrincipal;
import org.freshworks.connectors.okta.Usage;
import org.freshworks.connectors.okta.User;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.freshworks.Faker.generateServicePrincipal;

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
        x.put("org.example.connectors.okta", "org.example.configitems.okta");
        connectorConfig.add(x);
        x = new HashMap<>();
        x.put("org.example.connectors.onelogin", "org.example.configitems.onelogin");
        connectorConfig.add(x);

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

            ArrayList<JsonNode> classNode =  convertToClass(jNode, new ArrayList<JsonNode>());
            HashMap<String, Object> classList = new HashMap<String, Object>();

            for (JsonNode node: classNode) {

               Object o = objectMapper.readValue(node.toString(), Class.forName(node.get("type").asText()));
               classList.put(o.getClass().getName(), o);
            }

            for(String  configItem: connectorConfigItemTable.keys()) {
                List<String> dependencyList = (List<String>) connectorConfigItemTable.get(configItem);
                if (dependencyList.size() == 1 && dependencyList.get(0).equals(jNode.get("connectorClass").get("type").asText())){
                    Class<?> t =  Class.forName(configItem);
                    List<Method> setterMethods = Utility.getAllSetters(t);
                    Object o = t.newInstance();
                    for (Method method: setterMethods) {
                        Class<?> [] classParameterList = method.getParameterTypes();
                        method.invoke(o,classList.get(classParameterList[0].getName()));
                    }
                    System.out.println(objectMapper.writeValueAsString(o));
                }
                else if (dependencyList.contains(jNode.get("connectorClass").get("type").asText())){
//                    Check if depedencyList objects are present in the redis or not
                    ArrayList<String> checkInRedis = new ArrayList<>();
                    for(String dep : dependencyList){
                        if ( !dep.equals(jNode.get("connectorClass").get("type").asText())){
                            checkInRedis.add(dep);
                        }
                        Object o = objectMapper.readValue(jNode.get("connectorClass").toString(), Class.forName(jNode.get("connectorClass").get("type").asText()));
                        Class<?> c = Class.forName(jNode.get("connectorClass").get("type").asText());
                        Method getterMethod = c.getDeclaredMethod("get" + "id".substring(0, 1).toUpperCase()
                                + "id".substring(1));
                        Object fieldValue = getterMethod.invoke(o);

//                        Now check if it exists in
                        Boolean found = false;
                        for(String f : checkInRedis){
                            if(redis.get(f + "_" + fieldValue) == null){
                                found = false;
                                redis.put(jNode.get("connectorClass").get("type").asText() + "_" + fieldValue,jNode.get("connectorClass").toString());
                                break;
                            }
                            else{
                                found = true;
                            }
                        }

                        if(found){
//                            Here starts with the field
                        }
                    }
                }
            }
        }
    }

    public ArrayList<JsonNode> convertToClass(JsonNode jNode, ArrayList<JsonNode> x){
        Iterator<JsonNode> it = jNode.elements();

        while(it.hasNext()){

            JsonNode node = it.next();
            if(node.has("type")){
                x.add(node);
                convertToClass(node, x);
            }
        }
        return x;
    }
    public String getFromKafka() throws JsonProcessingException {

        Random rand = new Random();
        int randomNum = rand.nextInt((100 - 2) + 1) + 2;

        if ( randomNum < 30 ){
            return Faker.generateApplication();
        }
        else if ( randomNum > 30 && randomNum < 70){
            return Faker.generateServicePrincipal();
        }
        else if (randomNum > 70 && randomNum < 90){
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