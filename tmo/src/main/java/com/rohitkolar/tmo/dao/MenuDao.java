package com.rohitkolar.tmo.dao;

import java.util.List;

import com.rohitkolar.tmo.model.Menu;

public interface MenuDao {
	List<Menu> getAllMenues();

	int createMenue(Menu menu);

	List<Menu> getMenuesByOrderId(int orderId);
}
