package com.rohitkolar.tmo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.rohitkolar.tmo.dao.MenuDao;
import com.rohitkolar.tmo.dao.OrderDao;
import com.rohitkolar.tmo.model.Menu;
import com.rohitkolar.tmo.model.Order;
import com.rohitkolar.tmo.utility.TMOConstants;
import com.rohitkolar.tmo.utility.TMOValidator;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderDao orderDao;
	
	@Autowired
	MenuDao menuDao;
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.REQUIRED, readOnly=false, timeout=100, rollbackFor=Exception.class)
	@Override
	public ResponseEntity<Order> placeAnOrder(Order order) {
		ResponseEntity<Order> response = null;
		
		if(order == null || order.getMenues() == null || order.getTableNo() == 0){
			response = new ResponseEntity<Order>(order, HttpStatus.BAD_REQUEST);
			return response;
		}
		
		String ordTxnNo = orderDao.getLastOrderTxnNo();
		
		// ORDER TXN NO: ORD<increamented number>
		if(TMOValidator.isEmpty(ordTxnNo)) {
			ordTxnNo = TMOConstants.ORDER_TXN_NO_PRE + "1";
		} else {
			String ordTxnNoParts[] = ordTxnNo.split(TMOConstants.ORDER_TXN_NO_PART);
			if(ordTxnNoParts != null && ordTxnNoParts.length > 0) {
				int orderNo = Integer.parseInt(ordTxnNoParts[ordTxnNoParts.length-1]);
				ordTxnNo = TMOConstants.ORDER_TXN_NO_PRE + String.valueOf((orderNo + 1));
			} else {
				ordTxnNo = TMOConstants.ORDER_TXN_NO_PRE + TMOConstants.ORDER_TXN_NO_ERROR_PART + "1";
			}
		}
		
		order.setTxnOrderId(ordTxnNo);
		order.setOrderStatus(TMOConstants.ORDER_STATUS_SUBMITTED);
		
		if(orderDao.createOrder(order) <= 0 || order.getOrderId() == 0) {
			response = new ResponseEntity<Order>(order, HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
		
		int updates = 0;
		for(Menu menu: order.getMenues()) {
			menu.setQuantity(menu.getQuantity() != 0 ? menu.getQuantity() : 1);
			updates = orderDao.createOrderMenuesMap(order.getOrderId(), menu);
			if(updates <= 0)
				break;
		}
		
		if(updates == 0) {
			response = new ResponseEntity<Order>(order, HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
		response = new ResponseEntity<Order>(order, HttpStatus.OK);
		
		return response;
	}

	@Override
	public ResponseEntity<Integer> updateOrderStatus(Order order) {
		return new ResponseEntity<Integer>(orderDao.updateOrderStatus(order), HttpStatus.OK);
	}
	
	@Override
	public Order getOrderWithOrderedMenues(int orderId) {
		Order order = orderDao.getOrderDetailsByOrderId(orderId);
		if(order != null)
			order.setMenues(menuDao.getMenuesByOrderId(order.getOrderId()));
		
		return order;
	}
}
