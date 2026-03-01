package com.dbcat.gray.agent.core.server;


import com.dbcat.gray.agent.core.boot.BootService;
import com.dbcat.gray.agent.core.boot.DefaultImplementor;
import com.dbcat.gray.agent.core.boot.DefaultNamedThreadFactory;
import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.util.RunnableWithExceptionProtection;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Blackfost
 */
@DefaultImplementor
public class HttpServer implements BootService {

    private static ILog LOGGER = LogManager.getLogger(HttpServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    @Override
    public void prepare() {
        SimpleChannelInboundHandler agentServerHttpInboundHandler = new HttpInboundHandler();
    }

    @Override
    public void boot() {
        Thread serverThread = new Thread(new RunnableWithExceptionProtection(() -> {
            LOGGER.info("dbcat gray agent netty server.");
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            bossGroup = new NioEventLoopGroup(1, new DefaultNamedThreadFactory("server-boss"));
            workerGroup = new NioEventLoopGroup(4, new DefaultNamedThreadFactory("server-worker"));
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new HttpChannelInitializer());
            int port = Config.Agent.SERVER_PORT;
            try {
                ChannelFuture f = serverBootstrap.bind(port).sync();
                LOGGER.info("dbcat gray agent server started, port is {}.", port);
                f.channel().closeFuture().sync();
                LOGGER.info("dbcat gray agent server closed, port is {}.", port);
            } catch (InterruptedException e) {
                LOGGER.error("dbcat gray agent server start failed", e);
                Thread.currentThread().interrupt();
            }
        }, t -> {
        }), "gray-agent-server");
        serverThread.setDaemon(true);
        serverThread.start();
    }


    @Override
    public void onComplete() {

    }

    @Override
    public void shutdown() {
        LOGGER.info("dbcat gray agent server shutdown");
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
    }
}
