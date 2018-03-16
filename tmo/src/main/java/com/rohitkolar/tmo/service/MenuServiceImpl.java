package com.rohitkolar.tmo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rohitkolar.tmo.dao.MenuDao;
import com.rohitkolar.tmo.model.Menu;

@Service
public class MenuServiceImpl implements MenuService {
	
	@Autowired
	MenuDao menuDao;
	
	@Override
	public List<Menu> getAllMenues() {
		return menuDao.getAllMenues();
	}

	@Override
	public ResponseEntity<Integer> createMenu(Menu menu) {
		return new ResponseEntity<Integer>(menuDao.createMenue(menu), HttpStatus.OK);
	}
}
