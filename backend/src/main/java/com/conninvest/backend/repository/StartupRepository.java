package com.conninvest.backend.repository;

import com.conninvest.backend.model.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {
    List<Startup> findByNameContainingIgnoreCaseOrSectorContainingIgnoreCase(String name, String sector);
    List<Startup> findByStageIgnoreCase(String stage);
}
