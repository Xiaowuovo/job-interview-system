package com.interview.dto;

import com.interview.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String nickname;
    private String role;
    private String membershipType;
    private LocalDateTime membershipExpireAt;
    private String targetPosition;
    private Integer points;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole().name());
        response.setMembershipType(user.getMembershipType().name());
        response.setMembershipExpireAt(user.getMembershipExpireAt());
        response.setTargetPosition(user.getTargetPosition());
        response.setPoints(user.getPoints());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
