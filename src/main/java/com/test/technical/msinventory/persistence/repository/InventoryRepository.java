package com.test.technical.msinventory.persistence.repository;

import com.test.technical.msinventory.persistence.entity.InventoryEntity;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {
}
