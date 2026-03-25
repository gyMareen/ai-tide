package com.ai.tide.controller;

import com.ai.tide.dto.ChangePasswordDTO;
import com.ai.tide.dto.LoginDTO;
import com.ai.tide.dto.RegisterDTO;
import com.ai.tide.dto.UpdateProfileDTO;
import com.ai.tide.security.CurrentUserService;
import com.ai.tide.service.UserService;
import com.ai.tide.vo.LoginVO;
import com.ai.tide.vo.Result;
import com.ai.tide.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * User registration
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);
        return Result.success("注册成功", userVO);
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        LoginVO loginVO = userService.login(loginDTO, ipAddress);
        return Result.success("登录成功", loginVO);
    }

    /**
     * User logout
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = CurrentUserService.getCurrentUserId();
        if (userId != null) {
            userService.logout(userId);
        }
        return Result.success("退出成功", null);
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        Long userId = CurrentUserService.getCurrentUserId();
        UserVO userVO = userService.getCurrentUser(userId);
        return Result.success(userVO);
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@Valid @RequestBody UpdateProfileDTO updateDTO) {
        Long userId = CurrentUserService.getCurrentUserId();
        UserVO userVO = userService.updateProfile(userId, updateDTO);
        return Result.success("更新成功", userVO);
    }

    /**
     * Change password
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        Long userId = CurrentUserService.getCurrentUserId();
        userService.changePassword(userId, changePasswordDTO);
        return Result.success("密码修改成功", null);
    }

    /**
     * Get client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
