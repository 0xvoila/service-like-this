package org.system.amit.lamport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;

public class Mutation implements Serializable {

    private String command;
    private String key;
    private String value;

    private int timestamp;

    private Boolean tombstone;

    public Mutation(String command, String key, String value, int timestamp){
        this.command = command;
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
        this.tombstone = false;
    }

    public Mutation(){

    }

    public Boolean getTombstone(){
        return this.tombstone;
    }

    public void setTombstone(Boolean tombstone){
        this.tombstone = tombstone;
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

    public String getValue(){
        return this.value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public int getTimestamp(){
        return  this.timestamp;
    }

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }

}
