package org.amit.system;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UIDGenerator extends HttpServlet {

    int sequence = 0;
    final static int sequenceBits = 12; // max is 4096
    final static int maxSequence = (int)Math.pow(2, sequenceBits) - 1;

    final static int nodeId = 11;
    final static int nodeBits = 10; // max is 1024

    public synchronized int generateSequence(long runningMillisecond){

        this.sequence = this.sequence + 1;

        if (this.sequence >= maxSequence && runningMillisecond == System.currentTimeMillis()){
            waitOneMillisecond(runningMillisecond);
            this.sequence = 0;
        }

        return this.sequence;
    }

    private void waitOneMillisecond(long runningMillisecond){

        while(runningMillisecond == System.currentTimeMillis()){
            continue;
        }
    }

    public int generateNodeId(){
        return this.nodeId;
    }

    public void run(){
        int nodeNumber = this.generateNodeId();

        long epoch = System.currentTimeMillis();
        int sequence = this.generateSequence(epoch);

//        System.out.println("Echo before shift " + epoch);
        epoch = epoch << (this.nodeBits + this.sequenceBits);
        epoch = epoch | nodeNumber << this.sequenceBits;
        epoch = epoch | sequence;

        System.out.println("Echo after shift " + epoch);
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        int nodeNumber = this.generateNodeId();

        long epoch = System.currentTimeMillis();
        int sequence = this.generateSequence(epoch);

        epoch = epoch << (this.nodeBits + this.sequenceBits);
        epoch = epoch | nodeNumber << this.sequenceBits;
        epoch = epoch | sequence;

        response.getOutputStream().print("Echo after shift " + epoch);

    }
}
