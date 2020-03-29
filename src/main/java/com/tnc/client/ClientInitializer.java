package com.tnc.client;

import com.tnc.client.handler.ClientInboundHandler;
import com.tnc.client.handler.ClientOutboundHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class ClientInitializer extends ChannelInitializer<Channel> {
	private ChannelHandlerContext channelHandlerContext;
	private Object msg;

	public ClientInitializer(ChannelHandlerContext channelHandlerContext, Object msg) {
		this.channelHandlerContext = channelHandlerContext;
		this.msg = msg;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("clientOutboundHandler", new ClientOutboundHandler());
		pipeline.addLast("clientInboundHandler", new ClientInboundHandler(channelHandlerContext, msg));

	}
}
