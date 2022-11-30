package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import config_items.BaseConfigItem;
import config_items.Software;
import org.checkerframework.checker.units.qual.A;
import org.example.connectors.BaseConnector;
import org.example.connectors.okta.Application;
import org.example.connectors.okta.ServicePrincipal;
import org.example.connectors.okta.User;
import org.reflections.Reflections;
import static org.reflections.ReflectionUtils.*;
import static org.reflections.scanners.Scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{

    class Node{
         Multimap<Object, Object> DAG = ArrayListMultimap.create();
         Object rootNode = null;

        public Multimap<Object, Object> getDAG() {
            return DAG;
        }

        public void setDAG(Multimap<Object, Object> DAG) {
            this.DAG = DAG;
        }

        public Object getRootNode() {
            return rootNode;
        }

        public void setRootNode(Object rootNode) {
            this.rootNode = rootNode;
        }
    }

    Table<String, Class<?>, Class<?>> connectorConfigItemTable = HashBasedTable.create();
    HashMap<String, Node> dagMap = new HashMap<>();



    public static void main( String[] args ) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

        App app = new App();
        app.scanner();
        app.consume();
    }

    public void consume() throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {

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
            DiscoveryObject discoveryObject = objectMapper.readValue(s, DiscoveryObject.class);
            BaseConnector connector = discoveryObject.getConnectorClass();
            System.out.println(connector.getClass().getName());
            Class<?> t = connectorConfigItemTable.get(discoveryObject.getConnectorName(), connector.getClass());

            if ( t != null){
                System.out.println(t.getName());
                List<Method> setterMethods = getAllSetters(t);

                Object o = t.newInstance();

                for (Method method: setterMethods) {
                    Class<?> [] classParameterList = method.getParameterTypes();
                    method.invoke(o,classList.get(classParameterList[0].getName()));
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
        else {
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
            connectorConfigItemTable.put(connector.entrySet().iterator().next().getKey(), findDeepestLevel(getAllSetters(softwareClass), connector.entrySet().iterator().next().getKey()) , softwareClass);
        }

        System.out.println(connectorConfigItemTable.get("org.example.connectors.okta", org.example.connectors.okta.User.class));
        System.out.println(connectorConfigItemTable.get("org.example.connectors.onelogin", org.example.connectors.onelogin.ServicePrincipal.class));

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

    public Class<?> findDeepestLevel(List<Method> setterMethodList, String connector){
        Class<?> deep = null;
        ArrayList<Class<?>> arrayList = new ArrayList<>();

        for(Method setter: setterMethodList){
            ArrayList<Class<?>> y = new ArrayList(Arrays.asList(setter.getParameterTypes()));
            arrayList.addAll(y);
        }
        HashSet<Class<?>> u =  new HashSet<>(arrayList);

        Iterator<Class<?>> it  = u.iterator();

        while(it.hasNext()){
            Class<?> x = it.next();
            deep = deeperNode(dagMap.get(connector), x, deep);
        }
        return deep;
    }

    public Class<?> deeperNode(Node node, Class<?>x , Class<?> deep){

        Object o = node.rootNode;
        Multimap<Object, Object> DAG = node.DAG;

        int xHeight = 0;
        Boolean found = false;

        while(true){
            if ( o != x){
                xHeight = xHeight + 1;
            }
            else{
                found = true;
                break;
            }

            if(DAG.containsKey(o)){
                o = DAG.get(o).iterator().next();
            }
            else{
                break;
            }        }

        if ( !found){
            xHeight = -1;
        }

        found = false;
        int deepHeight = 0;
        o = node.rootNode;
        while(true){
            if ( o != deep){
                deepHeight = deepHeight + 1;
            }
            else{
                found = true;
                break;
            }

            if(DAG.containsKey(o)){
                o = DAG.get(o).iterator().next();
            }
            else{
                break;
            }

        }

        if ( !found){
            deepHeight = -100;
        }

        if ( xHeight > deepHeight){
            return x;
        }
        else{
            return deep;
        }
    }

    public Node createDAG(Reflections reflections){

        Multimap<Object, Object> DAG = ArrayListMultimap.create();
        Object rootNode = null;

        Set<Class<?>> steps =
                reflections.get(SubTypes.of(BaseConnector.class).asClass());
        for (Class<?> step: steps) {
            Set<Constructor> constructorSet = getConstructorSetByClass(step);
            Class<?> parentClass = getParentClass(constructorSet);
            if(parentClass != null){
                System.out.println( step.getName() + " ----> " + parentClass.getName());
                DAG.put(parentClass, step);
                System.out.println(DAG);
            }
            else{
                rootNode = step;
            }

        }

        Node node = new Node();
        node.setDAG(DAG);
        node.setRootNode(rootNode);

        return node;

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
        user.setId(faker.number().randomDigit());

        DiscoveryObject discoveryObject = new DiscoveryObject(User.class.getPackage().getName(), user);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }
}