package com.example.activitidemo;

import com.example.activitidemo.model.BpmsActivityTypeEnum;
import com.example.activitidemo.utils.UtilMisc;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitidemoApplicationTests {


	@Autowired
	private IdentityService identityService;

	@Autowired
	private TaskService taskService;

	@Autowired
	RepositoryService repositoryService;

	@Autowired
	HistoryService historyService;

	@Autowired
	ProcessEngine processEngine;


	@Autowired
	RuntimeService runtimeService;

	@Autowired
	FormService  formService;




	/**
	 * 查询用户
	 */
	@Test
	public void findUser() {

		boolean str = identityService.checkPassword("fozzie1" , "fozzie");
		System.out.println(str);
	}



	/**
	 * 用户待办任务列表
	 */
	@Test
	public void findUser1() {

		List<Task> task =  taskService.createTaskQuery().taskAssignee("fozzie").list();
		System.out.println(task.size());
	}




	/**
	 * 用户带签收列表
	 */
	@Test
	public void findUser10() {

		List<Task> task =  taskService.createTaskQuery().taskCandidateUser("gonzo").list();
		System.out.println(task.size());
	}




//
//	/**
//	 * 流程图
//	 */
//	@Test
//	public void findUser2() {
//		String id = "55";
//		try {
//			// 获取历史流程实例
//			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//
//			// 获取流程中已经执行的节点，按照执行先后顺序排序
//			List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(id).orderByHistoricActivityInstanceId().asc().list();
//
//			// 构造已执行的节点ID集合
//			List<String> executedActivityIdList = new ArrayList<String>();
//			for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
//				executedActivityIdList.add(activityInstance.getActivityId());
//			}
//
//			// 获取bpmnModel
//			BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
//			// 获取流程已发生流转的线ID集合
//			List<String> flowIds = getExecutedFlows(bpmnModel, historicActivityInstanceList);
//
//			// 使用默认配置获得流程图表生成器，并生成追踪图片字符流
//			ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
//			InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);
//			FileUtils.copyInputStreamToFile(imageStream , new File("E://aa.png"));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//
//	}
//
//
//
//	private List<String> getExecutedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
//		// 流转线ID集合
//		List<String> flowIdList = new ArrayList<String>();
//		// 全部活动实例
//		List<FlowNode> historicFlowNodeList = new LinkedList<FlowNode>();
//		// 已完成的历史活动节点
//		List<HistoricActivityInstance> finishedActivityInstanceList = new LinkedList<HistoricActivityInstance>();
//		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
//			historicFlowNodeList.add((FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true));
//			if (historicActivityInstance.getEndTime() != null) {
//				finishedActivityInstanceList.add(historicActivityInstance);
//			}
//		}
//
//		// 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
//		FlowNode currentFlowNode = null;
//		for (HistoricActivityInstance currentActivityInstance : finishedActivityInstanceList) {
//			// 获得当前活动对应的节点信息及outgoingFlows信息
//			currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
//			List<SequenceFlow> sequenceFlowList = currentFlowNode.getOutgoingFlows();
//
//			/**
//			 * 遍历outgoingFlows并找到已已流转的
//			 * 满足如下条件认为已已流转：
//			 * 1.当前节点是并行网关或包含网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
//			 * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最近的流转节点视为有效流转
//			 */
//			FlowNode targetFlowNode = null;
//			if (BpmsActivityTypeEnum.PARALLEL_GATEWAY.getType().equals(currentActivityInstance.getActivityType())
//					|| BpmsActivityTypeEnum.INCLUSIVE_GATEWAY.getType().equals(currentActivityInstance.getActivityType())) {
//				// 遍历历史活动节点，找到匹配Flow目标节点的
//				for (SequenceFlow sequenceFlow : sequenceFlowList) {
//					targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
//					if (historicFlowNodeList.contains(targetFlowNode)) {
//						flowIdList.add(sequenceFlow.getId());
//					}
//				}
//			} else {
//				List<Map<String, String>> tempMapList = new LinkedList<Map<String,String>>();
//				// 遍历历史活动节点，找到匹配Flow目标节点的
//				for (SequenceFlow sequenceFlow : sequenceFlowList) {
//					for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
//						if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
//							tempMapList.add(UtilMisc.toMap("flowId", sequenceFlow.getId(), "activityStartTime", String.valueOf(historicActivityInstance.getStartTime().getTime())));
//						}
//					}
//				}
//
//				// 遍历匹配的集合，取得开始时间最早的一个
//				long earliestStamp = 0L;
//				String flowId = null;
//				for (Map<String, String> map : tempMapList) {
//					long activityStartTime = Long.valueOf(map.get("activityStartTime"));
//					if (earliestStamp == 0 || earliestStamp >= activityStartTime) {
//						earliestStamp = activityStartTime;
//						flowId = map.get("flowId");
//					}
//				}
//				flowIdList.add(flowId);
//			}
//		}
//		return flowIdList;
//	}





	/**
	 * 获取已部署的列表
	 */
	@Test
	public void findUser3() {
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
				.orderByProcessDefinitionId()
				.asc()
				.orderByProcessDefinitionVersion()
				.asc()
				.list();
		for(int i = 0;i < list.size() ; i++){
			ProcessDefinition pd  =  list.get(i);
			System.out.println("");
		}
	}






	/**
	 * 获取我启动的流程列表
	 */
	@Test
	public void findUser4() {
		List<HistoricProcessInstance> list =  historyService.createHistoricProcessInstanceQuery()
				.startedBy("fozzie")
				.includeProcessVariables()
				.orderByProcessInstanceStartTime()
				.desc()
				.list();

		System.out.println("");

	}



	/**
	 * 查询所有已完成的任务列表
	 */
	@Test
	public void findUser5() {
		String  processInstanceId = "";
//		String  processInstanceId = "";
		List<HistoricProcessInstance> list = 	historyService.createHistoricProcessInstanceQuery().finished().includeProcessVariables().orderByProcessInstanceStartTime().desc().list();
		for(int i = 0 ; i < list.size() ; i ++){
			System.out.println("");
		}
	}




	/**
	 * 获取流程变量 启动
	 */
	@Test
	public void findUser6() {
		String procDefId = "logistics:1:7526";


		System.out.println("");
	}



	/**
	 * 获取流程变量 任务
	 */
	@Test
	public void findUser7() {
		List<HistoricVariableInstance> list = processEngine.getHistoryService()
				.createHistoricVariableInstanceQuery()//创建一个历史的流程变量查询
				.processInstanceId("22521")
				.list();
		System.out.println("");
	}





	/**
	 *  部署 流程
	 */
	@Test
	public void findUser8() {


		//从classpath路径下读取资源文件
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("processes/processes.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService()//获取流程定义和部署对象相关的Service
				.createDeployment()//创建部署对象
				.addZipInputStream(zipInputStream)//使用zip方式部署
				.name("物流")
				.deploy();//完成部署

		System.out.println("部署ID："+deployment.getId());//1
		System.out.println("部署时间："+deployment.getDeploymentTime());

	}







}