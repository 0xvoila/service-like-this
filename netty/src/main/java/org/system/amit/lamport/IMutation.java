package org.system.amit.lamport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

public class IMutation implements Serializable {

    private String command;
    private String key;

    private int timestamp;

    public IMutation(String command, String key, int timestamp){
        this.command = command;
        this.key = key;
        this.timestamp = timestamp;
    }

    public IMutation(){

    }

    public String getKey(){
        return this.key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getCommand(){
        return this.command;
    }

    public void setCommand(String command){
        this.command = command;
    }

    public int getTimestamp(){
        return  this.timestamp;
    }

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }

}
