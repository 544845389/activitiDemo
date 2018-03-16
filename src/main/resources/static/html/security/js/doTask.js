/**
 * Created by 54484 on 2018/3/7.
 */

$(function () {
    var  userName = sessionStorage.getItem("userName");

    // 初始化table
    var table = $("#table");
    var url = backUrl + doTask+"?userId="+userName;
    table.bootstrapTable("refresh", {url: url});

    slider();
})




operateFormatter = function (value, row, index) {
    return [
        '<a class="find" href="javascript:void(0)" title="查看流程详情" data-toggle="slider" data-target="#TaskInfoDiv" ><i class="glyphicon glyphicon-search" ></i></a>' +
        '&nbsp;&nbsp;&nbsp;'
    ].join('');

}



window.operateEvents = {

    'click .find': function (e, value, row, index) {
        $("#imgDiv").empty();
        var url = backUrl + findFlowChart+"?processInstanceId="+row.processInstanceId;
        $("#imgDiv").append("<img src='"+url+"' />")
    }

};
