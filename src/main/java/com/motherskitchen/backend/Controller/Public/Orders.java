//package com.motherskitchen.backend.Controller.Public;
//
//import com.motherskitchen.backend.DTO.Order.OrdersCreateDTO;
//import com.motherskitchen.backend.Service.Order.OrderService;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Null;
//import jakarta.websocket.server.PathParam;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/orders")
//@RequiredArgsConstructor
//public class Orders {
//    //Dependency Injection
//    private final OrderService orderService;
//
//    @PostMapping("/")
//    public ResponseEntity<UUID>createOrder(@Valid @RequestBody OrdersCreateDTO request){
//        return new ResponseEntity<>(orderService.createOrder(request),HttpStatus.CREATED);
//    }
//
//    @GetMapping("/my")
//    public ResponseEntity<String>myOrder(){
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String>cancelorder(@PathParam("id")UUID id){
//
//    }
//}
