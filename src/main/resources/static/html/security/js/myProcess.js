/**
 * Created by 54484 on 2018/3/7.
 */

$(function () {
    var  userName = sessionStorage.getItem("userName");

    // 初始化table
    var table = $("#table");
    var url = backUrl + myProcessPage+"?userId="+userName;
    table.bootstrapTable("refresh", {url: url});

    slider();
})





/**
 *  是否完成任务
 **/
isEnd = function (value, row, index) {
    var test = "";
    if (row.isEnd == true) {
        test = "<span style='color: green' >已完成</span>";
    }else{
        test = "<span style='color: red' >未完成</span>";
    }
    return [
        test
    ].join('');
}





operateFormatter = function (value, row, index) {

    return [
        '<a class="find" href="javascript:void(0)" title="查看流程详情" data-toggle="slider" data-target="#TaskInfoDiv" ><i class="glyphicon glyphicon-search" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'
    ].join('');

}



window.operateEvents = {

    'click .find': function (e, value, row, index) {
        $("#imgDiv").empty();
        var url = backUrl + findFlowChart+"?processInstanceId="+row.id;
        $("#imgDiv").append("<img src='"+url+"' />")
    }
};



/**
 *  启动任务
 */
$("#starTask-btn").on('click',  function() {
    var id = $("#starTaskId").val();
    var  userName = sessionStorage.getItem("userName");

    var url = backUrl +startProcess;
    var  params = {
        processId:id ,
        userId:userName
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


