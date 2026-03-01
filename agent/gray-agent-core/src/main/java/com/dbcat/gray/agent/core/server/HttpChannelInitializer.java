package com.dbcat.gray.agent.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @author Blackfost
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(20 * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast("httpRequestHandler", new HttpInboundHandler());
    }
}
