package org.downloader.contants;

public class Constants {

    public static final String ETC_HOST = "localhost";
    public static final int ETC_PORT = 2379;
    public static final String INPUT_KAFKA_QUEUE = "downloader-input";
    public static final String EXECUTE_KAFKA_QUEUE = "downloader-execute";
    public static final String DELAY_KAFKA_QUEUE = "downloader-delayed";

    public static final String SUCCESS_KAFKA_QUEUE = "downloader-success";

    public static final String FAILURE_KAFKA_QUEUE = "downloader-failure";

    public static final String DELAYED_REQUEST_TAG = "_downloader_delayed";

}
