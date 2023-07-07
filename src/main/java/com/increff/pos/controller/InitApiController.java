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

//	@ApiOperation(value = "Initializes application")
//	@RequestMapping(path = "/site/init", method = RequestMethod.GET)
//	public ModelAndView showPage(UserForm form) throws ApiException {
//		info.setMessage("");
//		return mav("init.html");
//	}
//
//	@ApiOperation(value = "Initializes application")
//	@RequestMapping(path = "/site/init", method = RequestMethod.POST)
//	public ModelAndView initSite(@RequestBody UserForm form) throws ApiException {
//		List<UserPojo> list = service.getAll();
//		if (list.size() > 0) {
//			info.setMessage("Application already initialized. Please use existing credentials");
//		} else {
//			form.setRole("admin");
//			UserPojo p = convert(form);
//			service.add(p);
//			info.setMessage("Application initialized");
//		}
//		return mav("init.html");
//
//	}

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
