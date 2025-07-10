package com.mikkkkkkka.cat.dao;

import com.mikkkkkkka.cat.model.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long>, JpaSpecificationExecutor<Cat> {
    List<Cat> findByOwnerId(Long ownerId);
}