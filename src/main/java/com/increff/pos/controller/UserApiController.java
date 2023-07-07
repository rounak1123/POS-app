package com.increff.pos.controller;

import com.increff.pos.dto.UserDto;
import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.model.UserData;
import com.increff.pos.model.MessageData;
import com.increff.pos.service.ApiException;
import com.increff.pos.spring.SecurityConfig;
import com.increff.pos.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Api
@RestController
@RequestMapping(path= "/api/user")
public class UserApiController {

    private static Logger logger = Logger.getLogger(SecurityConfig.class);

    @Autowired
    private UserDto dto;

    @ApiOperation(value = "Adds an user")
    @PostMapping
    public void add(@RequestBody UserForm form) throws ApiException {
        dto.add(form);
    }

    @ApiOperation(value = "Gets list of all users")
    @GetMapping
    public List<UserData> getAll() {
        return dto.getAll();
    }


}
