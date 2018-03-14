package com.example.activitidemo.service.impl;

import com.example.activitidemo.service.UserService;
import com.lorne.core.framework.exception.ServiceException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 54484 on 2018/3/6.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {



    @Autowired
    private IdentityService identityService;


    /**
     * 登陆
     * @param userName
     * @param pwd
     * @return
     */
    @Override
    public boolean login(String userName, String pwd) throws ServiceException {
       User user =  identityService.createUserQuery().userId(userName).singleResult();
       if(user == null){
            throw  new ServiceException("用户不存在！");
       }

       if(!user.getPassword().equals(pwd)){
            throw  new ServiceException("密码不正确！");
       }

       return true;
    }



    /**
     * 注册
     * @param
     * @param
     * @return
     */
    @Override
    public boolean register(String userName, String userPwd) {
        User user =  identityService.newUser(userName);
        user.setEmail("测试@.com");
        user.setPassword(userPwd);

        identityService.saveUser(user);
        return false;
    }



}
