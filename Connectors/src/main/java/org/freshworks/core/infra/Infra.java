package org.freshworks.core.infra;

import java.util.concurrent.LinkedBlockingQueue;

public class Infra {

    public static LinkedBlockingQueue<String>  kafka = new LinkedBlockingQueue<String>();
}
