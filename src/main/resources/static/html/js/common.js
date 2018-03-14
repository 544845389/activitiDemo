var _this,modal;
function check() {
    var check = true;
    $.each($(this).closest('.modal-dialog').find('*[check-type="required"]'), function() {
        if ($(this).val() == '') {
            var message = $(this).attr('required-message');
            $(this).closest('div').removeClass('has-success').addClass('has-error');
            $(this).next('.help-block').html(message);
            check = false;
        }
    })
    return check;
}

//打开模态框
$(document).on('click', '*[data-to]', function() {
        _this = $(this).closest('tr');
        var target = $(this).data('target');
        window.parent.openModal($(target).clone(true),function(modalp){modal = modalp});
})

//禁用/解禁
function ban() {
    $(document).on('click', '.ban i', function() {
        if ($(this).hasClass('fa-circle-thin')) {
            $(this).removeClass('fa-circle-thin').addClass('fa-ban').attr('title', '禁用');
            $(this).closest('tr').find('.State').html('未解禁');
        } else if ($(this).hasClass('fa-ban')) {
            $(this).removeClass('fa-ban').addClass('fa-circle-thin').attr('title', '解禁');
            $(this).closest('tr').find('.State').html('禁用');
        }
    })
}


//右侧滑块
function slider(){
    $(document).on('click', '*[data-toggle="slider"]', function() {
        var target = $(this).data('target');
        $(target).toggleClass('slide-open');
    })
}


/**
 * 将列表中的行数据填充到表单中
 * @param frm
 * @param data
 */
var setRowToFrm = function (frmid, obj) {
    $("#" + frmid + "")[0].reset();
    var inputs = $("#" + frmid + " :input");//表单中的控件
    inputs.each(function () {
        if (obj != null && obj[this.name] != undefined) {
            this.value = obj[this.name];
        }
    });
}




//格式化日期,
function formatDate(date,format){
    var paddNum = function(num){
        num += "";
        return num.replace(/^(\d)$/,"0$1");
    }
    //指定格式字符
    var cfg = {
        yyyy : date.getFullYear() //年 : 4位
        ,yy : date.getFullYear().toString().substring(2)//年 : 2位
        ,M  : date.getMonth() + 1  //月 : 如果1位的时候不补0
        ,MM : paddNum(date.getMonth() + 1) //月 : 如果1位的时候补0
        ,d  : date.getDate()   //日 : 如果1位的时候不补0
        ,dd : paddNum(date.getDate())//日 : 如果1位的时候补0
        ,hh : date.getHours()  //时
        ,mm : date.getMinutes() //分
        ,ss : date.getSeconds() //秒
    }
    format || (format = "yyyy-MM-dd hh:mm:ss");
    return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});
}