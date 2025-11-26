package com.motherskitchen.backend.Controller.Public;

import com.motherskitchen.backend.DTO.Email.OrderEmail;
import com.motherskitchen.backend.DTO.Order.OrdersCreateDTO;
import com.motherskitchen.backend.DTO.Order.OrdersDTO;
import com.motherskitchen.backend.Security.Jwt.AuthUtil;
import com.motherskitchen.backend.Service.Email.EmailService;
import com.motherskitchen.backend.Service.Order.OrderService;
import com.motherskitchen.backend.Service.User.UserService;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class Orders {
    //Dependency Injection
    private final OrderService orderService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;
    private final AuthUtil authUtil;

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrdersCreateDTO request) {

        try {
            // Create order
            OrdersDTO order = orderService.createOrder(request);

            // Build and send email event
            OrderEmail orderEmail = OrderEmail.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .order(order)
                    .build();

            emailService.orderConform(orderEmail);

            // Return created order ID
            return ResponseEntity.status(HttpStatus.CREATED).body(order.getId());

        } catch (Exception e) {
            // Log the error (always log backend errors)
            System.err.println("Order creation failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order. Please try again later.");
        }
    }

    @GetMapping("/all-orders")
    public ResponseEntity<List<OrdersDTO>> getAllOrders(
            @RequestParam(required = false, defaultValue = "all") String status) {

        if (status.equalsIgnoreCase("all")) {
            return ResponseEntity.ok(orderService.getALlOrders());
        } else {
            return ResponseEntity.ok(orderService.getALlOrdersByStatus(status.toUpperCase()));
        }
    }
    @PatchMapping("/status/{id}")
    public ResponseEntity<String>completeOrder(@PathVariable("id")String id,@RequestParam String status){

        return orderService.updateOrderStatus(id,status)
                ? ResponseEntity.ok("Order status changed to completed")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to change status");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String>deleteOrder(@PathVariable("id")String id){
        if(!orderService.DeleteOrder(id)){
            return new ResponseEntity<>("Unable to delete",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("",HttpStatus.OK);
    }
}
