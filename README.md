# websocket

[点击访问聊天室](https://www.ccx1.top/chat/chat.jsp)

 主要处理核心是后端

 聊天室思想

需要一个长久的Map来记录当前Session所在的位置,根据java特性,需要一个静态的,这样是随着类的加载而加载.并且需要一个set来存储不重复的session

一个session代表一个用户

map所在的key代表一个房间,set代表这个房间内所存在的人数

	Set<Session> sessions = stringSetMap.get(user);
        if (sessions == null) {
            sessions = new HashSet<>();
        }
        this.session = session;
        this.user = user;
        sessions.add(session);
        stringSetMap.put(user, sessions);
        stringSetMap.put("blackroom", stringSetMap.get("blackroom") == null ? new HashSet<>() : stringSetMap.get("blackroom"));

并且创建小黑屋,如果黑屋存在,则不创建,如果不存在,则重新创建

核心思想就是将所有的session统一管理起来,session的id则为每个用户的唯一标识

发送消息给此房间内的所有人

	Set<Session> sessions = stringSetMap.get(user);
        for (Session client : sessions) {
            synchronized (ChatServer.class) {
                client.getAsyncRemote().sendText(text);
            }
        }
