package com.joom.signaling.ws.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketSessionStore {

    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    public void add(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public WebSocketSession get(String sessionId) {
        return sessions.get(sessionId);
    }

}
