package org.freshworks.core.scanners;

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import config_items.BaseConfigItem;
import org.freshworks.connectors.BaseConnector;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.ReflectionUtils.Constructors;
import static org.reflections.ReflectionUtils.get;
import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ReflectionUtilsPredicates.withNamePrefix;

public class ScanConnector {

    public HashMap<String, TreeNode<String>> scanner(ArrayList<HashMap<String, String>> connectors){

        HashMap<String, TreeNode<String>> dagMap = new HashMap<>();

        for (HashMap<String, String> connector : connectors) {

//            Creation of the DAG for the given connector
            Reflections reflections = new Reflections(new ConfigurationBuilder().
                    setUrls(ClasspathHelper.forPackage(connector.entrySet().iterator().next().getKey())).
                    filterInputsBy(new FilterBuilder().includePackage(connector.entrySet().iterator().next().getKey()))
            );

            dagMap.put(connector.entrySet().iterator().next().getKey(), createDAG(reflections));

        }

        return dagMap;
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
            TreeNode<String> node = root.find(entry.getValue().data());
            if ( node == null){
                root.add(entry.getValue());
            }
            else{
                for( TreeNode<String> v : entry.getValue().subtrees()){
                    node.add(v);
                }
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
}
