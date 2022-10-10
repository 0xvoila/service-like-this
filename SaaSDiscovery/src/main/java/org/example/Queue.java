package org.example;


import org.example.models.RequestResponse;

import java.util.concurrent.LinkedBlockingQueue;

public class Queue {

    public static LinkedBlockingQueue<RequestResponse> queue = new LinkedBlockingQueue<RequestResponse>();
}
