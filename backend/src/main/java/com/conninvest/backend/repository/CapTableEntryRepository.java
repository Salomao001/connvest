package com.conninvest.backend.repository;

import com.conninvest.backend.model.CapTableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapTableEntryRepository extends JpaRepository<CapTableEntry, Long> {
    List<CapTableEntry> findByStartupIdOrderBySortOrderAsc(Long startupId);
    void deleteByStartupId(Long startupId);
}
