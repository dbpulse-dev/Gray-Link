package com.dbcat.gray.agent.core.server;


import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.server.endpoint.Endpoint;
import com.dbcat.gray.agent.core.server.endpoint.EndpointManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Blackfost
 */
@ChannelHandler.Sharable
public class HttpInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final ILog log = LogManager.getLogger(HttpInboundHandler.class);
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        //收到http请求
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        String requestUri = queryStringDecoder.rawPath();
        String method = request.method().name();
        if (!"POST".equals(method.toUpperCase())) {
            complete(ctx, request, RestResult.buildFailure(HttpResponseStatus.METHOD_NOT_ALLOWED.toString(), HttpResponseStatus.METHOD_NOT_ALLOWED.code()));
            return;
        }
        //通过url配置接口对应用实现
        Endpoint endpoint = EndpointManager.getEndpoint(requestUri);
        if (endpoint == null) {
            complete(ctx, request, RestResult.buildFailure(HttpResponseStatus.NOT_FOUND.toString(), HttpResponseStatus.NOT_FOUND.code()));
            return;
        }
        Throwable throwable = null;
        RestResult result = null;
        try {
            ByteBuf content = request.content();
            byte[] requestBodyBytes = new byte[content.readableBytes()];
            content.readBytes(requestBodyBytes);
            String requestBodyString = new String(requestBodyBytes, "UTF-8");
            ParameterizedType parameterizedType = ((ParameterizedType) endpoint.getClass().getGenericInterfaces()[0]);
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Object data = GSON.fromJson(requestBodyString, actualTypeArguments[0]);
            //调用相应的接口
            result = endpoint.invoke(data);
        } catch (Throwable e) {
            log.error(String.format("接口响应异常%s", requestUri), e);
            throwable = e;
        } finally {
            if (throwable == null) {
                complete(ctx, request, result);
            } else {
                complete(ctx, request, RestResult.buildFailure(throwable.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR.code()));
            }
        }
    }

    private void complete(ChannelHandlerContext ctx, FullHttpRequest request, RestResult<?> endPointResult) {
        //转化为json响应
        String jsonResult = GSON.toJson(endPointResult);
        if (jsonResult == null) {
            jsonResult = "{\"code\":500,\"message\":\"body转化错误！\"}";
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(endPointResult.getCode()),
                Unpooled.copiedBuffer(jsonResult, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        boolean keepAlive = isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        if (!keepAlive) {
            channelFuture.addListener(CLOSE);
        }
    }


    private static final ChannelFutureListener CLOSE = future -> {
        log.debug("关闭通道; {}", future.channel().toString());
        future.channel().close();
    };

    private boolean isKeepAlive(FullHttpRequest request) {
        return HttpUtil.isKeepAlive(request);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
