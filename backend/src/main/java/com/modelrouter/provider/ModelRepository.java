package com.modelrouter.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {
    List<Model> findByStatus(String status);
}
