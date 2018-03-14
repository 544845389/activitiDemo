/**
 * Created by houcunlu on 2017/8/24.
 */

/**
 * 页面加载
 */
$(function () {

    //加载菜单
    // loadMenu();

    // 绑定点击 事件
    click();

    userInfo();

});



function  userInfo(){
    // token
    var  userName = sessionStorage.getItem("userName");
    $("#userName1").text(userName);
    $("#userName2").text(userName);
    $("#userName3").text(userName);

}



// 添加子菜单
var addChildMenu = function (li, item) {
    if (item.childs.length > 0) {
        var ul = $("<ul class=\"treeview-menu\"></ul>");
        for (var i = 0; i < item.childs.length; i++) {
            var c = item.childs[i];
            var a = li.children("a");
            var mli = null;
            if (c.childs.length > 0) {
                mli = $("<li><a href=\"#\"><i class=\"" + c.icon + "\"></i>" + c.name + "<i class=\"fa fa-angle-left pull-right\"></i></a></li>");
                addChildMenu(mli, c);
            } else {
                var mpath = "";
                if (c.url == "" || c.url == "#") {
                    mpath = "#";
                }else{
                    mpath = c.url;
                }
                mli = $("<li><a   event-url=\"" + mpath + "\"  event-id=\""+ c.id+"\"  href=\"#\"><i class=\"" + c.icon + "\"></i>" + c.name + "</a></li>");
            }
            ul.append(mli);
            a.after(ul);
        }
    }
}


// 添加菜单
var loadMenu = function () {
    var  userId = sessionStorage.getItem("userId");
    var url = security +loadAdminMenu;
    var  params = {
        userId:userId
    }
    comment.post( url  , JSON.stringify(params) , '菜单加载中....', function (res) {
        var menu = $("#menu");
        if (res.state && res.res.code == 40000) {
            for (var i = 0; i < res.res.data.length; i++) {
                var item = res.res.data[i];
                var li = $("<li class=\"treeview\"><a href=\"#\"><i class=\"" + item.icon + "\"></i> <span>" + item.name + "</span> <i class=\"fa fa-angle-left pull-right\"></i></a></li>");
                addChildMenu(li, item);
                menu.append(li);
            }
        }
    });
}


//$('.main-sidebar').on('click','*[data-url]',function(){
//    var url = $(this).data('url');
//    $('iframe').attr('src',url);
//})



//菜单绑定事件
function  click(){
    //菜单跳转
    $('.main-sidebar').on('click','a[event-url]',function(){
        // 资源id
        var id = $(this).attr("event-id");
        // 资源地址
        var url = $(this).attr("event-url");
        if (url == "#") {
            return;
        }
        var url = $(this).attr('event-url');
        $('iframe').attr('src',url);
    });
}










