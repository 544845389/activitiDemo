/**
 * Created by 54484 on 2018/3/7.
 */

$(function () {
    var  userName = sessionStorage.getItem("userName");

    // 初始化table
    var table = $("#table");
    var url = backUrl + agencyTask+"?userId="+userName+"&type=1";
    table.bootstrapTable("refresh", {url: url});

    slider();
})





operateFormatter = function (value, row, index) {
    return [
        '<a class="find" href="javascript:void(0)" title="查看流程详情" data-toggle="slider" data-target="#TaskInfoDiv" ><i class="glyphicon glyphicon-search" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'+
        '<a class="ok" href="javascript:void(0)" title="签收任务" data-target="#signModalDiv" data-to="modal" ><i class="glyphicon glyphicon-ok" ></i></a>' +
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
        var  userName = sessionStorage.getItem("userName");
        $("#taskId").val(row.id);
        $("#userId").val(userName);
    }

};





/**
 * 签收任务
 */
$("#sign-btn").on('click',  function() {
    var userId = $("#userId").val();
    var taskId = $("#taskId").val();


    var url = backUrl +claim;
    var  params = {
        taskId:taskId ,
        userId:userId
    }
    comment.post( url  , JSON.stringify(params) , '任务操作中....', function (res) {
        if (res.res.data == true) {
            hint('操作成功!');
        }else{
            hint('操作失败!');
        }
        modal.modal('hide');
        $("#table").bootstrapTable("refresh");
    });



});








