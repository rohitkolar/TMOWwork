package com.rohitkolar.tmo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.rohitkolar.tmo.model.Menu;
import com.rohitkolar.tmo.model.Order;
import com.rohitkolar.tmo.utility.TMOConstants;

@Repository
public class OrderDaoImpl implements OrderDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Override
	public int createOrder(Order order) {
		System.out.println("Creating an order");
		
		String sql = "INSERT INTO tmo_order (TXN_ORDER_NO, TABLE_NO, ORDER_TIME, ORDER_STATUS, CREATED_ON) VALUES (:txnOrderId, :tableNo, NOW(), :orderStatus, NOW())";
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("txnOrderId", order.getTxnOrderId());
		paramMap.addValue("tableNo", order.getTableNo());
		paramMap.addValue("orderStatus", order.getOrderStatus());
		
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		
		int updatedRows = 0;
		
		updatedRows = namedParameterJdbcTemplate.update(sql, paramMap, keyHolder);
		order.setOrderId(keyHolder.getKey().intValue());
		System.out.println("Successfully created order");
		
		return updatedRows;
	}

	@Override
	public int createOrderMenuesMap(int orderId, Menu menu) {
		System.out.println("Creating order menu map where orderId: " + orderId + " menuId: " + menu.getMenuId());
		
		String sql = "INSERT INTO tmo_order_menu_map (ORDER_ID, MENU_ID, QUANTITIY, CREATED_ON) VALUES (:orderId, :menuId, :quantity, NOW())";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("orderId", orderId);
		paramMap.put("menuId", menu.getMenuId());
		paramMap.put("quantity", menu.getQuantity());
		
		int updatedRows = 0;
		
		updatedRows = namedParameterJdbcTemplate.update(sql, paramMap);
		System.out.println("Successfully created order menu map where orderId: " + orderId + " menuId: " + menu.getMenuId());
		
		return updatedRows;
	}

	@Override
	public String getLastOrderTxnNo() {
		System.out.println("getting last order Txn no");
		
		String sql="SELECT TXN_ORDER_NO FROM tmo_order WHERE ORDER_ID = (SELECT MAX(ORDER_ID) FROM tmo_order WHERE IS_DELETED = (:isDeleted))";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isDeleted", TMOConstants.IS_DELETED_NO);
		String orderTxnId = null;
		
		try {
			orderTxnId = namedParameterJdbcTemplate.queryForObject(sql, paramMap, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return rs.getString("TXN_ORDER_NO");
				}
			});
		} catch(EmptyResultDataAccessException e) {
			System.out.println("Empty result set");
		}
		
		return orderTxnId;
	}
	
	@Override
	public int updateOrderStatus(Order order) {
		System.out.println("Updating status of orderId: " + order.getOrderId() + " to: " + order.getOrderStatus());
		
		String sql = "UPDATE tmo_order SET ORDER_STATUS = (:orderStatus), MODIFIED_BY = (:modifiedBy), MODIFIED_ON = NOW() WHERE ORDER_ID = (:orderId) AND IS_DELETED=(:isDeleted)";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("orderStatus", order.getOrderStatus());
		paramMap.put("modifiedBy", order.getModifiedBy());
		paramMap.put("orderId", order.getOrderId());
		paramMap.put("isDeleted", TMOConstants.IS_DELETED_NO);
		
		int updatedRows = 0;
		
		updatedRows = namedParameterJdbcTemplate.update(sql, paramMap);
		System.out.println("Successfully updated status of orderId: " + order.getOrderId() + " to: " + order.getOrderStatus());
		
		return updatedRows;
	}
	
	@Override
	public Order getOrderDetailsByOrderId(int orderId) {
		System.out.println("Getting order details by orderId: " + orderId);
		
		String sql = "SELECT ORDER_ID, TXN_ORDER_NO, TABLE_NO, ORDER_TIME, ORDER_STATUS, CREATED_ON FROM tmo_order WHERE ORDER_ID = (:orderId) AND IS_DELETED = (:isDeleted)";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("orderId", orderId);
		paramMap.put("isDeleted", TMOConstants.IS_DELETED_NO);
		
		Order order = null;
		try {
			order = namedParameterJdbcTemplate.queryForObject(sql, paramMap, new RowMapper<Order>(){

				@Override
				public Order mapRow(ResultSet rs, int arg1) throws SQLException {
					Order ord = new Order();
					
					ord.setOrderId(rs.getInt("ORDER_ID"));
					ord.setTxnOrderId(rs.getString("TXN_ORDER_NO"));
					ord.setTableNo(rs.getInt("TABLE_NO"));
					ord.setOrderTime(rs.getDate("ORDER_TIME"));
					ord.setOrderStatus(rs.getString("ORDER_STATUS"));
					ord.setCreatedOn(rs.getDate("CREATED_ON"));
					return ord;
				}
				
			});
			
			System.out.println("Got order details for orderId: " + orderId);
		} catch(EmptyResultDataAccessException e) {
			System.out.println("Empty result set");
		}
		
		return order;
	}
}
