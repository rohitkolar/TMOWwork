package com.rohitkolar.tmo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.rohitkolar.tmo.model.Menu;
import com.rohitkolar.tmo.utility.TMOConstants;

@Repository
public class MenuDaoImpl implements MenuDao {
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<Menu> getAllMenues() {
		System.out.println("Getting all Menues");
		
		final String sql = "SELECT MENU_ID, MENU_NAME, PARENT_MENU_ID, IS_AVAILABLE, PRICE FROM tmo_menu";

		List<Menu> menues = new ArrayList<Menu>();
		try {
			menues = namedParameterJdbcTemplate.query(sql, new RowMapper<Menu>() {
				@Override
				public Menu mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					Menu menu = new Menu();
					menu.setMenuId(rs.getInt("MENU_ID"));
					menu.setMenuName(rs.getString("MENU_NAME"));
					menu.setIsAvailable(rs.getString("IS_AVAILABLE"));
					menu.setPrice(rs.getDouble("PRICE"));
					return menu;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			System.out.println("Empty result set");
		}
		return menues;
	}
	
	@Override
	public int createMenue(Menu menu) {
		System.out.println("Creating menu with name: " + menu + " and : " + menu.getMenuId());
		
		String sql = "INSERT INTO tmo_menu (MENU_NAME, PARENT_MENU_ID, IS_AVAILABLE, PRICE, CREATED_BY, CREATED_ON) VALUES (:menuName, :parentMenuId, :isAvailable, :price, :createdBy, NOW())";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("menuName", menu.getMenuName());
		paramMap.put("parentMenuId", menu.getParentMenuId());
		paramMap.put("isAvailable", menu.getIsAvailable());
		paramMap.put("price", menu.getPrice());
		paramMap.put("createdBy", menu.getCreatedBy());
		
		int updatedRows = 0;
		
		updatedRows = namedParameterJdbcTemplate.update(sql, paramMap);
		System.out.println("Successfully created menu with name: " + menu + " and : " + menu.getMenuId());
		
		return updatedRows;
	}
	
	@Override
	public List<Menu> getMenuesByOrderId(int orderId) {
		System.out.println("Getting all Menues of orderId: " + orderId);
		
		final String sql = "SELECT MN.MENU_ID, MN.MENU_NAME, MN.PARENT_MENU_ID, MN.IS_AVAILABLE, MN.PRICE, MO.QUANTITIY "
				+ "FROM tmo_menu as MN, tmo_order_menu_map as MO "
				+ "WHERE MN.MENU_ID = MO.MENU_ID AND MO.IS_CANCELED = (:isCanceled) AND MO.ORDER_ID = (:orderId) AND MN.IS_DELETED = (:isDeleted) AND MO.IS_DELETED = (:isDeleted)";
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isCanceled", TMOConstants.NO);
		paramMap.put("orderId", orderId);
		paramMap.put("isDeleted", TMOConstants.IS_DELETED_NO);
		
		List<Menu> menues = new ArrayList<Menu>();
		try {
			menues = namedParameterJdbcTemplate.query(sql, paramMap, new RowMapper<Menu>() {
				@Override
				public Menu mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					Menu menu = new Menu();
					menu.setMenuId(rs.getInt("MENU_ID"));
					menu.setMenuName(rs.getString("MENU_NAME"));
					menu.setIsAvailable(rs.getString("IS_AVAILABLE"));
					menu.setPrice(rs.getDouble("PRICE"));
					menu.setQuantity(rs.getInt("QUANTITIY"));
					return menu;
				}
			});
			System.out.println("Got " + (menues != null ? menues.size() : 0) + " menues for orderId: " + orderId);
		} catch (EmptyResultDataAccessException e) {
			System.out.println("Empty result set");
		}
		return menues;
	}

}
