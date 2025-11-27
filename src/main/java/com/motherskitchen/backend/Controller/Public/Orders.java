package com.motherskitchen.backend.Controller.Public;

import com.motherskitchen.backend.DTO.Email.OrderEmail;
import com.motherskitchen.backend.DTO.Order.OrdersCreateDTO;
import com.motherskitchen.backend.DTO.Order.OrdersDTO;
import com.motherskitchen.backend.Service.Email.EmailService;
import com.motherskitchen.backend.Service.Order.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class Orders {

    private static final Logger log = LoggerFactory.getLogger(Orders.class);

    private final OrderService orderService;
    private final EmailService emailService;

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrdersCreateDTO request) {
        try {
            log.info("Incoming order request for {}", request.getEmail());

            OrdersDTO order = orderService.createOrder(request);

            OrderEmail orderEmail = OrderEmail.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .order(order)
                    .build();

            emailService.orderConform(orderEmail);

            log.info("Order created successfully. Order ID: {}", order.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(order.getId());
        } catch (Exception e) {
            log.error("Order creation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to create order right now. Please retry later.");
        }
    }

    @GetMapping("/all-orders")
    public ResponseEntity<List<OrdersDTO>> getAllOrders(
            @RequestParam(defaultValue = "all") String status) {

        List<OrdersDTO> result = status.equalsIgnoreCase("all")
                ? orderService.getALlOrders()
                : orderService.getALlOrdersByStatus(status.toUpperCase());

        log.debug("Returning {} orders with status {}", result.size(), status);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        boolean ok = orderService.updateOrderStatus(id, status);
        log.info("order Status changed successfully {} , Status {}",id,status);

        return ok
                ? ResponseEntity.ok("Order status updated")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to update status");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {

        boolean ok = orderService.DeleteOrder(id);
        log.info("order deleted successfully {}",id);
        return ok
                ? ResponseEntity.ok("Order deleted")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to delete order");
    }
}
