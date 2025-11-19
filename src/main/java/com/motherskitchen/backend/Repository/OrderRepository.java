package com.motherskitchen.backend.Repository;

import com.motherskitchen.backend.Models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {
    List<Orders>findByStatus(String status);
}
