package com.conninvest.backend.repository;

import com.conninvest.backend.model.NicheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NicheConfigRepository extends JpaRepository<NicheConfig, String> {
    List<NicheConfig> findAllByOrderBySortOrderAsc();
}
