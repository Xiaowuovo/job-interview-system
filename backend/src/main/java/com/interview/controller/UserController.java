package com.interview.controller;

import com.interview.common.Result;
import com.interview.dto.ChangePasswordRequest;
import com.interview.dto.LoginRequest;
import com.interview.dto.RegisterRequest;
import com.interview.dto.UserResponse;
import com.interview.entity.User;
import com.interview.entity.UserAbilityModel;
import com.interview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setTargetPosition(request.getTargetPosition());
        
        User registeredUser = userService.registerUser(user);
        return Result.success(UserResponse.fromEntity(registeredUser));
    }

    @PostMapping("/login")
    public Result<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        return Result.success(UserResponse.fromEntity(user));
    }

    @GetMapping("/{id}")
    public Result<UserResponse> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserResponse::fromEntity)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @PutMapping("/{id}")
    public Result<UserResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return Result.success(UserResponse.fromEntity(updatedUser));
    }

    @GetMapping("/{id}/ability")
    public Result<UserAbilityModel> getUserAbility(@PathVariable Long id) {
        return userService.getUserAbility(id)
                .map(Result::success)
                .orElse(Result.error("能力模型不存在"));
    }

    @PostMapping("/{id}/change-password")
    public Result<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        boolean success = userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        if (success) {
            return Result.success("密码修改成功");
        }
        return Result.error("旧密码错误");
    }

    @PostMapping("/{id}/settings")
    public Result<String> saveSettings(@PathVariable Long id, @RequestBody java.util.Map<String, Object> settings) {
        // 这里可以将设置保存到数据库或只返回成功
        // 目前先返回成功，前端会保存到localStorage
        return Result.success("设置保存成功");
    }
}
