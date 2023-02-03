package org.freshworks.core.main;

import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.freshworks.core.constants.Constants;
import org.freshworks.core.env.Environment;
import org.freshworks.core.scanners.ScanAssets;
import org.freshworks.core.scanners.ScanBeans;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class Main
{
    Multimap<String, String> serviceAssetTable;
    HashMap<String, TreeNode<String>> dagMap ;


    public static void main( String[] args ) {

        log.debug("setting up environment variables");
        Environment.setKeyValue(Constants.SYNC_STATUS_KEY, Constants.SYNC_STATUS.START);

        Environment.setKeyValue("transaction_id", "11223");
        Environment.setKeyValue("service", "box");
        Environment.setKeyValue("params", "123");

        Main main = new Main();

        ScanBeans scanBeans = new ScanBeans();
        main.dagMap = scanBeans.scanner();
        checkArgument(main.dagMap.size() > 0 , "Directed Acyclic Graph for the service can not be null");

        ScanAssets scanAssets = new ScanAssets();
        main.serviceAssetTable = scanAssets.scanner(main.dagMap);
        checkArgument(main.serviceAssetTable.size() > 0, "Service asset table for the service can not be null");

        ExecutorService threadService = Executors.newFixedThreadPool(2);

        threadService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Iterator<TreeNode<String>> it = main.dagMap.get((String)Environment.getValueByKey("service")).iterator();
                while(it.hasNext()){
                    try{
                        Environment.setKeyValue(Constants.SYNC_STATUS_KEY, Constants.SYNC_STATUS.IN_PROGRESS);
                        Traverser.traverse(it.next());
                    }
                    catch(Exception e){
                        Environment.setKeyValue(Constants.SYNC_STATUS_KEY, Constants.SYNC_STATUS.TRAVERSE_FAILED);
                        e.printStackTrace();
                    }

                }
                Environment.setKeyValue(Constants.SYNC_STATUS_KEY, Constants.SYNC_STATUS.TRAVERSE_SUCCESS);
                return null;
            }
        });

        threadService.submit(new Callable<Void>() {
            @Override
            public Void call() {
                try{
                    Processor processor = new Processor(main.serviceAssetTable);
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