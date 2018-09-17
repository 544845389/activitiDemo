package com.example.activitidemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lorne.core.framework.model.Page;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author 侯存路
 * @date 2018/9/13
 * @company codingApi
 * @description
 */
@Controller
public class IndexController {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    ObjectMapper objectMapper;


    @GetMapping("/create")
    public void newModel(HttpServletResponse response ,
                         @RequestParam("name") String name ,
                         @RequestParam("description") String description ,
                         @RequestParam("revision") int revision ,
                         @RequestParam("key") String key
    ) throws IOException {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //初始化一个空模型
        Model model = repositoryService.newModel();

        //设置一些默认信息
//        String name = "new-process";
//        String description = "";
//        int revision = 1;
//        String key = "process";

        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());

        repositoryService.saveModel(model);
        String id = model.getId();

        //完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace",
                "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));
        response.sendRedirect("/modeler.html?modelId=" + id);

    }

    @ResponseBody
    @PostMapping("modelistPage")
    public Object modelList(){
        Page<Model> page = new Page<>();
        List<Model> models = repositoryService.createModelQuery().orderByCreateTime().desc().list();
        page.setRows(models);
        System.out.println(JSON.toJSONString(models));
        return page;
    }



    @PostMapping("/deleteModel")
    @ResponseBody
    public void newModel(@RequestBody JSONObject jsonObject ) {
        String id =  jsonObject.getString("id");
        repositoryService.deleteModel(id);
    }





}
