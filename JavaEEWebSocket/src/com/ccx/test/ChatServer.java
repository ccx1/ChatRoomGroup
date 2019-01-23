package com.ccx.test;

import org.json.JSONObject;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ServerEndpoint("/websocket")
public class ChatServer {
    private static Map<String, Set<Session>> stringSetMap = new HashMap<>();
    private        Session                   session;
    private        String                    name         = "";
    private        String                    room;
    private        long                      l;

    @OnOpen
    public void open(Session session) throws UnsupportedEncodingException {
        String room = new String(session.getQueryString().getBytes("iso-8859-1"), "utf-8");
        room = room.split("=")[1];
        Set<Session> sessions = stringSetMap.get(room);
        if (sessions == null) {
            sessions = new HashSet<>();
        }
        this.session = session;
        this.room = room;
        sessions.add(session);
        stringSetMap.put(room, sessions);
        stringSetMap.put("blackroom", stringSetMap.get("blackroom") == null ? new HashSet<>() : stringSetMap.get("blackroom"));
        String     gbk  = URLDecoder.decode(room, "UTF-8");
        JSONObject json = new JSONObject();
        json.put("content", "欢迎进入" + gbk + "房间");
        json.put("size", stringSetMap.get(room).size());
        sendToAll(json.toString());
    }

    @OnMessage
    public void onMessage(String s) {
        JSONObject jsonObject = new JSONObject(s);
        if (jsonObject.getString("type").equals("ping")) {
            sendTo("ping");
            return;
        }
        if (System.currentTimeMillis() - l > 1000 || System.currentTimeMillis() - l < 0) {
            if (room.equals("web")) {
                l = System.currentTimeMillis();
            } else {
                l = System.currentTimeMillis() + 60 * 1000 * 60;
            }
            String name = jsonObject.getString("name");
            if (this.name.isEmpty() || !this.name.equals(name)) {
                this.name = jsonObject.getString("name");
            }
            String content = jsonObject.getString("content");
            if (room.equals("web")) {
                // 防止js注入
                content = content.replace("<", " ").replace(">", "").replace(".{", "");

            }
            JSONObject json = new JSONObject();
            json.put("content", "[用户 " + name + "说]" + content);
            json.put("size", stringSetMap.get(room).size());
            sendToAll(json.toString());
        } else {
            sendTo("不能频繁发送");
        }
    }


    @OnClose
    public void onClose() {
        Set<Session> sessions = stringSetMap.get(room);
        sessions.remove(session);
        JSONObject json = new JSONObject();
        json.put("content", this.name + " 离开房间 ！");
        json.put("size", stringSetMap.get(room).size());
        sendToAll(json.toString());
        sendToAll(json.toString());

    }


    // 发送给所有的聊天者
    private void sendToAll(String text) {
        Set<Session> sessions = stringSetMap.get(room);
        for (Session client : sessions) {
            synchronized (ChatServer.class) {
                client.getAsyncRemote().sendText(text);
            }
        }
    } // 发送给所有的聊天者

    private void sendTo(String text) {
        synchronized (ChatServer.class) {
            session.getAsyncRemote().sendText(text);
        }
    }
}
