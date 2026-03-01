package com.joom.signaling.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalMessage {
    private SignalType type;
    private String roomId;
    private String userId;
    private String target; // 특정 유저한테 보낼경우
    // webRTC
    private JsonNode sdp;
    // 지금만 object로 받음
    private Object candidate;
}
