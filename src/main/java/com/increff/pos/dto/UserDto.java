package com.increff.pos.dto;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserDto {

    @Autowired
    private UserService userService;

    @Value(("${app.supervisorEmailList}"))
    private static String supervisorEmailList;


    public void add(UserForm userForm) throws ApiException {
        emptyCheck(userForm);
        normalize(userForm);
        UserPojo userPojo = convert(userForm);
        userService.add(userPojo);
    }


    private  UserPojo convert(UserForm f) throws ApiException {
        UserPojo p = new UserPojo();
        p.setEmail(f.getEmail());
        p.setPassword(f.getPassword());
        return p;
    }

    // CHECKS AND NORMALIZATION FOR THE FORM.

    private static void normalize(UserForm f) throws ApiException{
        f.setEmail(StringUtil.toLowerCase(f.getEmail()));
    }

    private static void emptyCheck(UserForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getEmail()))
            throw  new ApiException("Email field cannot be empty.");
        if(StringUtil.isEmpty(f.getPassword()))
            throw  new ApiException("Password cannot be empty");
    }

}
