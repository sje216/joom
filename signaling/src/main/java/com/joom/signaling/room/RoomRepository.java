package com.joom.signaling.room;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public interface RoomRepository {

    void addUser(String roomId, String userId, String session);

    void removeUser(String roomId, String userId);

    Map<String, String> getRoom(String roomId);

    String getUser(String roomId, String userId);
}
