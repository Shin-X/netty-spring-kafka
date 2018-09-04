/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zbum.example.socket.server.netty.handler;

import com.zbum.example.socket.server.kafka.producer.Producer;
import com.zbum.example.socket.server.netty.ChannelRepository;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * event handler to process receiving messages
 *
 * @author Jibeom Jung
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class SomethingServerHandler extends ChannelInboundHandlerAdapter {

    private final ChannelRepository channelRepository;
    @Autowired
    private Producer  producer;

    private static final Logger logger= Logger.getLogger("netty");



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");

        ctx.fireChannelActive();
        if (log.isDebugEnabled()) {
            log.debug(ctx.channel().remoteAddress() + "");
        }
        String channelKey = ctx.channel().remoteAddress().toString();
        channelRepository.put(channelKey, ctx.channel());

        ctx.writeAndFlush("Your channel key is " + channelKey + "\r\n");

        if (log.isDebugEnabled()) {
            log.debug("Binded Channel Count is {}", this.channelRepository.size());
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //String stringMessage = (String) msg;

//        super.channelRead(ctx, msg);
//        ((ByteBuf) msg).release();
        //tcp请求返回ACK字符串
        ByteBuf out = Unpooled.directBuffer(16);
        out.writeCharSequence("ACK", CharsetUtil.UTF_8);
        ctx.write(out); // (1)
        ctx.flush(); // (2)

        //System.out.println("client channelRead..");
        ByteBuf bytebufmsg = (ByteBuf)msg;
        ByteBuf buf1 = bytebufmsg.readBytes(bytebufmsg.readableBytes());
        //控制台打印收到的消息
        // System.out.println("Client received:" + ByteBufUtil.hexDump(buf1));

        String strTosend = ByteBufUtil.hexDump(buf1);
       // System.out.println(strTosend);
        //netty推送得到kafka produce
        logger.info("nettyMsg:"+strTosend);
        producer.send(strTosend+"0a");


        //Consumer.consumerMsg();


      /*  if (log.isDebugEnabled()) {
            log.debug(strTosend);
        }*/

       /* String[] splitMessage = strTosend.split("::");

        if ( splitMessage.length != 2 ) {
            ctx.channel().writeAndFlush(strTosend + "\n\r");
            return;
        }

        if ( channelRepository.get(splitMessage[0]) != null ) {
            channelRepository.get(splitMessage[0]).writeAndFlush(splitMessage[1] + "\n\r");
        }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        Assert.notNull(ctx, "[Assertion failed] - ChannelHandlerContext is required; it must not be null");

        String channelKey = ctx.channel().remoteAddress().toString();
        this.channelRepository.remove(channelKey);
        if (log.isDebugEnabled()) {
            log.debug("Binded Channel Count is " + this.channelRepository.size());
        }
    }
}
