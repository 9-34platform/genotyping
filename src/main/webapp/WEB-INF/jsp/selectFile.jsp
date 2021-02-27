<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<div>
    文件目录
    <hr />
    <%
        List<String> results = (List<String>) request.getAttribute("dirFile");
        for (String result: results) {
            out.print(result+"<br><br>");
        }
    %>
</div>
<div>
    <form action="/download">
        <input type="text" name="selectedFile" />
        <button type="submit">下载</button>
    </form>
</div>

</body>
</html>
