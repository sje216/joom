package com.joom.signaling.room;

import com.joom.signaling.ws.websocket.WebSocketSessionStore;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final WebSocketSessionStore sessionStore;

    public RoomService(RoomRepository roomRepository, WebSocketSessionStore sessionStore) {
        this.roomRepository = roomRepository;
        this.sessionStore = sessionStore;
    }

    public void join(String roomId, String userId, String sessionId) throws Exception {
        roomRepository.addUser(roomId, userId, sessionId);
        broadcast(roomId, "JOIN", userId);
    }

    public void leave(String roomId, String userId) throws Exception {
        roomRepository.removeUser(roomId, userId);
        broadcast(roomId, "LEAVE", userId);
    }

    public void relay(String roomId, String targetId, String payload) throws Exception {
        String sessionId = roomRepository.getUser(roomId, targetId);
        if(sessionId == null) return;

        WebSocketSession target = sessionStore.get(sessionId);
        if(target != null && target.isOpen()) {
            target.sendMessage(new TextMessage(payload));
        }
    }

    private void broadcast(String roomId, String type, String userId) throws Exception {
        Map<String,String> room = roomRepository.getRoom(roomId);
        if(room == null) return;

        String payload = """
                {
                    "type": "%s",
                    "userId": "%s"
                }
                """.formatted(type, userId);

        for(String sessionId : room.values()){
            WebSocketSession session = sessionStore.get(sessionId);
            if(session.isOpen()){
                session.sendMessage(new TextMessage(payload));
            }
        }

    }
}
