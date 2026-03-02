package com.joom.signaling.ws;

import com.joom.signaling.room.RoomService;
import com.joom.signaling.ws.dto.JoinAck;
import com.joom.signaling.ws.dto.Role;
import com.joom.signaling.ws.dto.SignalMessage;
import com.joom.signaling.ws.websocket.WebSocketSessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoomService  roomService;
    private final WebSocketSessionStore sessionStore;

    public SignalingHandler(RoomService roomService, WebSocketSessionStore sessionStore) {
        this.roomService = roomService;
        this.sessionStore = sessionStore;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionStore.add(session);
        System.out.println("Connected to " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{

        SignalMessage signal =  objectMapper.readValue(message.getPayload(), SignalMessage.class);

        switch(signal.getType()){
            case JOIN -> {
                Role role = roomService.join(signal.getRoomId(), signal.getUserId(), session.getId());
                JoinAck ack = new  JoinAck("JOIN_ACK", role.name(), signal.getUserId());
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(ack)));
            }
            case LEAVE -> roomService.leave(signal.getRoomId(), session.getId());
            case OFFER, ANSWER, ICE ->{
                if(signal.getTarget() == null){
                    log.error("target is null : {}", signal);
                    return;
                }
                    roomService.relay(signal.getRoomId(), signal.getTarget(), objectMapper.writeValueAsString(signal));
            }
            case READY -> roomService.broadcast(signal.getRoomId(), "PEER_READY", signal.getUserId());
            default -> System.out.println("Unknown SignalType " + signal.getType());
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionStore.remove(session.getId());
        System.out.println("Disconnected from " + session.getId());
    }

}
