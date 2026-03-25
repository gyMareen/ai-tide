package com.ai.tide.vo;

import com.ai.tide.enums.AccountStatus;
import com.ai.tide.enums.Gender;
import com.ai.tide.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User View Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String bio;
    private Role role;
    private AccountStatus status;
    private Gender gender;
    private LocalDateTime birthday;
    private String phone;
    private String website;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean emailVerified;
}
