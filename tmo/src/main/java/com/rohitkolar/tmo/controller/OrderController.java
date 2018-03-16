package com.rohitkolar.tmo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rohitkolar.tmo.model.Order;
import com.rohitkolar.tmo.service.OrderService;

@RestController
@RequestMapping("/tmo/api")
public class OrderController {
	
	@Autowired
	OrderService orderService; 
	
	@RequestMapping(value="/placeAnOrder", method=RequestMethod.POST)
	public ResponseEntity<Order> placeAnOrder(@RequestBody Order order) {
		return orderService.placeAnOrder(order);
	}
	
	@RequestMapping(value="/updateOrderStatus", method=RequestMethod.PUT)
	public ResponseEntity<Integer> updateOrderStatus(@RequestBody Order order) {
		return orderService.updateOrderStatus(order);
	}
}
