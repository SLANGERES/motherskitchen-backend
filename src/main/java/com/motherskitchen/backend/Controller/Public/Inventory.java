package com.motherskitchen.backend.Controller.Public;

import com.motherskitchen.backend.Aws.S3.S3Service;
import com.motherskitchen.backend.Aws.S3.UploadDTO;
import com.motherskitchen.backend.DTO.Inventory.InventoryDTO;
import com.motherskitchen.backend.DTO.Inventory.ProductDTO;
import com.motherskitchen.backend.Service.Inventory.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class Inventory {

    private static final Logger log = LoggerFactory.getLogger(Inventory.class);

    private final InventoryService inventoryService;
    private final S3Service s3Service;

    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProduct() {
        return ResponseEntity.ok(inventoryService.getAllProduct());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductDTO>> getAllActiveProduct() {
        return ResponseEntity.ok(inventoryService.getAllActiveProduct());
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> addNewProduct(
            @Valid @RequestPart("product") InventoryDTO request,
            @RequestPart("image") MultipartFile file
    ) {
        try {
            log.info("Adding new product: {}", request.getName());

            UploadDTO s3Upload = s3Service.uploadFile(file);
            log.info("File uploaded to S3. URL={}, KEY={}", s3Upload.getUrl(), s3Upload.getKey());

            request.setImageURL(s3Upload.getUrl());
            request.setImageKey(s3Upload.getKey());

            boolean ok = inventoryService.createProduct(request);

            if (!ok) {
                log.warn("Unable to add new product: {}", request.getName());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Unable to add new product");
            }

            log.info("Product added successfully: {}", request.getName());
            return ResponseEntity.ok(request);

        } catch (Exception e) {
            log.error("Error adding product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<String> deActivateProduct(@PathVariable String id) {
        boolean ok = inventoryService.deactivateProduct(id);

        if (!ok) {
            log.warn("Failed to deactivate product with id={}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to disable product");
        }

        log.info("Product deactivated successfully: {}", id);
        return ResponseEntity.ok("Product disabled successfully");
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<String> activateProduct(@PathVariable String id) {
        boolean ok = inventoryService.activateProduct(id);

        if (!ok) {
            log.warn("Failed to activate product with id={}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to activate product");
        }

        log.info("Product activated successfully: {}", id);
        return ResponseEntity.ok("Product activated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        Optional<InventoryDTO> temp = inventoryService.deleteProduct(id);

        if (temp.isEmpty()) {
            log.warn("Delete failed. Product not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        s3Service.deleteFile(temp.get().getImageKey());
        log.info("Product deleted successfully: {}", id);

        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ProductDTO>> getProductById(@PathVariable() String id) {
        return ResponseEntity.ok(inventoryService.getProductById(id));
    }

    @GetMapping("/top-product")
    public ResponseEntity<List<ProductDTO>> getProductByLimit() {
        return ResponseEntity.ok(inventoryService.getTop3Product());
    }
}
