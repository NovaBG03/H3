package com.example.h3server.repositories;

import com.example.h3server.models.Fact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactRepository extends CrudRepository<Fact, Long> {
}
