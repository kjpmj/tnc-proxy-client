package com.tnc.client;

import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

public class ClientBootstrapper {
	Logger logger = LoggerFactory.getLogger(ClientBootstrapper.class);

	public void init(ChannelHandlerContext channelHandlerContext, Map<String, String> map, Object msg) throws Exception{
		// 목적지 호스트, 포트 유효성 검사
		if(!checkParams(map, channelHandlerContext)) {
			return;
		};
		
		String destHost = map.get("destHost");
		int destPort = Integer.parseInt(map.get("destPort"));
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class).group(channelHandlerContext.channel().eventLoop())
				.handler(new ClientInitializer(channelHandlerContext, msg));

//		ChannelFuture future = bootstrap.connect(new InetSocketAddress(destHost, destPort));
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8080));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				logger.info("Client future isDone: " + channelFuture.isDone());
				logger.info("Client future isSuccess: " + channelFuture.isSuccess());

				if (!channelFuture.isSuccess()) {
					channelHandlerContext.close();
				}
			}
		});
	}
	
	private boolean checkParams(Map<String, String> map, ChannelHandlerContext channelHandlerContext) {
		String destHost = map.get("destHost");
		String destPort = map.get("destPort");
		
		if(StringUtil.isNullOrEmpty(destHost)|| StringUtil.isNullOrEmpty(destPort)) {
			channelHandlerContext.write(Unpooled.copiedBuffer("목적지 호스트 또는 포트 미설정", CharsetUtil.UTF_8));
			return false;
		}
		
		try {
			Integer.parseInt(map.get("destPort"));
		} catch (NumberFormatException e) {
			channelHandlerContext.write(Unpooled.copiedBuffer("목적지 호스트 또는 포트 미설정", CharsetUtil.UTF_8));
			return false;
		}
		
		return true;
	}
}
