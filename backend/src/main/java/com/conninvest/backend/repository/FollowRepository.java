package com.conninvest.backend.repository;

import com.conninvest.backend.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndTargetTypeAndTargetId(Long followerId, String targetType, Long targetId);
    List<Follow> findByFollowerId(Long followerId);
    long countByTargetTypeAndTargetId(String targetType, Long targetId);
}
