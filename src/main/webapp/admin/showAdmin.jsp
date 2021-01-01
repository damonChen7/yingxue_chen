<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="path" value="${pageContext.request.contextPath}"/>

<script>
    //延迟加载
    $(function () {
        pageInit();

        //删除管理员
        $("#deleteAdmin").click(function () {

            //getGridParam:返回请求的参数信息  id
            //selrow:只读属性，最后选择行的id
            var rowId = $("#adminTable").jqGrid("getGridParam", "selrow");

            //选择一行删除
            if (rowId != null) {
                $.post("${path}/admin/edit", {"id": rowId, "oper": "del"}, function (data) {
                    //刷新表单
                    $("#adminTable").trigger("reloadGrid");
                })
            } else {
                //向提示框设置内容
                $("#showMsgs").html("<span>请选择一行</span>");
                //展示提示框
                $("#showMessages").show();
                //设置倒计时关闭
                setTimeout(function () {
                    //关闭提示框
                    $("#showMessages").hide();
                }, 2000);
            }
        });
    });

    //创建表格
    function pageInit() {
        $("#adminTable").jqGrid({
            url: "${path}/admin/queryAdminPage",  //接收  page=当前页   rows=每页展示条数   返回  page=当前页   rows=[User,User]数据    tolal=总页数   records=总条数
            editurl: "${path}/admin/edit",  //增删改走的路径  oper:add ,edit,del
            datatype: "json", //数据格式
            rowNum: 2,  //每页展示条数
            rowList: [2, 10, 20, 30],  //可选每页展示条数
            pager: '#adminPage',  //分页工具栏
            sortname: 'id', //排序
            type: "post",  //请求方式
            styleUI: "Bootstrap", //使用Bootstrap
            autowidth: true,  //宽度自动
            height: "auto",   //高度自动
            viewrecords: true, //是否展示总条数
            colNames: ['Id', '管理员账号', '管理员密码', '状态', '盐'],
            colModel: [
                {name: 'id', width: 130},
                {name: 'username', editable: true, width: 55},
                {name: 'password', editable: true, width: 130, align: "center"},
                {
                    name: 'status', width: 50, align: "center",
                    formatter: function (cellvalue, options, rowObject) {
                        if (cellvalue == 1) {
                            //正常
                            return "<button class='btn btn-success' onclick='changeStatus(\"" + rowObject.id + "\",\"" + rowObject.status + "\")' >冻结</button>";
                        } else {
                            //冻结
                            return "<button class='btn btn-danger' onclick='changeStatus(\"" + rowObject.id + "\",\"" + rowObject.status + "\")' >解除冻结</button>";
                        }
                    }
                },
                {name: 'salt', width: 50},
            ]
        });

        //分页工具栏
        $("#adminTable").jqGrid('navGrid', '#adminPage',
            {edit: true, add: true, del: true, addtext: "添加", edittext: "编辑", deltext: "删除"},
            {
                closeAfterEdit: true,  //关闭对话框
            },  //修改之后的额外操作
            {
                closeAfterAdd: true,  //关闭对话框
            }, //添加之后的额外操作
            {}  //删除之后的额外操作
        );
    }

    //冻结
    function changeStatus(id, status) {
        if (status == 0) {
            $.post("${path}/admin/edit", {"id": id, "status": "1", "oper": "edit"}, function (data) {
                //刷新表单
                $("#adminTable").trigger("reloadGrid");
            })
        } else {
            $.post("${path}/admin/edit", {"id": id, "status": "0", "oper": "edit"}, function (data) {
                //刷新表单
                $("#adminTable").trigger("reloadGrid");
            })
        }
    }

</script>

<%--创建一个面板--%>
<div class="panel panel-danger">

    <%--面板头--%>
    <div class="panel panel-heading">
        <span>管理员信息</span>
    </div>

    <!-- Nav tabs -->
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab"
                                                  data-toggle="tab">管理员信息</a></li>
    </ul>
    <br>

    <%--警告框--%>
    <div id="showMessages" style="width:300px;display: none " class="alert alert-warning alert-dismissible"
         role="alert">
        <strong id="showMsgs"/>
    </div>

    <div>
        <button class="btn btn-danger">导出管理员信息</button>
        <button class="btn btn-danger" id="deleteAdmin">删除管理员</button>
    </div>
    <br>

    <%--创建表格--%>
    <table id="adminTable"/>

    <%--分页工具栏--%>
    <div id="adminPage"/>

</div>