package org.example;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Queue {

    public static LinkedBlockingQueue<Map<HttpGet, HttpResponse>> queue = new LinkedBlockingQueue<Map<HttpGet, HttpResponse>>();
}
