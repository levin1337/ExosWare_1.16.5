package ru.levinov.ui.duplicateMain;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CopyProxy {
    private final String remoteHost;
    private final int remotePort;

    public CopyProxy(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public void start(int localPort) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(4);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ProxyHandler(remoteHost, remotePort));
                        }
                    });

            ChannelFuture f = b.bind(localPort).sync();
            System.out.println("Прокси-сервер запущен на порту: " + localPort);
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

class ProxyHandler extends ChannelInboundHandlerAdapter {
    private final String remoteHost;
    private final int remotePort;
    private Channel outboundChannel;
    private Channel observerChannel;

    public ProxyHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ForwardingHandler(ctx.channel()));
                    }
                });
        //Целевой
        outboundChannel = b.connect(remoteHost, remotePort).sync().channel();
        System.out.println("Подключение к серверу: " + remoteHost + ":" + remotePort);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
       //     System.err.println("");
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            outboundChannel.close();
        }
        System.out.println("Клиент отключился: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

class ForwardingHandler extends ChannelInboundHandlerAdapter {
    private final Channel inboundChannel;

    public ForwardingHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        inboundChannel.writeAndFlush(msg).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        inboundChannel.close();
        System.out.println("Закрто вам туда нещя");
    }
}
