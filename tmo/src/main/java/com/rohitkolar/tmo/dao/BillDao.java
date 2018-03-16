package com.rohitkolar.tmo.dao;

import java.util.List;

import com.rohitkolar.tmo.model.Bill;
import com.rohitkolar.tmo.model.Tax;

public interface BillDao {

	List<Tax> getAllActiveTaxes();

	int createBill(Bill bill);

	String getLastBillNo();

	int createBillTaxMap(int billId, int taxId, double amount, int createdBy);

	int updateBillStatusByOrderId(int orderId, String status, int modifiedBy);
	
}
