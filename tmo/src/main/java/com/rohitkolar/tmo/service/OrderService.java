package com.rohitkolar.tmo.service;

import org.springframework.http.ResponseEntity;

import com.rohitkolar.tmo.model.Order;

public interface OrderService {
	ResponseEntity<Order> placeAnOrder(Order order);
	ResponseEntity<Integer> updateOrderStatus(Order order);
	Order getOrderWithOrderedMenues(int orderId);
}
