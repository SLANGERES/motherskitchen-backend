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
                .ordersType(order.getOrderType())
                .deliveryDate(order.getDeliveryDay())
                .payment(order.getPayment())
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
                .address(order.getAddress() == null ? null : Address.builder()
                        .streetAddress(order.getAddress().getStreetAddress())
                        .city(order.getAddress().getCity())
                        .postalcode(order.getAddress().getPostalcode())
                        .build())
                .Notes(order.getNotes())
                .build();
    }


    // ---------------------------------------------------
    //                    CREATE ORDER
    // ---------------------------------------------------

    @Transactional
    public OrdersDTO createOrder(OrdersCreateDTO request) {

        // ---- HANDLE ADDRESS ----
        Address address = null;

        if ("Delivery".equalsIgnoreCase(request.getOrderType())) {

            if (request.getAddress() == null) {
                throw new RuntimeException("Address is required for Delivery orders");
            }

            address = Address.builder()
                    .streetAddress(request.getAddress().getStreetAddress())
                    .city(request.getAddress().getCity())
                    .postalcode(request.getAddress().getPostalcode())
                    .build();
        }


        // ---- ITEMS ----
        List<OrderItem> orderItems = request.getItems().stream().map(i -> {

            Inventory item = itemRepository.findById(i.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + i.getItemId()));

            return OrderItem.builder()
                    .itemId(item.getId())
                    .quantity(i.getQuantity())
                    .price(item.getPrice())
                    .build();

        }).toList();

        // Calculate items total
        double itemsTotal = orderItems.stream()
                .mapToDouble(oi -> oi.getPrice() * oi.getQuantity())
                .sum();

        // Add delivery charge from frontend
        double finalTotal = itemsTotal + request.getDeliveryCharge();


        // ---- ORDER ENTITY ----
        Orders order = Orders.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(address)
                .items(orderItems)
                .deliveryDay(request.getDay())            // Friday/Sat/Sun
                .orderType(request.getOrderType())        // Pickup / Delivery
                .deliveryCharge(request.getDeliveryCharge())
                .status("PENDING")
                .payment(request.getPayment())
                .total(finalTotal)
                .notes(request.getNotes())
                .build();

        // Set relation for JPA
        orderItems.forEach(oi -> oi.setOrder(order));

        Orders savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }


    // ---------------------------------------------------
    //               ORDER RETRIEVAL METHODS
    // ---------------------------------------------------

    public List<OrdersDTO> getALlOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<OrdersDTO> getALlOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Optional<OrdersDTO> getOrderById(String uid) {
        try {
            Optional<Orders> tempOrder = orderRepository.findById(UUID.fromString(uid));
            return tempOrder.map(this::mapToDTO);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    // ---------------------------------------------------
    //               ORDER STATUS + DELETE
    // ---------------------------------------------------

    public boolean updateOrderStatus(String id, String newStatus) {
        UUID orderUuid;

        try {
            orderUuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return false;
        }

        Optional<Orders> tempOrder = orderRepository.findById(orderUuid);
        if (tempOrder.isEmpty()) return false;

        Orders orders = tempOrder.get();
        orders.setStatus(newStatus);

        orderRepository.save(orders);
        return true;
    }

    public boolean DeleteOrder(String id) {
        if (!orderRepository.existsById(UUID.fromString(id))) {
            return false;
        }
        orderRepository.deleteById(UUID.fromString(id));
        return true;
    }
}
