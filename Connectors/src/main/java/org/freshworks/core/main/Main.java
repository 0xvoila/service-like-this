package org.freshworks.core.main;

import com.google.common.collect.Multimap;
import com.scalified.tree.TreeNode;
import org.freshworks.core.scanners.ScanConfigItem;
import org.freshworks.core.scanners.ScanConnector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Hello world!
 *
 */
public class Main
{
    Multimap<String, String> connectorConfigItemTable;
    HashMap<String, TreeNode<String>> dagMap ;


    public static void main( String[] args ) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, URISyntaxException {

        ArrayList<HashMap<String, String>> connectorConfig = new ArrayList<>();
        HashMap<String, String> x = new HashMap<>();
        x.put("org.freshworks.connectors.box", "org.freshworks.configitems.box");
        connectorConfig.add(x);
//        x = new HashMap<>();
//        x.put("org.freshworks.connectors.onelogin", "org.freshworks.configitems.onelogin");
//        connectorConfig.add(x);

        Main main = new Main();

        ScanConnector scanConnector = new ScanConnector();
        main.dagMap = scanConnector.scanner(connectorConfig);

        ScanConfigItem scanConfigItem = new ScanConfigItem();
        main.connectorConfigItemTable = scanConfigItem.scanner(connectorConfig,main.dagMap);

        Processor processor = new Processor(main.connectorConfigItemTable);
        processor.process();

        Traverser.traverse(main.dagMap.get("org.freshworks.connectors.box"));
    }
}