package org.freshworks.core.scanners;

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.freshworks.core.constants.Constants;
import org.freshworks.core.Annotations.FreshHierarchy;
import org.freshworks.core.env.Environment;
import org.freshworks.steps.ParentStep;
import org.freshworks.steps.AbstractStep;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;
import static org.reflections.scanners.Scanners.SubTypes;

public class ScanBeans {

    public HashMap<String, TreeNode<String>> scanner(){

        HashMap<String, TreeNode<String>> dagMap = new HashMap<>();

//      Creation of the DAG for the given connector
        Reflections reflections = new Reflections(new ConfigurationBuilder().
                setUrls(ClasspathHelper.forPackage(Constants.STEP_PATH + Environment.getValueByKey("service")))
        );
        dagMap.put((String)Environment.getValueByKey("service"), createDAG(reflections));

        return dagMap;
    }

    public TreeNode<String> createDAG(Reflections reflections){

        HashMap<String, TreeNode<String>> branchMap = new HashMap<>();
        TreeNode<String> root = new ArrayMultiTreeNode<>(ParentStep.class.getName());

        Set<Class<?>> steps =
                reflections.get(SubTypes.of(AbstractStep.class).asClass());

        for (Class<?> step: steps) {

            if(step.getName().equals(ParentStep.class.getName())){
                continue;
            }
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
        if(freshHierarchy.parentClass().getName().equals(ParentStep.class.getName())){
            return ParentStep.class;
        }
        else{
            return freshHierarchy.parentClass();
        }
    }
}
