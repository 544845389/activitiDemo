/**
 * Created by 54484 on 2018/3/7.
 */

$(function () {

    // 初始化table
    var table = $("#table");
    var url = "/modelistPage";
    table.bootstrapTable("refresh", {url: url});

    slider();
})



metaInfo = function (value, row, index) {
    var metaInfoValue = JSON.parse(value);
    return [
        '<span>'+metaInfoValue.description+'</span>'
    ].join('');

}




operateFormatter = function (value, row, index) {
    return [
        '<a class="start" href="javascript:void(0)" title="编辑流程" >编辑</a>' +
        '&nbsp;&nbsp;&nbsp;'+
        '<a class="delete" href="javascript:void(0)" title="删除流程" data-target="#delTaskModal" data-to="modal" ><i class="glyphicon glyphicon-remove-sign" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'+
        '<a class="deploy" href="javascript:void(0)" title="部署流程" data-target="#deployTaskModal" data-to="modal"  >部署流程</a>' +
        '&nbsp;&nbsp;&nbsp;'
    ].join('');

}



window.operateEvents = {

    'click .start': function (e, value, row, index) {
        location.href = "../../../modeler.html?modelId="+row.id;
    },
    'click .delete': function (e, value, row, index) {
        $("#delId").val(row.id);
    },
    'click .deploy': function (e, value, row, index) {
        $("#deployId").val(row.id);
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

    $("#starTaskInfoDiv").toggleClass('slide-open');

    var key =  $("#key").val();
    var name =  $("#name").val();
    var description =  $("#description").val();
    var revision =  $("#revision").val();


    location.href = "/create?key="+key+"&name="+name+"&description="+description+"&revision="+revision;

});














/**
 *  删除任务
 */
$("#delTask-btn").on('click',  function() {
    var id = $("#delId").val();
    var  params = {
        id:id
    }

    $.ajax({
        type: "POST",
        url: "/deleteModel",
        data: JSON.stringify(params),
        contentType:"application/json",
        success: function(data){
            modal.modal('hide');
            var table = $("#table");
            table.bootstrapTable("refresh");
        }
    });

});





/**
 *  部署流程
 */
$("#deployTask-btn").on('click',  function() {
    var id = $("#deployId").val();
    var  params = {
        modelId:id
    }

    $.ajax({
        type: "POST",
        url: "/task/deployById",
        data: JSON.stringify(params),
        contentType:"application/json",
        success: function(data){
            modal.modal('hide');
            var table = $("#table");
            table.bootstrapTable("refresh");
        }
    });

});