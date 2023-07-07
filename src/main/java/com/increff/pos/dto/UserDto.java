package com.increff.pos.dto;

import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserDto {

    @Autowired
    private UserService service;

    @Value(("${app.supervisorEmail}"))
    private String supervisorEmail;

    public void add(@RequestBody UserForm form) throws ApiException {
        emptyCheck(form);
        normalize(form);
        UserPojo p = convert(form);
        service.add(p);
    }

    public List<UserData> getAll() {
        List<UserPojo> list = service.getAll();
        List<UserData> list2 = new ArrayList<UserData>();
        for (UserPojo p : list) {
            list2.add(convert(p));
        }
        list2.sort((o1, o2) -> {
            if (o1.getId() > o2.getId()) return -1;
            else return 1;
        });
        return list2;
    }


    private  UserData convert(UserPojo p) {
        UserData d = new UserData();
        d.setEmail(p.getEmail());
        d.setId(p.getId());
        return d;
    }


    private  UserPojo convert(UserForm f) throws ApiException {
        UserPojo p = new UserPojo();
        p.setEmail(f.getEmail());
        p.setPassword(f.getPassword());
        if(f.getEmail() == supervisorEmail)
            p.setRole("admin");
        else
            p.setRole("operator");
        return p;
    }

    // CHECKS AND NORMALIZATION FOR THE FORM.

    public static void normalize(UserForm f) throws ApiException{
        f.setEmail(StringUtil.toLowerCase(f.getEmail()).trim());
        f.setPassword(StringUtil.toLowerCase(f.getPassword()).trim());
        if(hasSpecialCharacter(f.getEmail()) || hasSpecialCharacter(f.getPassword()))
            throw new ApiException("invalid character in brand or category.");
    }

    public static void emptyCheck(UserForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getEmail()))
            throw  new ApiException("Email field cannot be empty.");
        if(StringUtil.isEmpty(f.getPassword()))
            throw  new ApiException("Password cannot be empty");
    }

    public static boolean hasSpecialCharacter(String input) {
        String allowedCharacters = "-a-zA-Z0-9_*#@!.&%\\s";
        String patternString = "[^" + allowedCharacters + "]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
}
