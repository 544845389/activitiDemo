package com.example.activitidemo.intercept;

import com.alibaba.fastjson.JSON;
import com.lorne.core.framework.exception.ServiceException;
import com.lorne.core.framework.model.Msg;
import com.lorne.core.framework.model.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public String  jsonErrorHandler(HttpServletResponse response, HttpServletRequest req, Exception e) throws Exception {
        Response res = new Response();
        res.setCode(40010);
        if ( e instanceof ServiceException) {
            res.setMsg(e.getMessage());
        }else{
            e.printStackTrace();
            res.setMsg(e.getClass().getName());
        }
        Msg _msg = new Msg();
        _msg.setRes(res);
        String json = JSON.toJSONString(_msg).toString();

        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Content-type",
                "application/json;charset=utf-8");
        response.getWriter().print(json);
        response.getWriter().close();
        return json;
    }




}
