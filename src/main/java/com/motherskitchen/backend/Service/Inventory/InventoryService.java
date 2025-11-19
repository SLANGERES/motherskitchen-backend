package com.motherskitchen.backend.Service.Inventory;

import com.motherskitchen.backend.DTO.Inventory.InventoryDTO;
import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.Models.User.User;
import com.motherskitchen.backend.Repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    //Dependency Injection
    private InventoryRepository inventoryRepository;

    @Transactional
    public boolean createProduct(InventoryDTO request){
        
    }
}
