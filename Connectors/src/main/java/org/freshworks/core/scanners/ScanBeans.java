package org.freshworks.core.scanners;

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.freshworks.Constants;
import org.freshworks.core.Annotations.FreshHierarchy;
import org.freshworks.steps.BaseStep;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;
import static org.reflections.scanners.Scanners.SubTypes;

public class ScanBeans {

    public HashMap<String, TreeNode<String>> scanner(HashMap<String, String> syncConfig){

        HashMap<String, TreeNode<String>> dagMap = new HashMap<>();

//      Creation of the DAG for the given connector
        Reflections reflections = new Reflections(new ConfigurationBuilder().
                setUrls(ClasspathHelper.forPackage(Constants.STEP_PATH + syncConfig.get("service")))
        );
        dagMap.put(syncConfig.get("service"), createDAG(reflections));

        return dagMap;
    }

    public TreeNode<String> createDAG(Reflections reflections){

        HashMap<String, TreeNode<String>> branchMap = new HashMap<>();
        TreeNode<String> root = new ArrayMultiTreeNode<>(null);

        Set<Class<?>> steps =
                reflections.get(SubTypes.of(BaseStep.class).asClass());

        for (Class<?> step: steps) {
            Class<?> parentClass = getParentClass(step);

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


    public Class<?> getParentClass(Class<?> clazz){

        FreshHierarchy freshHierarchy = clazz.getAnnotation(FreshHierarchy.class);
        if(freshHierarchy.parentClass().getName().equals(Void.class.getName())){
            return null;
        }
        else{
            return freshHierarchy.parentClass();
        }
    }
}
