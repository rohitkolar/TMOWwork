package com.rohitkolar.tmo.dao;

import com.rohitkolar.tmo.model.Menu;
import com.rohitkolar.tmo.model.Order;

public interface OrderDao {
	int createOrder(Order order);
	int createOrderMenuesMap(int orderId, Menu menu);
	String getLastOrderTxnNo();
	int updateOrderStatus(Order order);
	Order getOrderDetailsByOrderId(int orderId);
}
