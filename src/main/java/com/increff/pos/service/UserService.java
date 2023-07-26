package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserDao dao;

	@Value(("${app.supervisorEmailList}"))
	private String supervisorEmailList;

	public void add(UserPojo userPojo) throws ApiException {

		UserPojo existing = dao.select(userPojo.getEmail());
		if (existing != null) {
			throw new ApiException("User with given email already exists");
		}
		if(isSupervisor(userPojo.getEmail())){
			userPojo.setRole("admin");
		}else{
			userPojo.setRole("standard");
		}
		dao.insert(userPojo);
	}

	public UserPojo get(String email) throws ApiException {
		return dao.select(email);
	}


	private String[] splitEmail(String supervisorEmailList) {
		// Remove leading and trailing white spaces (optional)
		supervisorEmailList = supervisorEmailList.trim();

		String[] supervisorArray = supervisorEmailList.split(",");

		for (int i = 0; i < supervisorArray.length; i++) {
			supervisorArray[i] = supervisorArray[i].trim();
		}

		return supervisorArray;
	}

	private boolean isSupervisor(String userEmail){
		String[] supervisorList = splitEmail(supervisorEmailList);
		for(String email: supervisorList){
			if(email.equals(userEmail))
				return true;
		}
		return false;
	}
}
