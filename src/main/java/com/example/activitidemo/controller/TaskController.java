package com.example.activitidemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.activitidemo.service.TaskService;
import com.lorne.core.framework.exception.ServiceException;
import com.lorne.core.framework.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by 54484 on 2018/3/6.
 */
@RestController
@RequestMapping("task")
public class TaskController {



    @Autowired
    private TaskService taskService;




    /**
     * 部署流程
     * @return
     */
    @RequestMapping(value = "deploy")
    public void deploy() throws IOException {
        taskService.deploy();
    }





    /**
     * 待办任务列表
     * @return
     */
    @RequestMapping(value = "agencyTaskPage" ,method = RequestMethod.POST)
    public Page<Map<String,Object>> agencyTaskPage(@RequestParam("userId") String  userId , @RequestParam("type") String  type ) {
        return taskService.agencyTaskPage(userId , type);
    }



    /**
     * 获取流程流转图
     * @return
     */
    @RequestMapping(value = "findFlowChart")
    public void findFlowChart(@RequestParam("processInstanceId") String  processInstanceId , HttpServletResponse response) throws IOException {
         taskService.findFlowChart(processInstanceId , response);
    }



    /**
     *  完成任务
     * @return
     */
    @RequestMapping(value = "finishTask")
    public boolean finishTask(@RequestBody JSONObject json ) throws IOException {
        String taskId = json.get("taskId").toString();
        String variable = json.get("variable").toString();
        return taskService.finishTask(taskId , variable);
    }




    /**
     *  获取已部署流程
     * @return
     */
    @RequestMapping(value = "deployedProcessPage")
    public Page<Map<String,Object>> deployedProcessPage() throws IOException {
        return taskService.deployedProcessPage();
    }





    /**
     *  启动流程
     * @return
     */
    @RequestMapping(value = "startProcess")
    public boolean startProcess(@RequestBody JSONObject json) throws IOException, ServiceException {
        String processId = json.getString("processId");
        String userId = json.getString("userId");
        String variable = json.getString("variable");
        return taskService.startProcess(processId , userId , variable);
    }



    /**
     *  删除流程
     * @return
     */
    @RequestMapping(value = "deleteProcess")
    public boolean deleteProcess(@RequestBody JSONObject json) throws IOException {
        String deploymentId = json.getString("deploymentId");
        return taskService.deleteProcess(deploymentId);
    }



    /**
     * 我的流程列表
     * @return
     */
    @RequestMapping(value = "myProcessPage" ,method = RequestMethod.POST)
    public Page<Map<String,Object>> myProcessPage(@RequestParam("userId") String  userId) {
        return taskService.myProcess(userId);
    }



    /**
     * 获取流程的表单  【启动】
     */
    @RequestMapping(value = "obtainForm")
    public Object obtainForm(@RequestBody JSONObject  json) {
        String processId = json.getString("processId");
        return taskService.obtainForm(processId);
    }



    /**
     * 获取流程的表单  【任务】
     */
    @RequestMapping(value = "taskForm")
    public Object taskForm(@RequestBody JSONObject  json) {
        String taskId = json.getString("taskId");
        return taskService.taskForm(taskId);
    }




    /**
     * 签收任务
     */
    @RequestMapping(value = "claimTask")
    public Object claimTask(@RequestBody JSONObject  json) {
        String taskId = json.getString("taskId");
        String userId = json.getString("userId");
        return taskService.claimTask(taskId , userId);
    }




    /**
     * 已办任务
     * @return
     */
    @RequestMapping(value = "doTaskPage" ,method = RequestMethod.POST)
    public Page<Map<String,Object>> doTask(@RequestParam("userId") String  userId) {
        return taskService.doTask(userId);
    }




}
