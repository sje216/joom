package com.joom.signaling.room;

import com.joom.signaling.ws.dto.Role;
import com.joom.signaling.ws.websocket.WebSocketSessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final WebSocketSessionStore sessionStore;

    public RoomService(RoomRepository roomRepository, WebSocketSessionStore sessionStore) {
        this.roomRepository = roomRepository;
        this.sessionStore = sessionStore;
    }

    public Role join(String roomId, String userId, String sessionId) throws Exception {
        log.info("🧑 JOIN room={}, user={}, session={}", roomId, userId, sessionId);

        Map<String, String> room = roomRepository.getRoom(roomId);
        boolean isFirst          = (room == null || room.isEmpty());

        Role role                = isFirst ? Role.CALLER : Role.CALLEE;
        roomRepository.addUser(roomId, userId, sessionId);

        if(!isFirst){
            notifyPeerReady(roomId, userId);
        }
        return role;
    }

    private void notifyPeerReady(String roomId, String newUserId) throws Exception {
        Map<String, String> room = roomRepository.getRoom(roomId);
        if(room == null || room.size() < 2) return;

        String payload = """
                    {
                       "type": "PEER_READY",
                       "userId": "%s"
                    }
                    """.formatted(newUserId);

        for(Map.Entry<String, String> entry : room.entrySet()) {
            String userId = entry.getKey();
            String sessionId = entry.getValue();

            if(userId.equals(newUserId)) continue;

            WebSocketSession caller = sessionStore.get(sessionId);
            if(caller != null && caller.isOpen()){
                caller.sendMessage(new TextMessage(payload));
            }
        }
        log.info("👥 PEER_READY sent to CALLER");
    }

    public void leave(String roomId, String userId) throws Exception {
        roomRepository.removeUser(roomId, userId);
        broadcast(roomId, "LEAVE", userId);
    }

    public void relay(String roomId, String targetId, String payload) throws Exception {

        String sessionId = roomRepository.getUser(roomId, targetId);
        log.info("📡 relay room={}, target={}, session={}",
                roomId, targetId, sessionId);

        if(sessionId == null) return;

        WebSocketSession target = sessionStore.get(sessionId);
        if(target != null && target.isOpen()) {
            target.sendMessage(new TextMessage(payload));
        }
    }

    public void broadcast(String roomId, String type, String userId) throws Exception {
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
