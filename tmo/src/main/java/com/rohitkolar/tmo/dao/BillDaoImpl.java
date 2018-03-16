package com.rohitkolar.tmo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.rohitkolar.tmo.model.Bill;
import com.rohitkolar.tmo.model.Order;
import com.rohitkolar.tmo.model.Tax;
import com.rohitkolar.tmo.utility.TMOConstants;

@Repository
public class BillDaoImpl implements BillDao {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Override
	public List<Tax> getAllActiveTaxes() {
		System.out.println("Getting all active taxes");
		
		String sql = "SELECT TAX_ID, TAX_NAME, TAX_DESCRIPTION, TAX_AMOUNT_PER, IS_ACTIVE "
				+ "FROM tmo_tax_master WHERE IS_ACTIVE = (:isActive) AND IS_DELETED = (:isDeleted)";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isActive", TMOConstants.YES);
		paramMap.put("isDeleted", TMOConstants.IS_DELETED_NO);
		List<Tax> taxes = null;
		try {
			taxes = namedParameterJdbcTemplate.query(sql, paramMap, new RowMapper<Tax>() {

				@Override
				public Tax mapRow(ResultSet rs, int arg1) throws SQLException {
					return new Tax(rs.getInt("TAX_ID"), 
							rs.getString("TAX_NAME"), 
							rs.getDouble("TAX_AMOUNT_PER"), 
							rs.getString("IS_ACTIVE"),
							rs.getString("TAX_DESCRIPTION"));
				}
				
			});
			
			System.out.println("Got " + taxes != null ? taxes.size(): 0 + " active taxes");
		} catch(EmptyResultDataAccessException e) {
			System.out.println("Empty ResultSet");
		}
		return taxes;
	}
	
	@Override
	public int createBill(Bill bill) {
		System.out.println("Creating bill for orderId: " + bill.getOrderId());
		
		String sql = "INSERT INTO tmo_bill "
				+ "(BILL_ID, ORDER_ID, BILL_NUMBER, BILL_DATE, BILL_AMOUNT, STATUS, CREATED_BY, CREATED_ON)"
				+ "VALUES(:billId, :orderId, :billNo, NOW(), :billAmount, :status, :createdBy, NOW())";
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		
		paramMap.addValue("billId", bill.getBillId());
		paramMap.addValue("orderId", bill.getOrderId());
		paramMap.addValue("billNo", bill.getBillNumber());
		paramMap.addValue("billAmount", bill.getBillAmount());
		paramMap.addValue("status", bill.getStatus());
		paramMap.addValue("createdBy", bill.getCreatedBy());
		
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		int updatedRows = namedParameterJdbcTemplate.update(sql, paramMap, keyHolder);
		
		return keyHolder.getKey().intValue();
		
	}
	
	@Override
	public int createBillTaxMap(int billId, int taxId, double amount, int createdBy) {
		System.out.println("Creating bill taxes map for billId: " + billId);
		
		String sql = "INSERT INTO tmo_bill_tax_map "
				+ "(BILL_ID, TAX_ID, AMOUNT, CREATED_BY, CREATED_ON)"
				+ "VALUES(:billId, :taxId, :amount, :createdBy, NOW())";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("billId", billId);
		paramMap.put("taxId", taxId);
		paramMap.put("amount", amount);
		paramMap.put("createdBy", createdBy);
		
		return namedParameterJdbcTemplate.update(sql, paramMap);
	}
	
	@Override
	public String getLastBillNo() {
		System.out.println("getting last bill no");
		
		String sql="SELECT BILL_NUMBER FROM tmo_bill WHERE BILL_ID = (SELECT MAX(BILL_ID) FROM tmo_bill WHERE IS_DELETED = (:isDeleted))";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isDeleted", TMOConstants.IS_DELETED_NO);
		String billNo = null;
		
		try {
			billNo = namedParameterJdbcTemplate.queryForObject(sql, paramMap, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return rs.getString("BILL_NUMBER");
				}
			});
		} catch(EmptyResultDataAccessException e) {
			System.out.println("Empty result set");
		}
		
		return billNo;
	}
	
	@Override
	public int updateBillStatusByOrderId(int orderId, String status, int modifiedBy) {
		System.out.println("Updating status of bills where orderId is : " + orderId + " to: " + status);
		
		String sql = "UPDATE tmo_bill SET STATUS = (:status), MODIFIED_BY = (:modifiedBy), MODIFIED_ON = NOW() WHERE ORDER_ID = (:orderId)";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("status", status);
		paramMap.put("modifiedBy", modifiedBy);
		paramMap.put("orderId", orderId);
		
		int updatedRows = 0;
		
		updatedRows = namedParameterJdbcTemplate.update(sql, paramMap);
		
		System.out.println("updated " + updatedRows + " rows while updating status of bills where orderId is : " + orderId + " to: " + status);
		
		return updatedRows;
	}
}
