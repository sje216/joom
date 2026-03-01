package com.joom.signaling.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinAck {

    private String type = "JOIN_ACK";
    private String role;
    private String userId;
    
}
