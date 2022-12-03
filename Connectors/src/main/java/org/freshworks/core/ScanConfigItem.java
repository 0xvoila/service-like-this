package org.freshworks.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import config_items.BaseConfigItem;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ReflectionUtilsPredicates.withNamePrefix;

public class ScanConfigItem {

    public Multimap<String, String> scanner(ArrayList<HashMap<String, String>> connectors, HashMap<String, TreeNode<String>> dagMap){

        Multimap<String, String> connectorConfigItemTable = ArrayListMultimap.create();

        for (HashMap<String, String> connector : connectors) {

//            Creation of the config item table for the given config item and connector.
//            As of now we have just Software class
            Reflections reflections = new Reflections( new ConfigurationBuilder().
                    setUrls(ClasspathHelper.forPackage(connector.entrySet().iterator().next().getValue())).
                    filterInputsBy(new FilterBuilder().includePackage(connector.entrySet().iterator().next().getValue()))
            );

            Set<Class<?>> steps = reflections.get(SubTypes.of(BaseConfigItem.class).
                    filter(withNamePrefix(connector.entrySet().iterator().next().getValue()))
                    .asClass());

            Class<?> softwareClass = steps.iterator().next();
            ArrayList<String> dependentClassList = findDependencyOfConfigItem(Utility.getAllSetters(softwareClass), connector.entrySet().iterator().next().getKey(), dagMap);
            for (String dependent:
                    dependentClassList) {
                connectorConfigItemTable.put(softwareClass.getName(), dependent);
            }
        }

        return connectorConfigItemTable;
    }

    public ArrayList<String> findDependencyOfConfigItem(List<Method> setterMethodList, String connector, HashMap<String, TreeNode<String>> dagMap){

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
}
