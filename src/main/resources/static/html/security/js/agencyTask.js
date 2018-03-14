/**
 * Created by 54484 on 2018/3/7.
 */

$(function () {
    var  userName = sessionStorage.getItem("userName");

    // 初始化table
    var table = $("#table");
    var url = backUrl + agencyTask+"?userId="+userName+"&type=0";
    table.bootstrapTable("refresh", {url: url});

    slider();
})





operateFormatter = function (value, row, index) {
    return [
        '<a class="find" href="javascript:void(0)" title="查看流程详情" data-toggle="slider" data-target="#TaskInfoDiv" ><i class="glyphicon glyphicon-search" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'+
        '<a class="ok" href="javascript:void(0)" title="完成任务" data-toggle="slider" data-target="#OkTaskInfoDiv" ><i class="glyphicon glyphicon-ok" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'
    ].join('');

}



window.operateEvents = {

    'click .find': function (e, value, row, index) {
        $("#imgDiv").empty();
        var url = backUrl + findFlowChart+"?processInstanceId="+row.processInstanceId;
        $("#imgDiv").append("<img src='"+url+"' />")
    },
    'click .ok': function (e, value, row, index) {
        
        $("#okTaskId").val(row.id);
        loadform(row.id , "#taskForm");
    }

};


function loadform(taskId , div ) {
    $(div).empty();

    var url = backUrl +taskFormUrl;
    var  params = {
        taskId:taskId
    }
    comment.post( url  , JSON.stringify(params) , '任务操作中....', function (res) {
        $(div).append(res.res.data.body);
    });
}




/**
 * 完成任务
 */
$("#okTask-btn").on('click',  function() {
    var id = $("#okTaskId").val();

    var inputs =  $("#taskForm").find("*[name]");

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

    var url = backUrl +finishTask;
    var  params = {
        taskId:id ,
        variable:JSON.stringify(variable)
    }
    comment.post( url  , JSON.stringify(params) , '任务操作中....', function (res) {
        if (res.res.data == true) {
            hint('操作成功!');
        }else{
            hint('操作失败!');
        }
        $("#OkTaskInfoDiv").toggleClass('slide-open');
        $("#table").bootstrapTable("refresh");
    });



});




function singleModeOnchange(div ,  select ){
   var  divSelect =   $(''+div+'');
   var value =   $(select).val();
    if(value == "appoint"){
        divSelect.show();
    }else{
        divSelect.hide();
    }
}




function handlingOnchange(div ,  select ){
    var  divSelect =   $(''+div+'');
    var value =   $(select).val();
    alert(value);
    if(value == "handlingPoint"){
        divSelect.show();
    }else{
        divSelect.hide();
    }
}