package org.amit.system;

public class Message {

    String messageFromUser;
    String messageContent;

    protected void setFrom(String messageFromUser){
        this.messageFromUser = messageFromUser;
    }

    protected void setContent(String messageContent){
        this.messageContent = messageContent;
    }
}
