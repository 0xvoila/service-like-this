package org.freshworks;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Constants {

    public final static String JsonTypeInfo_As_PROPERTY = "type";
    public final static String GETTER_METHOD_PREFIX = "get";
    public final static String SETTER_METHOD_PREFIX = "set";

    public final static int DAG_MAX_HEIGHT = 10000;

    public final static String STEP_PATH = "org.freshworks.steps.";
    public final static String BEAN_PATH = "org.freshworks.beans.";
    public final static String ASSET_PATH = "org.freshworks.assets.";
}
