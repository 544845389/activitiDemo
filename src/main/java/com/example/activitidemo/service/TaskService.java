package com.example.activitidemo.service;

import com.lorne.core.framework.exception.ServiceException;
import com.lorne.core.framework.model.Page;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by 54484 on 2018/3/7.
 */
public interface TaskService {




    Page<Map<String,Object>> agencyTaskPage(String userId , String type);



    void findFlowChart(String processInstanceId , HttpServletResponse response) throws IOException;



    boolean finishTask(String taskId , String variable);



    Page<Map<String,Object>> deployedProcessPage();



    boolean startProcess(String processId , String userId , String variable) throws ServiceException;




    boolean deleteProcess(String deploymentId);



    Page<Map<String,Object>> myProcess(String userId);



    Object obtainForm(String processId);



    Object taskForm(String taskId);



    Object claimTask(String taskId, String userId);


    void deploy();

    Page<Map<String,Object>> doTask(String userId);
}
