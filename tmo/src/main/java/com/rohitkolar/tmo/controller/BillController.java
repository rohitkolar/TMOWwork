package com.rohitkolar.tmo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rohitkolar.tmo.model.Bill;
import com.rohitkolar.tmo.model.Order;
import com.rohitkolar.tmo.service.BillService;

@RestController
@RequestMapping("/tmo/api/bill")
public class BillController {
	
	@Autowired
	BillService billService; 
	
	@RequestMapping(value="/generateBill", method=RequestMethod.POST)
	public ResponseEntity<Bill> generateBill(@RequestBody Order order) {
		return billService.generateBill(order.getOrderId(), order.getCreatedBy());
	}
}
