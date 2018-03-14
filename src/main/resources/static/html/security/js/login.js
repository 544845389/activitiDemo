/**
 * Created by houcunlu on 2017/8/24.
 */


/**
 * 登陆
 */
$("#submit").click(function (){
    // 账号
    var userName =   $.trim($("input[name=userName]").val());
    // 密码
    var userPwd =   $.trim($("input[name=userPwd]").val());

    if(isNull(userName)){
        hint("账号不可为空！");
        return;
    }

    if(isNull(userPwd)){
        hint("密码不可为空！");
        return;
    }

    // 参数
    var  params = {
        userName:userName ,
        userPwd:userPwd
    }
    // 登陆
    var loginUrl = backUrl + login;
    comment.post( loginUrl  , JSON.stringify(params)  , "登陆中，，，", function (res) {
         sessionStorage.setItem("userName",userName);
         location.href = "/html/security/html/index.html";

    });

})




/*生成4位随机数*/
function rand(){
    var str="abcdefghijklmnopqrstuvwxyz0123456789";
    var arr=str.split("");
    var validate="";
    var ranNum;
    for(var i=0;i<4;i++){
        ranNum=Math.floor(Math.random()*36);   //随机数在[0,35]之间
        validate+=arr[ranNum];
    }
    return validate;
}

/*干扰线的随机x坐标值*/
function lineX(){
    var ranLineX=Math.floor(Math.random()*90);
    return ranLineX;
}

/*干扰线的随机y坐标值*/
function lineY(){
    var ranLineY=Math.floor(Math.random()*40);
    return ranLineY;
}






































