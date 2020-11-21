package com.example.h3server.repositories;

import com.example.h3server.models.TreeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeTagRepository extends JpaRepository<TreeTag, Long> {

    TreeTag findByLabel(String label);

    @Query(value = "SELECT COUNT(*) FROM trees_tags AS tt WHERE tt.tag_id = ?1",
            nativeQuery = true)
    int findTreesCountWithTag(Long tagId);
}
