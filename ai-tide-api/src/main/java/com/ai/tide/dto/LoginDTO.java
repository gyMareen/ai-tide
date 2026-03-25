package com.ai.tide.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * User Login DTO
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名或邮箱不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
