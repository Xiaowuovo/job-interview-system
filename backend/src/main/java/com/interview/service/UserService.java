package com.interview.service;

import com.interview.entity.User;
import com.interview.entity.UserAbilityModel;
import com.interview.repository.UserRepository;
import com.interview.repository.UserAbilityModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAbilityModelRepository abilityModelRepository;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        User savedUser = userRepository.save(user);

        // 创建用户能力模型
        UserAbilityModel abilityModel = new UserAbilityModel();
        abilityModel.setUserId(savedUser.getId());
        abilityModelRepository.save(abilityModel);

        return savedUser;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (userDetails.getNickname() != null) {
            user.setNickname(userDetails.getNickname());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getAvatar() != null) {
            user.setAvatar(userDetails.getAvatar());
        }
        if (userDetails.getTargetPosition() != null) {
            user.setTargetPosition(userDetails.getTargetPosition());
        }

        return userRepository.save(user);
    }

    public Optional<UserAbilityModel> getUserAbility(Long userId) {
        return abilityModelRepository.findByUserId(userId);
    }
}
