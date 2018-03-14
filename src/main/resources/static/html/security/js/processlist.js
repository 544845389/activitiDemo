/**
 * Created by 54484 on 2018/3/7.
 */

$(function () {

    // 初始化table
    var table = $("#table");
    var url = backUrl + processListPage;
    table.bootstrapTable("refresh", {url: url});

    slider();
})




operateFormatter = function (value, row, index) {
    return [
        '<a class="start" href="javascript:void(0)" title="启动流程" data-toggle="slider" data-target="#starTaskInfoDiv" ><i class="glyphicon glyphicon-play" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'+
        '<a class="delete" href="javascript:void(0)" title="删除流程" data-target="#delTaskModal" data-to="modal" ><i class="glyphicon glyphicon-remove-sign" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'
    ].join('');

}



window.operateEvents = {

    'click .start': function (e, value, row, index) {
        $("#starTaskId").val(row.processId);

        obtainForm(row.processId , $("#starDiv"));
    },
    'click .delete': function (e, value, row, index) {
        $("#delId").val(row.deploymentId);
    }

};



/**
 * 展示表单 【启动】
 */
function  obtainForm(processId , div ) {
    div.empty();
    var url = backUrl +obtainFormUrl;
    var  params = {
        processId:processId
    }
    comment.post( url  , JSON.stringify(params) , '任务操作中....', function (res) {
        var obj = res.res.data.body;
        if(obj != null && obj != ""){
            div.append(obj);
        }
    });


}















/**
 *  启动任务
 */
$("#starTask-btn").on('click',  function() {
    var id = $("#starTaskId").val();
    var  userName = sessionStorage.getItem("userName");
    var inputs =  $("#starDiv").find("*[name]");

    var variable = {};
    for(var i = 0 ;i < inputs.length ; i++){
        var obj = inputs[i];
        var name = $(obj).attr("name");
        var name = $(obj).attr("name");
        var value =  $(obj).val();
        var info = $(obj).attr("data-info");
        if(value == null || value == ""){
            hint(info+"不可为空！");
            return;
        }
        variable[name] = value;
    }


    var url = backUrl +startProcess;
    var  params = {
        processId:id ,
        userId:userName ,
        variable:JSON.stringify(variable)
    }
    comment.post( url  , JSON.stringify(params) , '任务操作中....', function (res) {
        if (res.res.data == true) {
            hint('操作成功!');
        }else{
            hint('操作失败!');
        }
        $("#starTaskInfoDiv").toggleClass('slide-open');
    });

});







/**
 *  部署一个流程
 */
$("#addTask").on('click',  function() {
    var url = backUrl +deploy;
    comment.post( url  ,null, '任务操作中....', function (res) {
        var table = $("#table");
        table.bootstrapTable("refresh");
    });

});






/**
 *  删除任务
 */
$("#delTask-btn").on('click',  function() {
    var id = $("#delId").val();

    var url = backUrl +deleteProcess;
    var  params = {
        deploymentId:id
    }
    comment.post( url  , JSON.stringify(params) , '任务操作中....', function (res) {
        if (res.res.data == true) {
            hint('操作成功!');
        }else{
            hint('操作失败!');
        }
        modal.modal('hide');

        var table = $("#table");
        table.bootstrapTable("refresh");
    });

});