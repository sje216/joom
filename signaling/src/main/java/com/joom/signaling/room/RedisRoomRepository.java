package com.joom.signaling.room;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Profile("redis")
public class RedisRoomRepository implements RoomRepository {

    private RedisTemplate<String, String> redis;

    public RedisRoomRepository(RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    @Override
    public void addUser(String roomId, String userId, String sessionId) {
        redis.opsForHash().put(roomUsersKey(roomId), userId, sessionId);
    }

    @Override
    public void removeUser(String roomId, String userId) {
        redis.opsForHash().delete(roomUsersKey(roomId), userId);
    }

    @Override
    public Map<String, String> getRoom(String roomId) {
        Map<Object, Object> entries = redis.opsForHash().entries(roomUsersKey(roomId));

        Map<String, String> room = new HashMap<>();
        entries.forEach((key, value) -> room.put(key.toString(), value.toString()));
        return room;
    }

    @Override
    public String getUser(String roomId, String userId) {
        return (String) redis.opsForHash()
                .get(roomUsersKey(roomId), userId);
    }

    private String roomUsersKey(String roomId){
        return "room:" + roomId + ":users";
    }
}
