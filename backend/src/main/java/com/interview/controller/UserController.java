package com.interview.controller;

import com.interview.common.Result;
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
}
