package com.increff.pos.controller;

import com.increff.pos.model.InfoData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class InitApiController extends AbstractUiController {

	@Autowired
	private UserService service;
	@Autowired
	private InfoData info;
	@Value(("${app.supervisorEmail}"))
	private String supervisorEmail;

	@ApiOperation(value = "Signing  up to the application")
	@RequestMapping(path = "/site/user/new", method = RequestMethod.POST)
	public void signUp(@RequestBody UserForm form) throws ApiException {
		if(form.getEmail() == supervisorEmail){
			UserPojo p = convert(form);
			p.setRole("admin");
			service.add(p);
		}else {
			UserPojo p = convert(form);
			p.setRole("standard");
			service.add(p);
		}
	}

	private static UserPojo convert(UserForm f) {
		UserPojo p = new UserPojo();
		p.setEmail(f.getEmail());
		p.setPassword(f.getPassword());
		return p;
	}

}
