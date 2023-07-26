package com.increff.pos.dto;

import com.increff.pos.dto.constructorUtil.FormConstructor;
import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;

public class UserDtoTest extends AbstractUnitTest{

    @Autowired
    UserDto userDto;

    @Autowired
    UserService userService;
    @Test
    public void addUserTest() throws ApiException {
        UserForm userForm = FormConstructor.createUser("test@test.com", "Test@123");
        userDto.add(userForm);
        UserPojo userPojo = userService.get("test@test.com");
        assertEquals(userPojo.getEmail(), "test@test.com");
        assertEquals(userPojo.getPassword(), "Test@123");
    }
}
