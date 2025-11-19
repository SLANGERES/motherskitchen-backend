package com.motherskitchen.backend.Service.Order;

import com.motherskitchen.backend.DTO.Order.OrderItemDTO;
import com.motherskitchen.backend.DTO.Order.OrdersCreateDTO;
import com.motherskitchen.backend.DTO.Order.OrdersDTO;
import com.motherskitchen.backend.Models.Address;
import com.motherskitchen.backend.Models.Inventory.Inventory;
import com.motherskitchen.backend.Models.OrderItem;
import com.motherskitchen.backend.Models.Orders;
import com.motherskitchen.backend.Repository.InventoryRepository;
import com.motherskitchen.backend.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository itemRepository;
    private OrdersDTO mapToDTO(Orders order) {
        return OrdersDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .totalAmount(order.getTotal())
                .status(order.getStatus())
                .items(
                        order.getItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                        .itemId(item.getItemId())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build()
                                ).toList()
                )
                .address(
                        Address.builder()
                                .streetAddress(order.getAddress().getStreetAddress())
                                .city(order.getAddress().getCity())
                                .postalcode(order.getAddress().getPostalcode())
                                .build()
                )
                .build();
    }


    @Transactional
    public UUID createOrder(OrdersCreateDTO request) {

        // 1. Build Address Entity
        Address address = Address.builder()
                .streetAddress(request.getAddress().getStreetAddress())
                .city(request.getAddress().getCity())
                .postalcode(request.getAddress().getPostalcode())
                .build();

        // 2. Build Order Items
        List<OrderItem> orderItems = request.getItems().stream().map(i -> {

            Inventory item = itemRepository.findById(i.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            return OrderItem.builder()
                    .itemId(item.getId())
                    .quantity(i.getQuantity())
                    .price(item.getPrice())   // snapshot price from DB
                    .build();

        }).toList();

        // 3. Calculate Total
        double total = orderItems.stream()
                .mapToDouble(oi -> oi.getPrice() * oi.getQuantity())
                .sum();

        // 4. Build Order Entity
        Orders order = Orders.builder()
                .customerId(request.getCustomerId())
                .address(address)
                .items(orderItems)
                .status("PENDING")
                .total(total)
                .build();

        // 5. Save Order
        Orders saveOrder=orderRepository.save(order);
        return saveOrder.getId();
    }

    public List<OrdersDTO> getALlOrders(){
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public List<OrdersDTO> getALlOrdersByStatus(String status ){
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public Optional<OrdersDTO>  getOrderById(String uid){
        Optional<Orders>tempOrder=orderRepository.findById(UUID.fromString(uid));
        return tempOrder.map(this::mapToDTO);
    }
}
