package com.increff.pos.dao;

import com.increff.pos.pojo.UserPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
public class UserDao extends AbstractDao {

	private static String select_id = "select p from UserPojo p where id=:id";
	private static String select_email = "select p from UserPojo p where email=:email";

	
	@Transactional
	public void insert(UserPojo userPojo) {
		em().persist(userPojo);
	}

	public UserPojo select(int id) {
		TypedQuery<UserPojo> query = getQuery(select_id, UserPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public UserPojo select(String email) {
		TypedQuery<UserPojo> query = getQuery(select_email, UserPojo.class);
		query.setParameter("email", email);
		return getSingle(query);
	}

	public void update(UserPojo userPojo) {
	}


}
