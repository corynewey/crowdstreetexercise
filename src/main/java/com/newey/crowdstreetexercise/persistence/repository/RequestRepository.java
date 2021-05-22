package com.newey.crowdstreetexercise.persistence.repository;

import com.newey.crowdstreetexercise.persistence.entities.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    RequestEntity findById(long id);
}
