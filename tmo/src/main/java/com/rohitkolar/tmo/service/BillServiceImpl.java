package com.rohitkolar.tmo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.rohitkolar.tmo.dao.BillDao;
import com.rohitkolar.tmo.dao.MenuDao;
import com.rohitkolar.tmo.dao.OrderDao;
import com.rohitkolar.tmo.model.Bill;
import com.rohitkolar.tmo.model.Menu;
import com.rohitkolar.tmo.model.Order;
import com.rohitkolar.tmo.model.Tax;
import com.rohitkolar.tmo.utility.TMOConstants;
import com.rohitkolar.tmo.utility.TMOValidator;

@Service
public class BillServiceImpl implements BillService {
	
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	MenuDao menuDao;
	
	@Autowired
	BillDao billDao;
	
	@Autowired
	OrderService orderService;
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.REQUIRED, readOnly=false, timeout=100, rollbackFor=Exception.class)
	@Override
	public ResponseEntity<Bill> generateBill(int orderId, int createdBy) {
		System.out.println("Creating bill for orderId: " + orderId);
		
		if(orderId == 0)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		// Get order details	
		Order order = orderService.getOrderWithOrderedMenues(orderId);
		
		if(order == null || order.getMenues() == null)
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		
		// calculate total amount
		double orderTotalAmt = getOrderTotalAmt(order.getMenues());
		System.out.println("Calculated order amount: " + orderTotalAmt);
		double totalTaxAmt = 0;
		// get taxes
		
		List<Tax> activeTaxes = billDao.getAllActiveTaxes();
		
		// calculate total tax
		if(activeTaxes != null && !activeTaxes.isEmpty())
			totalTaxAmt = getTotalTaxAmount(orderTotalAmt, activeTaxes);

		System.out.println("Total tax amount: " + totalTaxAmt);
		// create bill
		double totalBillAmount = orderTotalAmt + totalTaxAmt;
		System.out.println("Total bill amount: " + totalBillAmount);
		
		String lastBillNo = billDao.getLastBillNo();
		String newBillNO = null;
		// ORDER TXN NO: ORD<increamented number>
		if(TMOValidator.isEmpty(lastBillNo)) {
			newBillNO = TMOConstants.ORDER_TXN_NO_PRE + "1";
		} else {
			String billNoParts[] = lastBillNo.split(TMOConstants.ORDER_TXN_NO_PART);
			if(billNoParts != null && billNoParts.length > 0) {
				int billNo = Integer.parseInt(billNoParts[billNoParts.length-1]);
				newBillNO = TMOConstants.ORDER_TXN_NO_PRE + String.valueOf((billNo + 1));
			} else {
				newBillNO = TMOConstants.ORDER_TXN_NO_PRE + TMOConstants.ORDER_TXN_NO_ERROR_PART + "1";
			}
		}
		System.out.println("Generated bill no.: " + newBillNO);
		Bill bill = new Bill();
		bill.setOrderId(orderId);
		bill.setBillNumber(newBillNO);
		bill.setBillAmount(totalBillAmount);
		bill.setCreatedBy(createdBy);
		bill.setStatus(TMOConstants.BILL_STATUS_GENERATED);
		
		// Mark previous bills of the orderId to 'R' - rejected
		billDao.updateBillStatusByOrderId(orderId, TMOConstants.BILL_STATUS_REJECTED, createdBy);
		int billId = billDao.createBill(bill);
		bill.setBillId(billId);
		bill.setTaxes(activeTaxes);
		// create bill tax map
		if(billId > 0 && activeTaxes != null && activeTaxes.size() > 0) {
			for(Tax tax: activeTaxes)
				billDao.createBillTaxMap(billId, tax.getTaxId(), tax.getAmount(), createdBy);
		}
		
		if(billId > 0) {
			System.out.println("Bill created successfully.");
			return new ResponseEntity<Bill>(bill, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
	private double getOrderTotalAmt(List<Menu> orderedMenues) {
		double orderTotalAmt = 0;
		
		for(Menu orderedMenu: orderedMenues) {
			orderTotalAmt = orderTotalAmt + (orderedMenu.getPrice()*orderedMenu.getQuantity());
		}
		
		return orderTotalAmt;
	}
	
	private double getTotalTaxAmount(double orderTotalAmt, List<Tax> taxes) {
		double totalTaxAmt = 0;
		
		for(Tax tax: taxes) {
			double calculatedTax = ((orderTotalAmt * tax.getTaxPercent())/100);
			totalTaxAmt = totalTaxAmt + calculatedTax;
			tax.setAmount(totalTaxAmt);
		}
		
		return totalTaxAmt;
	}
}
