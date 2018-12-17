<%--
  Created by IntelliJ IDEA.
  User: chicunxiang
  Date: 2018/12/14
  Time: 上午11:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
Welcome<br/>
用户名<input id="name" type="text"/>
<button onclick="clearMsg()">清空消息</button>
<br/>
<input id="text" type="text"/>
<button onclick="send()">发送消息</button>
<hr/>
<button onclick="closeWebSocket()">关闭WebSocket连接</button>
<hr/>
<div id="message"></div>
</body>
<script type="text/javascript">
    var websocket = null;
    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://129.204.64.4:80/chat/websocket/web");
        // websocket = new WebSocket("ws://192.168.1.8:8080/Test02/websocket/web");
        // websocket.connect();
    }
    else {
        alert('当前浏览器 Not support websocket');
    }
    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("WebSocket连接发生错误")
    };
    //连接成功建立的回调方法
    websocket.onopen = function () {
        setMessageInnerHTML("WebSocket连接成功");
    };
    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        setMessageInnerHTML(event.data)
    };
    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML("WebSocket连接关闭")
    };
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket()
    };

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        var innerHTML1 = document.getElementById('message').innerHTML;
        document.getElementById('message').innerHTML = innerHTML + '<br/>' + innerHTML1;
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close()
    }

    function clearMsg() {
        document.getElementById('message').innerHTML = '';
    }

    //发送消息
    function send() {
        var message = document.getElementById('text').value;
        var name = document.getElementById('name').value;
        websocket.send(JSON.stringify({
            name: !!name ? name : "李鬼",
            content: message
        }));

    }
</script>
</html>
