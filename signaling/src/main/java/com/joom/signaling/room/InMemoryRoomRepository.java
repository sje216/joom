package com.joom.signaling.room;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("local")
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<String, Map<String, String>> rooms = new ConcurrentHashMap<>();


    @Override
    public void addUser(String roomId, String userId, String sessionId) {
        rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(userId, sessionId);
    }

    @Override
    public void removeUser(String roomId, String userId) {
        Map<String, String> room = rooms.get(roomId);
        if (room != null) {
            room.remove(userId);
        }
    }

    @Override
    public Map<String, String> getRoom(String roomId) {
        return rooms.get(roomId);
    }

    @Override
    public String getUser(String roomId, String userId) {
        Map<String, String> room = rooms.get(roomId);
        return room != null ? room.get(userId) : null;
    }
}
