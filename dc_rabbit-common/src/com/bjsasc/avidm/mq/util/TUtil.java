package com.bjsasc.avidm.mq.util;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.context.ConnectionContext;
import com.bjsasc.avidm.mq.context.Context;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class TUtil {
	private static Logger logger = Logger.getLogger(TUtil.class);

	public static void close(Channel channel) {
		if (channel == null) {
			return;
		}

		try {
			channel.close();
		} catch (Exception e) {
			logger.debug("关闭channel出现异常", e);
		}
	}

	public static void close(Connection conn) {
		if (conn == null) {
			return;
		}

		try {
			conn.close();
		} catch (Exception e) {
			logger.debug("关闭connection出现异常", e);
		}
	}

	// 同时关闭channel和connection
	public static void close(ConnectionContext ctx) {
		if (ctx == null) {
			return;
		}

		try {
			close(ctx.getChannel());
			close(ctx.getConnection());
		} catch (Exception e) {
			logger.debug("关闭connection出现异常", e);
		}
	}

	public static JSONObject getData(Context ctx) throws Exception {
		JSONObject obj = null;
		if (ctx == null) {
			return new JSONObject();
		}

		String s = new String(ctx.getBody(), "UTF-8");
		obj = new JSONObject(s);

		return obj;
	}

}
