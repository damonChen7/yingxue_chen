<%@page pageEncoding="UTF-8" isELIgnored="false" contentType="text/html; UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}"/>
<script>
    $(function () {
        //点击搜索
        $("#searchId").click(function () {

            //清空表单
            $("#showContentId").empty();
            //获取输入内容
            var content = $("#contentId").val();

            //清空输入框
            $("#contentId").val("");

            //发送请求查询结果
            $.ajax({
                url: "${path}/video/searchVdieo",
                type: "post",
                dataType: "JSON",
                data: {"content": content},
                success: function (data) {

                    //添加目录行
                    $("#showContentId").append(
                        "<tbody>" +
                        "<td>ID</td>" +
                        "<td>标题</td>" +
                        "<td>描述</td>" +
                        "<td>封面</td>" +
                        "<td>发布时间</td>" +
                        "</tbody>"
                    );


                    //遍历几何数据  渲染页面
                    $.each(data, function (index, video) {
                        $("#showContentId").append(
                            "<tr>" +
                            "<td>" + video.id + "</td>" +
                            "<td>" + video.title + "</td>" +
                            "<td>" + video.description + "</td>" +
                            "<td><img src='" + video.coverPath + "' width='400px' heigt='800px'></td>" +
                            "<td>" + video.uploadTime + "</td>" +
                            "</tr>"
                        )
                    });
                }
            });
        });
    });
</script>

<%--搜索框--%>
<div align="center">
    <div class="input-group" style="width: 300px">
        <input id="contentId" type="text" class="form-control" placeholder="请输入检索内容" aria-describedby="basic-addon2">
        <span class="input-group-btn" id="basic-addon2">
            <button class="btn btn-warning" id="searchId">点击搜索</button>
        </span>
    </div>
    <br>
</div>


<%--搜索内容--%>
<div class="panel panel-warning">
    <%--面板标题--%>
    <div class="panel panel-heading">搜索结果</div>

    <%--面板内容--%>
    <table class="table" id="showContentId"/>
</div>