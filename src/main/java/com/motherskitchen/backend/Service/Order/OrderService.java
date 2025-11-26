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


    private String fetchItemName(UUID itemId) {
        return itemRepository.findById(itemId)
                .map(Inventory::getName)
                .orElse("Unknown Item");
    }

    private OrdersDTO mapToDTO(Orders order) {
        return OrdersDTO.builder()
                .id(order.getId())
                .name(order.getName())
                .email(order.getEmail())
                .phone(order.getPhone())
                .totalAmount(order.getTotal())
                .status(order.getStatus())
                .deliveryDate(order.getDeliveryDate())
                .items(
                        order.getItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                        .itemId(item.getItemId())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .name(fetchItemName(item.getItemId()))
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
                .Notes(order.getNotes())
                .build();
    }


    /**
     * Creates a new order. NOTE: This service assumes there is infinite inventory
     * since the Inventory model does not track stock quantity.
     */
    @Transactional
    public OrdersDTO createOrder(OrdersCreateDTO request) {

        Address address = Address.builder()
                .streetAddress(request.getAddress().getStreetAddress())
                .city(request.getAddress().getCity())
                .postalcode(request.getAddress().getPostalcode())
                .build();

        List<OrderItem> orderItems = request.getItems().stream().map(i -> {

            // Fetch item to ensure it exists and get its current price
            Inventory item = itemRepository.findById(i.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + i.getItemId()));

            // NO INVENTORY CHECK OR DEDUCTION HERE (as per your request)

            return OrderItem.builder()
                    .itemId(item.getId())
                    .quantity(i.getQuantity())
                    .price(item.getPrice()) // Use the current price from the Inventory model
                    .build();

        }).toList();

        double total = orderItems.stream()
                .mapToDouble(oi -> oi.getPrice() * oi.getQuantity())
                .sum();

        Orders order = Orders.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(address)
                .items(orderItems)
                .deliveryDate(request.getDeliveryDate())
                .status("PENDING")
                .total(total)
                .Notes(request.getNotes())
                .build();

        // CRITICAL FIX: Set parent for bidirectional mapping
        orderItems.forEach(oi -> oi.setOrder(order));

        Orders savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }

    // --- Order Retrieval Methods ---

    public List<OrdersDTO> getALlOrders(){
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public List<OrdersDTO> getALlOrdersByStatus(String status){
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Optional<OrdersDTO> getOrderById(String uid){
        try {
            Optional<Orders> tempOrder = orderRepository.findById(UUID.fromString(uid));
            return tempOrder.map(this::mapToDTO);
        } catch (IllegalArgumentException e) {
            // Handle malformed UUID string gracefully
            return Optional.empty();
        }
    }

    // --- Order Status Update Methods ---

    /**
     * Private helper method to update order status by ID.
     */
    private boolean updateOrderStatus(String id, String newStatus) {
        UUID orderUuid;
        try {
            orderUuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            // Invalid UUID format provided
            return false;
        }

        Optional<Orders> tempOrder = orderRepository.findById(orderUuid);
        if(tempOrder.isEmpty()){
            return false;
        }

        Orders orders = tempOrder.get();
        orders.setStatus(newStatus);
        orderRepository.save(orders);
        return true;
    }

    public boolean acceptOrderById(String id){
        return updateOrderStatus(id, "ACCEPT");
    }

    public boolean completeOrderById(String id){
        return updateOrderStatus(id, "COMPLETE");
    }
}