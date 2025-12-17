package com.interview.repository;

import com.interview.entity.UserAbilityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserAbilityModelRepository extends JpaRepository<UserAbilityModel, Long> {

    // 根据用户ID查询能力模型
    Optional<UserAbilityModel> findByUserId(Long userId);

    // 检查用户是否已有能力模型
    boolean existsByUserId(Long userId);
}
