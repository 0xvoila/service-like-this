package org.freshworks.core.main;

import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import org.freshworks.core.scanners.ScanAssets;
import org.freshworks.core.scanners.ScanBeans;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class Main
{
    Multimap<String, String> connectorConfigItemTable;
    HashMap<String, TreeNode<String>> dagMap ;


    public static void main( String[] args ) {

        HashMap<String, String> syncConfig = new HashMap<>();
        syncConfig.put("transaction_id", "11223");
        syncConfig.put("service", "box");
        syncConfig.put("params", "123");

        Main main = new Main();

        ScanBeans scanBeans = new ScanBeans();
        main.dagMap = scanBeans.scanner(syncConfig);

        ScanAssets scanAssets = new ScanAssets();
        main.connectorConfigItemTable = scanAssets.scanner(syncConfig,main.dagMap);

        ExecutorService threadService = Executors.newFixedThreadPool(2);

        threadService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Iterator<TreeNode<String>> it = main.dagMap.get(syncConfig.get("service")).iterator();
                while(it.hasNext()){
                    try{
                        Traverser.traverse(it.next(), syncConfig);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                }
                return null;
            }
        });

        threadService.submit(new Callable<Void>() {
            @Override
            public Void call() {
                try{
                    Processor processor = new Processor(main.connectorConfigItemTable);
                    processor.process();
                    return null;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}