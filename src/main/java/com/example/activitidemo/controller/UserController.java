package com.example.activitidemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.activitidemo.service.UserService;
import com.lorne.core.framework.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 54484 on 2018/3/6.
 */
@RestController
@RequestMapping("user")
public class UserController {



    @Autowired
    private UserService userService;



    /**
     * 注册
     * @return
     */
    @RequestMapping(value = "register" ,method = RequestMethod.POST)
    public boolean register( @RequestBody JSONObject json){
        String userName = json.get("userName").toString();
        String userPwd = json.get("userPwd").toString();
        return userService.register(userName , userPwd);
    }





    /**
     * 登陆
     * @return
     */
    @RequestMapping(value = "login" ,method = RequestMethod.POST)
    public boolean login( @RequestBody JSONObject json) throws ServiceException {
        String userName = json.get("userName").toString();
        String userPwd = json.get("userPwd").toString();
        return userService.login(userName , userPwd);
    }









}
