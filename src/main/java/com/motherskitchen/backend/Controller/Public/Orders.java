package com.motherskitchen.backend.Controller.Public;

import com.motherskitchen.backend.DTO.Email.OrderEmail;
import com.motherskitchen.backend.DTO.Order.OrdersCreateDTO;
import com.motherskitchen.backend.DTO.Order.OrdersDTO;
import com.motherskitchen.backend.DTO.User.UserDTO;
import com.motherskitchen.backend.Security.Jwt.AuthUtil;
import com.motherskitchen.backend.Service.Order.OrderService;
import com.motherskitchen.backend.Service.User.UserService;
import com.motherskitchen.backend.Util.MessageQueue.EmailProducer;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class Orders {
    //Dependency Injection
    private final OrderService orderService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailProducer emailProducer;
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

            emailProducer.sendOrderMail(orderEmail);

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
    @PostMapping("/status/{id}/accept")
    public ResponseEntity<String>acceptOrder(@PathVariable("id")String id){

        return orderService.acceptOrderById(id)
                ? ResponseEntity.ok("Order status changed to accepted")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to accept order");
    }
    @PostMapping("/status/{id}/complete")
    public ResponseEntity<String>completeOrder(@PathVariable("id")String id){

        return orderService.completeOrderById(id)
                ? ResponseEntity.ok("Order status changed to completed")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to chnage status");
    }
}
