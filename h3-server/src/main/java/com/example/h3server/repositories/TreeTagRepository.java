package com.example.h3server.repositories;

import com.example.h3server.models.TreeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeTagRepository extends JpaRepository<TreeTag, Long> {

    TreeTag findByLabel(String label);
}
