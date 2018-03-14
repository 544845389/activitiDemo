/**
 * Created by houcunlu on 2017/8/22.
 */

var comment;
if (!comment)
    comment = {};


/**
 * post提交事件
 * @param url   地址
 * @param params    参数
 * @param msg   提示信息
 * @param success   成功回调
 */
comment.post = function (url, params, msg, success) {
    if (msg == null)
        msg = '加载中...';
    $.blockUI(
        {
            message: msg,
            baseZ: 2000,
            css: {
                border: 'none',
                padding: '15px',
                backgroundColor: 'white',
                width: "300px",
                opacity: .5,
                color: 'black'
            }
        });
    $.ajax({
        type: "POST",
        url: url,
        data: params,
        contentType:"application/json",
        success: function(data){
            $.unblockUI();
            if (success != null) {
                if (data.state == 1 && data.state != undefined  ) {
                    if (data.res.code == 40000){
                        success(data);
                    }else{
                        hint(data.res.msg);
                    }
                } else {
                    hint(data.res.msg);
                }
            }

        }
    });
}




// 验证用户是否拥有权限
comment.verifyRight = function ( id ) {

    var is = false;
    // 用户
    var  userId = sessionStorage.getItem("userId");
    if(userId == null || userId == "" ){
        hint("请重新登陆！");
        setTimeout(function(){
            location.href = "login.html";
        } , 3000);
        return;
    }
    // token
    var  token = sessionStorage.getItem("token");
    if(token == null || token == "" ){
        hint("请重新登陆！");
        setTimeout(function(){
            location.href = "login.html";
        } , 3000);
        return;
    }

    var url = security + verifyRight+"?token="+token;
    var  params = {
        userId:userId ,
        resourceId:id
    }

    $.ajax({
        type: "POST",
        url: url,
        data:  JSON.stringify(params),
        async:false,
        dataType: 'json',
        contentType:"application/json",
        success: function(data){

            if (data.state == 1 && data.state != undefined  ) {
                if (data.res.code == 40000){
                    is =  true;
                }else{
                    is = false;
                }
            } else {
                is = false;
            }
        }

    });

    return is;
}




comment.getToken = function ( id ) {
    // token
    var  token = sessionStorage.getItem("token");
    if(token == null || token == "" ){
        hint("请重新登陆！");
        setTimeout(function(){
            location.href = "../../index.html";
        } , 3000);
        return;
    }
    return token;
}








/**
 * 判断输入是否为数字
 * @param v
 * @returns {boolean}
 */
function isNumber(str) {
    var reg = parseFloat(str);
    var re = /^\+?[0-9][0-9]*$/;
    if (!re.test(str)) {
        return false;
    }
    return !isNaN(reg);
}




/**
 * 非空验证
 * @param v
 * @returns {boolean}
 */
function isNull(v) {
    if(v==undefined || v=="" || v==null){
        return true;
    }
    return false;
}











