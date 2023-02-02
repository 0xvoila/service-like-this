package org.freshworks.core.constants;

public class Constants {

    public final static String JsonTypeInfo_As_PROPERTY = "@class";
    public final static String GETTER_METHOD_PREFIX = "get";

    public final static String SETTER_METHOD_PREFIX = "set";
    public final static int DAG_MAX_HEIGHT = 10000;

    public final static String BASE_BEAN = "baseBean";
    public final static String STEP_PATH = "org.freshworks.steps.";
    public final static String BEAN_PATH = "org.freshworks.beans.";
    public final static String ASSET_PATH = "org.freshworks.assets.";

    public final static String SYNC_STATUS_KEY = "sync_status";
    public enum SYNC_STATUS{
        START,
        IN_PROGRESS,
        TRAVERSE_SUCCESS,
        TRAVERSE_FAILED,
        PROCESS_SUCCESS,
        PROCESS_FAILED,
        TOTAL_SUCCESS
    }
}
