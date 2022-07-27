package org.system.amit.asynchronous;

import com.google.common.util.concurrent.*;

import java.util.concurrent.*;


public class First {

    public static void main(String args[]) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
        ListenableFuture<Integer> x = listeningExecutorService.submit(() -> factorial(10));

        Futures.addCallback(x, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                System.out.println("Factorial is executed " + integer);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        },executorService);

        System.out.println("I am calling from main thread as the last statement");

        if (!listeningExecutorService.awaitTermination(10,TimeUnit.SECONDS)){
            listeningExecutorService.shutdownNow();
        }
    }

    public static int factorial(int n) throws InterruptedException {
        Thread.sleep(100);
        return n;
    }
}
