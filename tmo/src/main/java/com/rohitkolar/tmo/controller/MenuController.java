package com.rohitkolar.tmo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rohitkolar.tmo.model.Menu;
import com.rohitkolar.tmo.service.MenuService;

@RestController
@RequestMapping("/tmo/api/menu")
public class MenuController {
	
	@Autowired
	MenuService menuService;
	
	@RequestMapping(value="/getAllMenues", method=RequestMethod.GET)
	public List<Menu> getAllMenues() {
		return menuService.getAllMenues();
	}
	
	@RequestMapping(value="/createMenu", method=RequestMethod.POST)
	public ResponseEntity<Integer> createMenu(@RequestBody Menu menu) {
		return menuService.createMenu(menu);
	}
}
