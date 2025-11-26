package com.motherskitchen.backend.Service.Inventory;

import com.motherskitchen.backend.DTO.Inventory.InventoryDTO;
import com.motherskitchen.backend.DTO.Inventory.ProductDTO;
import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.Models.Inventory.Inventory;
import com.motherskitchen.backend.Models.User.User;
import com.motherskitchen.backend.Repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public boolean createProduct(InventoryDTO request) {
        Inventory product = Inventory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .imageUrl(request.getImageURL())
                .imageKey(request.getImageKey())
                .isActive(true)
                .build();

        try {
            inventoryRepository.save(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ProductDTO> getAllActiveProduct() {
        List<Inventory> products = inventoryRepository.findByIsActiveTrue(
                Sort.by("name").ascending()
        );

        // Convert Inventory → ProductDTO
        return products.stream().map(p ->
                ProductDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .category(p.getCategory())
                        .image(p.getImageUrl())
                        .build()
        ).toList();
    }
    public List<ProductDTO> getAllInActiveProduct() {
        List<Inventory> products = inventoryRepository.findByIsActiveFalse(
                Sort.by("name").ascending()
        );

        // Convert Inventory → ProductDTO
        return products.stream().map(p ->
                ProductDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .category(p.getCategory())
                        .image(p.getImageUrl())
                        .build()
        ).toList();
    }
    public List<ProductDTO> getAllProduct() {
        List<Inventory> products = inventoryRepository.findAll(
                Sort.by("name").ascending()
        );

        // Convert Inventory → ProductDTO
        return products.stream().map(p ->
                ProductDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .category(p.getCategory())
                        .image(p.getImageUrl())
                        .build()
        ).toList();
    }

    public List<ProductDTO>getProductByCategory(String category){
        List <Inventory> products=inventoryRepository.findByCategoryAndIsActiveTrue(category,Sort.by("name").ascending());

        return products.stream().map(p->
                ProductDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .category(category)
                        .image(p.getImageUrl())
                        .build()
                ).toList();
    }
    public Optional<ProductDTO> getProductById(String productID) {

        Optional<Inventory> tempProduct = inventoryRepository.findById(UUID.fromString(productID));

        if (tempProduct.isEmpty()) {
            return Optional.empty();
        }

        Inventory p = tempProduct.get();

        ProductDTO product = ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .image(p.getImageUrl())
                .description(p.getDescription())
                .category(p.getCategory())
                .build();

        return Optional.of(product);
    }
    @Transactional
    public boolean deactivateProduct(String productId) {
        Optional<Inventory> temp = inventoryRepository.findById(UUID.fromString(productId));

        if (temp.isEmpty()) {
            return false;
        }

        Inventory product = temp.get();
        product.setActive(false);   // set isActive = false

        inventoryRepository.save(product);
        return true;
    }
    @Transactional
    public boolean activateProduct(String productId) {
        Optional<Inventory> temp = inventoryRepository.findById(UUID.fromString(productId));

        if (temp.isEmpty()) {
            return false;
        }

        Inventory product = temp.get();
        product.setActive(true);   // set isActive = true

        inventoryRepository.save(product);
        return true;
    }
    @Transactional
    public Optional<InventoryDTO> deleteProduct(String productId) {
        Optional<Inventory> temp = inventoryRepository.findById(UUID.fromString(productId));

        if (temp.isEmpty()) {
            return Optional.empty();
        }

        Inventory product = temp.get();
        inventoryRepository.delete(product);
        InventoryDTO response=InventoryDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageKey(product.getImageKey())
                .imageURL(product.getImageUrl())
                .build();
        return Optional.of(response);
    }



}
