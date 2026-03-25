package com.ai.tide.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response View Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    private String token;
    private UserVO user;
}
