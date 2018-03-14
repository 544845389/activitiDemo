/**
 * Created by yuliang on 2015/8/21.
 */
$(document).on("click", ".addImage", function () {
    var img = $(this);
    var maxSize = 0;//图片上传文件大小限制：默认2M限制
    var fileUploadUrl = File_Upload_Url;//图片上传路径
    var fileDownloadUrl = File_Download_Url;//图片访问路径

    try {
        maxSize = img.attr("maxSize");
    } catch (e) {
        maxSize = 2;
    }

    //创建input file控件，并且选择文件
    var createFile = function () {
        var file = $('<input type="file" name="file" style="display: none" accept="image/gif,image/jpg,image/jpeg,image/png"  />');
        file.trigger('click');
        file.change(function () {
            checkFile(file);
        });
    }

    //上传图片
    var uploadFile = function (file) {
        var form = $('<form method="post" enctype="multipart/form-data"></form>');
        var input = $('<input type="hidden" name="is300" value="0" />');
        form.append(file);
        form.append(input);
        $.blockUI(
            {
                message: '上传图片中...',
                baseZ: 2000,
                css: {
                    border: 'none',
                    padding: '15px',
                    backgroundColor: 'white',
                    width: "300px",
                    opacity: .5,
                    color: 'black'
                }
            }
        );
        form.ajaxSubmit({
            url: fileUploadUrl,
            success: function (data) {
                $.unblockUI();
                if (data != null && data != "") {
                    var imgs = data;
                    if (imgs != null && imgs.length > 0) {
                        var path = imgs[0].download;
                        var nUrl = fileDownloadUrl + path;
                        var isUpdate = img.attr("img-path") != null;
                        img.attr("img-isUpdate", isUpdate);//是否是更新
                        img.attr("img-url", nUrl);//绑定图片上传完以后的完整路径
                        img.attr("img-path", path);//绑定图片上传完以后的path地址

                        //回调结束事件
                        try {
                            img.trigger("loadImageAfter", img);
                        } catch (e) {
                            console.info(e.toString());
                        }
                    } else {
                        hint('上传失败.');
                    }
                } else {
                    hint('上传失败.');
                }
            },
            error: function (data) {
                $.unblockUI();
                hint(data);
            }
        });
    }


    //检查文件是否满足要求
    var checkFile = function (file) {

        var f = $(file).prop('files')[0];
        var name = f.name;
        var size = f.size;
        var type = f.type;

        //图片类型检查
        if (!(type == 'image/jpeg' || type == 'image/jpeg' || type == 'image/png' || type == 'image/gif' || type == 'image/JPG' || type == 'image/jpg')) {
            hint('图片类型错误，请选择图片文件类型.');
            return;
        }
        //图片大小检查
        var fSize = size / 1024 / 1024;
        if (fSize > maxSize) {
            hint('图片超过上传图片限制..');
            return;
        }

        //加载图片到浏览器
        if (typeof FileReader === 'undefined') {
            hint('Your browser does not support FileReader...');
            return;
        }
        var reader = new FileReader();
        reader.onload = function (e) {
            img.attr('src', this.result);
        }
        reader.readAsDataURL(f);

        //上传图片
        uploadFile(file);

    }

    createFile();

});