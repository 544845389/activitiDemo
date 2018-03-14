package com.example.activitidemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.activitidemo.model.BpmsActivityTypeEnum;
import com.example.activitidemo.model.StartParam;
import com.example.activitidemo.service.TaskService;
import com.example.activitidemo.utils.UtilMisc;
import com.lorne.core.framework.exception.ServiceException;
import com.lorne.core.framework.model.Page;
import com.lorne.core.framework.utils.DateUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * Created by 54484 on 2018/3/7.
 */
@Service
public class TaskServiceImpl implements TaskService {


    @Autowired
    private org.activiti.engine.TaskService taskService;


    @Autowired
    RepositoryService repositoryService;

    @Autowired
    HistoryService historyService;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    IdentityService  identityService;

    @Autowired
    FormService  formService;




    /**
     * 待办任务列表
     * @return
     */
    @Override
    public Page<Map<String, Object>> agencyTaskPage(String userId , String type) {
        Page<Map<String, Object>> page = new Page<Map<String, Object>>();
        List<Map<String,Object>> list = new ArrayList<>();
        List<Task> task = null;

        if(type.equals("0")){
            // 待办
            task =  taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        }else{
            // 待签收
            task = taskService.createTaskQuery().taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
        }


        for(int i = 0 ; i < task.size(); i ++){
            Map<String,Object> map = new HashMap<>();
            Task t =   task.get(i);
            map.put("createDate" , DateUtil.formatDate(t.getCreateTime(),DateUtil.FULL_DATE_TIME_FORMAT));
            map.put("name" , t.getName());
            map.put("processInstanceId" , t.getProcessInstanceId());
            map.put("id" , t.getId());
            map.put("fromKey" , t.getFormKey());
            list.add(map);

            // 流程启动用户
            HistoricProcessInstance historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(t.getProcessInstanceId())
                            .singleResult();
            map.put("startUserId" , historicProcessInstance.getStartUserId());

        }

        page.setNowPage(1);
        page.setPageSize(1000);
        page.setPageNumber(1000);
        page.setTotal(task.size());
        page.setRows(list);
        return page;
    }


    /**
     * 获取流程流转图
     * @return
     */
    @Override
    public void findFlowChart(String processInstanceId , HttpServletResponse response) throws IOException {
        // 获取历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 获取流程中已经执行的节点，按照执行先后顺序排序
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();
        // 构造已执行的节点ID集合
        List<String> executedActivityIdList = new ArrayList<String>();
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
            executedActivityIdList.add(activityInstance.getActivityId());
        }
        // 获取bpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
        // 获取流程已发生流转的线ID集合
        List<String> flowIds = getExecutedFlows(bpmnModel, historicActivityInstanceList);
        // 使用默认配置获得流程图表生成器，并生成追踪图片字符流
        ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
        InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "宋体", "黑体", null, 1.0);




        System.out.println("这是一个中文！！");
        BufferedInputStream reader = new BufferedInputStream(imageStream);
        BufferedOutputStream writer = new BufferedOutputStream(response.getOutputStream());

        byte[] bytes = new byte[1024 * 1024];
        int length = reader.read(bytes);
        while ((length > 0)) {
            writer.write(bytes, 0, length);
            length = reader.read(bytes);
        }
        reader.close();
        writer.close();

//        response.addHeader("Content-Disposition", "attachment;filename=" + file.getName());
//        response.addHeader("Content-Length", "" + file.length());
        response.setContentType("application/octet-stream");
    }

    private List<String> getExecutedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 流转线ID集合
        List<String> flowIdList = new ArrayList<String>();
        // 全部活动实例
        List<FlowNode> historicFlowNodeList = new LinkedList<FlowNode>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstanceList = new LinkedList<HistoricActivityInstance>();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            historicFlowNodeList.add((FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId()));
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstanceList.add(historicActivityInstance);
            }
        }

        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        FlowNode currentFlowNode = null;
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstanceList) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId());
            List<SequenceFlow> sequenceFlowList = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的
             * 满足如下条件认为已已流转：
             * 1.当前节点是并行网关或包含网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最近的流转节点视为有效流转
             */
            FlowNode targetFlowNode = null;
            if (BpmsActivityTypeEnum.PARALLEL_GATEWAY.getType().equals(currentActivityInstance.getActivityType())
                    || BpmsActivityTypeEnum.INCLUSIVE_GATEWAY.getType().equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配Flow目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlowList) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef());
                    if (historicFlowNodeList.contains(targetFlowNode)) {
                        flowIdList.add(sequenceFlow.getId());
                    }
                }
            } else {
                List<Map<String, String>> tempMapList = new LinkedList<Map<String,String>>();
                // 遍历历史活动节点，找到匹配Flow目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlowList) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            tempMapList.add(UtilMisc.toMap("flowId", sequenceFlow.getId(), "activityStartTime", String.valueOf(historicActivityInstance.getStartTime().getTime())));
                        }
                    }
                }

                // 遍历匹配的集合，取得开始时间最早的一个
                long earliestStamp = 0L;
                String flowId = null;
                for (Map<String, String> map : tempMapList) {
                    long activityStartTime = Long.valueOf(map.get("activityStartTime"));
                    if (earliestStamp == 0 || earliestStamp >= activityStartTime) {
                        earliestStamp = activityStartTime;
                        flowId = map.get("flowId");
                    }
                }
                flowIdList.add(flowId);
            }
        }
        return flowIdList;
    }





    /**
     *  完成任务
     * @return
     */
    @Override
    public boolean finishTask(String taskId ,  String variable) {
        Map<String,Object> map = new HashMap<>();
        if(!StringUtils.isEmpty(variable)){
            map = JSONObject.parseObject(variable);
        }
         taskService.complete(taskId , map);
         return  true;
    }




    /**
     *  获取已部署流程
     * @return
     */
    @Override
    public Page<Map<String,Object>> deployedProcessPage() {
        Page<Map<String,Object>> page = new Page<>();
        List<Map<String,Object>> rList = new ArrayList<>();

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionId()
                .asc()
                .orderByProcessDefinitionVersion()
                .asc()
                .list();

        for(int i = 0;i < list.size() ; i++){
            Map<String,Object> map = new HashMap<>();
            ProcessDefinition pd  =  list.get(i);
            map.put("processId" , pd.getId());
            map.put("key" , pd.getKey());
            map.put("version" , pd.getVersion());
            map.put("name" , pd.getName());

            Deployment deployment =  repositoryService.createDeploymentQuery().deploymentId(pd.getDeploymentId()).singleResult();

            map.put("name" , deployment.getName());
            map.put("createDate" , DateUtil.formatDate( deployment.getDeploymentTime() , DateUtil.FULL_DATE_TIME_FORMAT ));
            map.put("deploymentId" , deployment.getId());

            rList.add(map);
        }

        page.setNowPage(1);
        page.setRows(rList);
        page.setPageSize(1000);
        page.setTotal(rList.size());
        page.setPageNumber(1000);
        return  page;
    }


    /**
     * 启动流程
     * @param processId
     * @return
     */
    @Override
    public boolean startProcess(String processId ,  String userId , String variable) throws ServiceException {
        identityService.setAuthenticatedUserId(userId);
        if(!userId.equals("kermit")){
            throw  new ServiceException("请使用账号【kermit】启动该流程");
        }
        Map map = JSONObject.parseObject(variable);
        runtimeService.startProcessInstanceById(processId , map);

        return true;
    }


    /**
     *  删除流程
     * @return
     */
    @Override
    public boolean deleteProcess(String deploymentId) {
         repositoryService.deleteDeployment(deploymentId , true);
         return true;
    }


    /**
     * 我的流程列表
     * @return
     */
    @Override
    public Page<Map<String, Object>> myProcess(String userId) {
        Page<Map<String,Object>>  page = new Page<>();

        List<Map<String,Object>>  list = new ArrayList<>();
        List<HistoricProcessInstance> hList =  historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId)
                .includeProcessVariables()
                .orderByProcessInstanceStartTime()
                .desc()
                .list();

        for(int i = 0 ; i < hList.size() ; i++){
            HistoricProcessInstance hp =  hList.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("startTime" ,  DateUtil.formatDate(hp.getStartTime() , DateUtil.FULL_DATE_TIME_FORMAT ));
            if(hp.getEndTime() != null){
                map.put("endTime" , DateUtil.formatDate(hp.getEndTime() , DateUtil.FULL_DATE_TIME_FORMAT ));
            }
            map.put("processDefinitionKey" , hp.getProcessDefinitionKey());

            map.put("deploymentId" , hp.getDeploymentId());
            Deployment deployment =  repositoryService.createDeploymentQuery().deploymentId(hp.getDeploymentId()).singleResult();
            map.put("name" , deployment.getName());
            map.put("createDate" , DateUtil.formatDate( deployment.getDeploymentTime() , DateUtil.FULL_DATE_TIME_FORMAT ));


            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(hp.getId()).singleResult();
            if(processInstance == null){
                map.put("isEnd" , true);
            }else{
                map.put("isEnd" , false);
            }
            map.put("id" , hp.getId());


            list.add(map);
        }

        page.setNowPage(1);
        page.setRows(list);
        page.setPageSize(1000);
        page.setTotal(list.size());
        page.setPageNumber(1000);
        return page;
    }



    /**
     * 获取流程的表单
     */
    @Override
    public Object obtainForm(String processId) {
        Object startFormData = formService.getRenderedStartForm(processId);
        Map<String,Object> map = new HashMap<>();
        if(startFormData != null && startFormData != ""){
            map.put("body" , startFormData);
        }
        return map;
    }



    /**
     * 获取流程的表单  【任务】
     */
    @Override
    public Map<String,Object> taskForm(String taskId) {
        Object renderedTaskForm  =    formService.getRenderedTaskForm(taskId);
        Map<String,Object> map = new HashMap<>();
        if(renderedTaskForm != null && renderedTaskForm != ""){
            map.put("body" , renderedTaskForm);
        }
        return map;
    }



    /**
     * 签收任务
     * @param taskId
     * @param userId
     * @return
     */
    @Override
    public Object claimTask(String taskId, String userId) {
        taskService.claim(taskId , userId);
        return true;
    }



    @Override
    public void deploy() {

        //从classpath路径下读取资源文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("processes/processes.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        Deployment deployment = processEngine.getRepositoryService()//获取流程定义和部署对象相关的Service
                .createDeployment()//创建部署对象
                .addZipInputStream(zipInputStream)//使用zip方式部署
                .name("物流")
                .deploy();//完成部署

    }


}
