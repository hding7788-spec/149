package com.bjsasc.avidm.mq.context;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

// 连接上下文
public class ConnectionContext {

	private Connection connection = null;
	private Channel channel = null;

	public ConnectionContext(Connection conn, Channel ch) {
		connection = conn;
		channel = ch;
	}

	public Connection getConnection() {
		return connection;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}
