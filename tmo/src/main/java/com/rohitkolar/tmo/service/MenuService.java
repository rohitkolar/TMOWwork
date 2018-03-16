package com.rohitkolar.tmo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rohitkolar.tmo.model.Menu;

public interface MenuService {
	List<Menu> getAllMenues();
	ResponseEntity<Integer> createMenu(Menu menu);
}
