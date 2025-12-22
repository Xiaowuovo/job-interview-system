package com.interview.service;

import com.interview.entity.User;
import com.interview.entity.UserAbilityModel;
import com.interview.exception.BusinessException;
import com.interview.repository.UserRepository;
import com.interview.repository.UserAbilityModelRepository;
import com.interview.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
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
        log.info("注册新用户: {}", user.getUsername());
        
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("邮箱已被使用");
        }

        // 加密密码
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(user.getPassword(), salt);
        user.setPassword(encryptedPassword);
        user.setSalt(salt);

        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}, ID: {}", savedUser.getUsername(), savedUser.getId());

        // 创建用户能力模型
        UserAbilityModel abilityModel = new UserAbilityModel();
        abilityModel.setUserId(savedUser.getId());
        abilityModelRepository.save(abilityModel);

        return savedUser;
    }

    @Transactional
    public User login(String username, String password) {
        log.info("用户登录: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 兼容旧数据（明文密码）和新数据（加密密码）
        boolean isValid = false;
        if (user.getSalt() != null && !user.getSalt().isEmpty()) {
            // 新加密方式
            isValid = PasswordUtil.verifyPassword(password, user.getPassword(), user.getSalt());
        } else {
            // 兼容旧数据明文密码
            isValid = user.getPassword().equals(password);
            
            // 如果验证成功，升级为加密密码
            if (isValid) {
                String salt = PasswordUtil.generateSalt();
                String encryptedPassword = PasswordUtil.encryptPassword(password, salt);
                user.setPassword(encryptedPassword);
                user.setSalt(salt);
                userRepository.save(user);
                log.info("用户密码已升级为加密存储: {}", username);
            }
        }

        if (!isValid) {
            log.warn("登录失败，密码错误: {}", username);
            throw new BusinessException("密码错误");
        }

        log.info("用户登录成功: {}", username);
        return user;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

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
