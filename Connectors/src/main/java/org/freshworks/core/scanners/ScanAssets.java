package org.freshworks.core.scanners;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import org.freshworks.core.constants.Constants;
import org.freshworks.assets.BaseAsset;
import org.freshworks.core.env.Environment;
import org.freshworks.core.utils.Utility;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.*;

import static org.freshworks.core.constants.Constants.DAG_MAX_HEIGHT;
import static org.reflections.scanners.Scanners.SubTypes;

public class ScanAssets {


    public Multimap<String, String> scanner(HashMap<String, TreeNode<String>> dagMap){

        Multimap<String, String> connectorConfigItemTable = ArrayListMultimap.create();


//            Creation of the config item table for the given config item and connector.
//            As of now we have just Software class
        Reflections reflections = new Reflections( new ConfigurationBuilder().
                setUrls(ClasspathHelper.forPackage(Constants.ASSET_PATH + Environment.getValueByKey("service")))
        );

        Set<Class<?>> steps = reflections.get(SubTypes.of(BaseAsset.class)
                .asClass());

        for (Class<?> configItem : steps) {
            ArrayList<String> dependentClassList = findDependencyOfConfigItem(Utility.getAllSetters(configItem), dagMap);
            for (String dependent :
                    dependentClassList) {
                connectorConfigItemTable.put(configItem.getName(), dependent);
            }
        }

        return connectorConfigItemTable;
    }

    public ArrayList<String> findDependencyOfConfigItem(List<Method> setterMethodList, HashMap<String, TreeNode<String>> dagMap){

        TreeNode<String> DAG = dagMap.get((String)Environment.getValueByKey("service"));

        ArrayList<String> dependents = new ArrayList<>();

        ArrayList<Class<?>> arrayList = new ArrayList<>();

        for(Method setter: setterMethodList){
            ArrayList<Class<?>> y = new ArrayList(Arrays.asList(setter.getParameterTypes()));
            arrayList.addAll(y);
        }
        HashSet<Class<?>> u =  new HashSet<>(arrayList);

        Iterator<Class<?>> it  = u.iterator();

        int nodeHeight = DAG_MAX_HEIGHT;
        while(it.hasNext()){
            Class<?> c = it.next();
            String x = c.getName();
            HashMap<String, String> metaData = Utility.getMetaDataByClass(c);
            String traverserName = metaData.get("postman");
            TreeNode<String> n = DAG.find(traverserName);
            if (n != null && n.isLeaf()){
                dependents.add(x);
                nodeHeight = n.height();
            }
        }
        return dependents;
    }
}
