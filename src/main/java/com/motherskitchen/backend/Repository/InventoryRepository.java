package com.motherskitchen.backend.Repository;

import com.motherskitchen.backend.Models.Inventory.Inventory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    List<Inventory> findByCategoryAndIsActiveTrue(String category, Sort sort);
    List<Inventory> findByIsActiveFalse(Sort sort);
    List<Inventory> findByIsActiveTrue(Sort sort);
    List<Inventory> findTop3By();
}
