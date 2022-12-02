package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import config_items.BaseConfigItem;
import org.example.connectors.BaseConnector;
import org.example.connectors.okta.Application;
import org.example.connectors.okta.ServicePrincipal;
import org.example.connectors.okta.Usage;
import org.example.connectors.okta.User;
import org.reflections.Reflections;
import static org.reflections.ReflectionUtils.*;
import static org.reflections.scanners.Scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{

    Multimap<String, String> connectorConfigItemTable = ArrayListMultimap.create();
    HashMap<String, TreeNode<String>> dagMap = new HashMap<>();
    HashMap<String, String> redis = new HashMap<>();



    public static void main( String[] args ) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {

        App app = new App();

        app.test();
        app.scanner();
        app.consume();
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
                    List<Method> setterMethods = getAllSetters(t);
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
            return generateApplication();
        }
        else if ( randomNum > 30 && randomNum < 70){
            return generateServicePrincipal();
        }
        else if (randomNum > 70 && randomNum < 90){
            return generateUsage();
        }
        else{
            return generateUser();
        }

    }
    public void scanner(){

        ArrayList<HashMap<String, String>> connectors = new ArrayList<>();
        HashMap<String, String> x = new HashMap<>();
        x.put("org.example.connectors.okta", "org.example.configitems.okta");
        connectors.add(x);
        x = new HashMap<>();
        x.put("org.example.connectors.onelogin", "org.example.configitems.onelogin");
        connectors.add(x);

        for (HashMap<String, String> connector : connectors) {

//            Creation of the DAG for the given connector
            Reflections reflections = new Reflections(new ConfigurationBuilder().
                    setUrls(ClasspathHelper.forPackage(connector.entrySet().iterator().next().getKey())).
                    filterInputsBy(new FilterBuilder().includePackage(connector.entrySet().iterator().next().getKey()))
            );

            dagMap.put(connector.entrySet().iterator().next().getKey(), createDAG(reflections));

//            Creation of the config item table for the given config item and connector.
//            As of now we have just Software class
            reflections = new Reflections( new ConfigurationBuilder().
                    setUrls(ClasspathHelper.forPackage(connector.entrySet().iterator().next().getValue())).
                    filterInputsBy(new FilterBuilder().includePackage(connector.entrySet().iterator().next().getValue()))
            );

            Set<Class<?>> steps = reflections.get(SubTypes.of(BaseConfigItem.class).
                    filter(withNamePrefix(connector.entrySet().iterator().next().getValue()))
                    .asClass());

            Class<?> softwareClass = steps.iterator().next();
            ArrayList<String> dependentClassList = findDependencyOfConfigItem(getAllSetters(softwareClass), connector.entrySet().iterator().next().getKey());
            for (String dependent:
                 dependentClassList) {
                connectorConfigItemTable.put(softwareClass.getName(), dependent);
            }
        }

    }

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

    public ArrayList<String> findDependencyOfConfigItem(List<Method> setterMethodList, String connector){

        TreeNode<String> DAG = dagMap.get(connector);

        ArrayList<String> dependents = new ArrayList<>();

        ArrayList<Class<?>> arrayList = new ArrayList<>();

        for(Method setter: setterMethodList){
            ArrayList<Class<?>> y = new ArrayList(Arrays.asList(setter.getParameterTypes()));
            arrayList.addAll(y);
        }
        HashSet<Class<?>> u =  new HashSet<>(arrayList);

        Iterator<Class<?>> it  = u.iterator();

        while(it.hasNext()){
            String x = it.next().getName();
            TreeNode<String> n = DAG.find(x);
            if (n != null && n.isLeaf()){

                dependents.add(x);
            }
        }
        return dependents;
    }

    public TreeNode<String> createDAG(Reflections reflections){
        TreeNode<String> root = new ArrayMultiTreeNode<>(null);
        HashMap<String, TreeNode<String>> branchMap = new HashMap<>();

        Set<Class<?>> steps =
                reflections.get(SubTypes.of(BaseConnector.class).asClass());
        for (Class<?> step: steps) {
            Set<Constructor> constructorSet = getConstructorSetByClass(step);
            Class<?> parentClass = getParentClass(constructorSet);
            if(parentClass != null){
                if ( branchMap.containsKey(parentClass.getName())){
                    branchMap.get(parentClass.getName()).add(new ArrayMultiTreeNode<>(step.getName()));
                }
                else{
                    TreeNode<String>  parent = new ArrayMultiTreeNode<>(parentClass.getName());
                    parent.add(new ArrayMultiTreeNode<>(step.getName()));
                    branchMap.put(parent.data(), parent);
                }
            }
            else{
                root.setData(step.getName());
            }

        }

        for (Map.Entry<String, TreeNode<String>> entry : branchMap.entrySet()) {
            if ( root.find(entry.getValue().data()) == null){
                root.add(entry.getValue());
            }
        }

        return root;

    }
    public  Set<Constructor> getConstructorSetByClass(Class<?> c){
        return get(Constructors.of(c));
    }

    public Class<?> getParentClass(Set<Constructor> constructorSet){

        Iterator<Constructor> it = constructorSet.iterator();
        Class<?> c = null;

        while(it.hasNext()){
            Class<?>  [] classList = it.next().getParameterTypes();
            if ( classList.length != 0){
                c = classList[0];
            }
        }
        return c;
    }

    public String generateApplication() throws JsonProcessingException {
        Faker faker = new Faker();
        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);

        app.setAppId(faker.number().randomDigit());

        DiscoveryObject discoveryObject = new DiscoveryObject(Application.class.getPackage().getName(), app);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }

    public String generateServicePrincipal() throws JsonProcessingException {
        Faker faker = new Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);
        app.setAppId(faker.number().randomDigit());

        ServicePrincipal servicePrincipal = new ServicePrincipal(app);
        String principalName = faker.name().fullName();
        servicePrincipal.setServicePrincipalName(principalName);
        servicePrincipal.setId(faker.number().randomDigit());

        DiscoveryObject discoveryObject = new DiscoveryObject(ServicePrincipal.class.getPackage().getName(), servicePrincipal);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);

    }

    public String generateUser() throws JsonProcessingException {
        Faker faker = new Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);
        app.setAppId(faker.number().randomDigit());

        ServicePrincipal servicePrincipal = new ServicePrincipal(app);
        String principalName = faker.name().fullName();
        servicePrincipal.setServicePrincipalName(principalName);
        servicePrincipal.setId(faker.number().randomDigit());

        User user = new User(servicePrincipal);
        String userName = faker.name().fullName();
        user.setUserName(userName);
        int x = faker.number().randomDigit();
        user.setId(x);

        Usage usage = new Usage(servicePrincipal);
        usage.setUserUsage(userName);
        usage.setId(x);

        DiscoveryObject discoveryObject = new DiscoveryObject(User.class.getPackage().getName(), user);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }

    public String generateUsage() throws JsonProcessingException {
        Faker faker = new Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);
        app.setAppId(faker.number().randomDigit());

        ServicePrincipal servicePrincipal = new ServicePrincipal(app);
        String principalName = faker.name().fullName();
        servicePrincipal.setServicePrincipalName(principalName);
        servicePrincipal.setId(faker.number().randomDigit());


        Usage usage = new Usage(servicePrincipal);
        usage.setUserUsage("axnd");
        usage.setId(faker.number().randomDigit());

        DiscoveryObject discoveryObject = new DiscoveryObject(User.class.getPackage().getName(), usage);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }
}