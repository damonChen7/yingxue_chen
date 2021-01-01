<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="path" value="${pageContext.request.contextPath}"/>

<script>
    //延迟加载
    $(function () {
        pageInit();
        //删除类别
        $("#deleteCategory").click(function () {

            //getGridParam:返回请求的参数信息  id
            //selrow:只读属性，最后选择行的id
            var rowId = $("#cateTable").jqGrid("getGridParam", "selrow");

            //选择一行删除
            if (rowId != null) {
                $.post("${path}/category/edit", {"id": rowId, "oper": "del"}, function (data) {
                    //向提示框设置内容
                    $("#showMsg").html("<span>" + data.message + "</span>");
                    //展示提示框
                    $("#showMessage").show();
                    //设置倒计时关闭
                    setTimeout(function (data) {
                        //关闭提示框
                        $("#showMessage").hide();
                    }, 2000);

                    //刷新表单
                    $("#cateTable").trigger("reloadGrid");
                })
            } else {
                //向提示框设置内容
                $("#showMsg").html("<span>请选择一行</span>");
                //展示提示框
                $("#showMessage").show();
                //设置倒计时关闭
                setTimeout(function (data) {
                    //关闭提示框
                    $("#showMessage").hide();
                }, 2000);
            }

        });
    });

    //创建表格
    function pageInit() {
        $("#cateTable").jqGrid({
            url: "${path}/category/queryOneCategory",
            editurl: "${path}/category/edit",
            datatype: "json",
            height: "auto",
            autowidth: true,
            styleUI: "Bootstrap",
            rowNum: 8,
            rowList: [8, 10, 20, 30],
            pager: '#catePage',
            viewrecords: true,
            colNames: ['Id', '类别名', '级别', '上级别id'],
            colModel: [
                {name: 'id', index: 'id', width: 55},
                {name: 'cateName', editable: true, index: 'invdate', width: 90},
                {name: 'levels', index: 'name', width: 100},
                {name: 'parentId', index: 'note', width: 150, sortable: false}
            ],
            subGrid: true, //开启子表格
            // subgrid_id是在创建表数据时创建的div标签的ID
            //点击二级类别按钮，动态创建一个div,此div来容纳子表格，subgrid_id就是这个div的id
            // row_id是该行的ID
            subGridRowExpanded: function (subgrid_id, row_id) {
                addSubGird(subgrid_id, row_id);
            }
        });
        //分页工具栏
        $("#cateTable").jqGrid('navGrid', '#catePage', {
                add: true,
                edit: true,
                del: true,
                addtext: "添加",
                edittext: "编辑",
                deltext: "删除"
            },
            {
                closeAfterEdit: true,  //关闭对话框
            },
            {
                closeAfterAdd: true,  //关闭对话框
            },
            {
                closeAfterDel: true,  //关闭对话框
                afterSubmit: function (data) {
                    //向提示框设置内容
                    $("#showMsg").html("<span>" + data.responseJSON.message + "</span>");
                    //展示提示框
                    $("#showMessage").show();
                    //设置倒计时关闭
                    setTimeout(function () {
                        //关闭提示框
                        $("#showMessage").hide();
                    }, 2000);

                    return "aaa";
                }
            }
        );
    }

    //创建子表格
    function addSubGird(subgridId, rowId) {

        //声明两个变量
        // 子表格table的id
        var subgridTableId = subgridId + "Table";
        // 子表格分页工具栏的id
        var pagerId = subgridId + "Page";

        //在div中创建子表格和分页工具栏
        $("#" + subgridId).html("<table id=" + subgridTableId + " /><div id=" + pagerId + " />");

        //初始化子表格
        $("#" + subgridTableId).jqGrid({
            url: "${path}/category/queryTwoCategory?parentId=" + rowId,
            editurl: "${path}/category/edit?parentId=" + rowId,
            datatype: "json",
            rowNum: 20,
            pager: "#" + pagerId,
            height: "auto",
            autowidth: true,
            styleUI: "Bootstrap",
            colNames: ['Id', '类别名', '级别', '上级别id'],
            colModel: [
                {name: 'id', index: 'id', width: 55},
                {name: 'cateName', editable: true, index: 'invdate', width: 90},
                {name: 'levels', index: 'name', width: 100},
                {name: 'parentId', index: 'note', width: 150, sortable: false}
            ]
        });

        //子表格的分页工具栏
        $("#" + subgridTableId).jqGrid('navGrid', "#" + pagerId, {
                edit: true,
                add: true,
                del: true,
                addtext: "添加",
                edittext: "编辑",
                deltext: "删除"
            },
            {
                closeAfterEdit: true,  //关闭对话框
            },
            {
                closeAfterAdd: true,  //关闭对话框
            },
            {}
        );
    }


</script>

<%--创建一个面板--%>
<div class="panel panel-success">

    <%--面板头--%>
    <div class="panel panel-heading">
        <span>类别信息</span>
    </div>

    <!-- Nav tabs -->
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab"
                                                  data-toggle="tab">类别信息</a></li>
    </ul>
    <br>

    <%--警告框--%>
    <div id="showMessage" style="width:300px;display: none " class="alert alert-warning alert-dismissible" role="alert">
        <strong id="showMsg"/>
    </div>

    <div>
        <button class="btn btn-success" id="deleteCategory">删除父类别</button>&emsp;&emsp;
        <button class="btn btn-success">展示类别详情</button>
    </div>
    <br>

    <%--创建表格--%>
    <table id="cateTable"/>

    <%--分页工具栏--%>
    <div id="catePage"/>

</div>