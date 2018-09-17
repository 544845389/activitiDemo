package com.example.activitidemo.intercept;

import com.lorne.core.framework.model.Msg;
import com.lorne.core.framework.model.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Created by houcunlu on 2017/8/18.
 */
@ControllerAdvice
public class DataResponseBodyAdvice implements ResponseBodyAdvice {


    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }


    /**
     * 统一处理  正常返回数据
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        /**
         *  分页数据  接口地址必须以 Page 结尾   /admin/findUserPage
         */
        String url =  ((ServletServerHttpRequest) request).getServletRequest().getRequestURI();
        if("Page".equals(url.substring(url.length()-4))){
            return  body;
        }

        if(url.endsWith("json") || url.endsWith("stencilset") || url.endsWith("error")){
            return  body;
        }

        Response res = new Response();
        res.setCode(40000);
        res.setMsg("");
        res.setData(body);
        Msg msg = new Msg(1, res);
        return msg;

    }




}
