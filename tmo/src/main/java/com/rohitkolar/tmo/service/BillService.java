package com.rohitkolar.tmo.service;

import org.springframework.http.ResponseEntity;

import com.rohitkolar.tmo.model.Bill;

public interface BillService {

	ResponseEntity<Bill> generateBill(int orderId, int createdBy);

}
