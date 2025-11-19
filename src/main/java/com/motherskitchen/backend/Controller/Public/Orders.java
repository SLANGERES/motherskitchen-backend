package com.motherskitchen.backend.Controller.Public;

import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class Orders {


    @PostMapping("/")
    public ResponseEntity<String>createOrder(){

    }

    @GetMapping("/my")
    public ResponseEntity<String>myOrder(){

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>cancelorder(@PathParam("id")UUID id){

    }
}
