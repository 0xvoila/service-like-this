package org.amit.system;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ServerEndpoint(
        value="/chat/{username}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class )
public class ChatEndPoint extends HttpServlet{

    private Session session;
    private static Set<ChatEndPoint> chatEndPoints
            = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {

        System.out.println("New socket is opened");
        String username = "Amit";
        // Get session and WebSocket connection
        this.session = session;
        chatEndPoints.add(this);
        users.put(session.getId(),username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected");
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException {
        // Handle new messages
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {

        chatEndPoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}