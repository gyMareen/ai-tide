package com.ai.tide.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update Profile DTO
 */
@Data
public class UpdateProfileDTO {

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Size(max = 255, message = "头像URL长度不能超过255")
    private String avatar;

    @Size(max = 500, message = "个人简介长度不能超过500")
    private String bio;

    @Size(max = 100, message = "手机号长度不能超过100")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 255, message = "网站URL长度不能超过255")
    private String website;

    @Size(max = 100, message = "位置长度不能超过100")
    private String location;
}
