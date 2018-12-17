<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/6/5
  Time: 22:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传文件</title>
</head>
<body>
<form method="post" action="/Test02/upload" enctype="multipart/form-data">

    <label>
        <input name="username">
    </label>
    <input type="file" name="userfile">

    <input type="submit">
</form>

</body>
</html>
