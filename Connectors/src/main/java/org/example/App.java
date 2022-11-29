package org.example;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import config_items.BaseConfigItem;
import config_items.Software;
import org.checkerframework.checker.units.qual.A;
import org.example.connectors.BaseConnector;
import org.example.connectors.okta.Application;
import org.example.connectors.okta.ServicePrincipal;
import org.example.connectors.okta.User;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.MethodParameterScanner;
import static org.reflections.ReflectionUtils.*;
import static org.reflections.scanners.Scanners.*;

import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.reflections.util.QueryFunction;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
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

    Table<String, Object, Object> connectorConfigItemTable = HashBasedTable.create();
    HashMap<String, Node> dagMap = new HashMap<>();



    public static void main( String[] args )  {

        App app = new App();
        app.scanner();
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
            connectorConfigItemTable.put(connector.entrySet().iterator().next().getKey(), findDeepestLevel(getAllSetters(softwareClass), connector.entrySet().iterator().next().getKey()) , Software.class);
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

        Class<?> [] classList = constructorSet.iterator().next().getParameterTypes();
        if(classList.length == 0 ){
            return null;
        }
        else {
            return classList[0];
        }
    }
}