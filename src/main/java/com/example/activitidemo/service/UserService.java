package com.example.activitidemo.service;

import com.lorne.core.framework.exception.ServiceException;

/**
 * Created by 54484 on 2018/3/6.
 */
public interface UserService {


    /**
     * 登陆
     * @param userName
     * @param pwd
     * @return
     */
    boolean login(String userName, String pwd) throws ServiceException;



    /**
     * 注册
     * @param
     * @param
     * @return
     */
    boolean register(String userName, String userPwd);

}
