package com.example.activitidemo.listeners;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.util.Map;

/**
 * Created by 54484 on 2018/3/14.
 *
 *  装卸派单
 */
public class HandlingListeners implements TaskListener {


    @Override
    public void notify(DelegateTask delegateTask) {
        Map<String,Object> map =  delegateTask.getVariables();
        String singleMode =  map.get("handlingSingleMode").toString();
        if("handlingPoint".equals(singleMode)){
            // 指定人员
            String personnel =  map.get("handlingPersonnel").toString();
            delegateTask.setAssignee(personnel);
        }else{
            // 抢单模式
            delegateTask.addCandidateGroup("engineering");
        }

    }



}
