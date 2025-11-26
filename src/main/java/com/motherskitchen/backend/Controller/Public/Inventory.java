package com.motherskitchen.backend.Controller.Public;


import com.motherskitchen.backend.Aws.S3.S3Service;
import com.motherskitchen.backend.Aws.S3.UploadDTO;
import com.motherskitchen.backend.DTO.Inventory.InventoryDTO;
import com.motherskitchen.backend.DTO.Inventory.ProductDTO;
import com.motherskitchen.backend.Service.Inventory.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final InventoryService inventoryService;
    private final S3Service s3Service;

    @GetMapping("/")
    public ResponseEntity<List<ProductDTO>> getAllProduct() {
        return ResponseEntity.ok(inventoryService.getAllActiveProduct());
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> addNewProduct(
            @Valid @RequestPart("product") InventoryDTO request,
            @RequestPart("image") MultipartFile file
    ) {
        try {
            System.out.println("Adding product ..........");
            UploadDTO s3Upload = s3Service.uploadFile(file);

            System.out.println("Generating S3 Url ..........");
            request.setImageURL(s3Upload.getUrl());
            request.setImageKey(s3Upload.getKey());

            boolean ok = inventoryService.createProduct(request);

            if (!ok) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Unable to add new product");
            }
            System.out.println("Added Successfull ..........");

            return ResponseEntity.ok(request); // return product with URL

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<String> deActivateProduct(@PathVariable String id) {
        return inventoryService.deactivateProduct(id)
                ? ResponseEntity.ok("Product disabled successfully")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to disable product");
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<String> activateProduct(@PathVariable String id) {
        return inventoryService.activateProduct(id)
                ? ResponseEntity.ok("Product activated successfully")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to activate product");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        Optional<InventoryDTO> temp = inventoryService.deleteProduct(id);

        if (temp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        s3Service.deleteFile(temp.get().getImageKey());
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ProductDTO>> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(inventoryService.getProductById(id));
    }
}
