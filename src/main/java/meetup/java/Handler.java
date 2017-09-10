/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package meetup.java;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.nio.charset.StandardCharsets;

/**
 * ChannelHandler and callbacks:
 */
public class Handler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof HttpRequest) {
      HttpRequest req = (HttpRequest) msg;

      boolean keepAlive = HttpUtil.isKeepAlive(req);
      FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
          Unpooled.wrappedBuffer("Hola Java Meetup!".getBytes(
              StandardCharsets.UTF_8)));

      response.headers().set("Content-Type", "text/plain");
      response.headers().setInt("Content-Length", response.content().readableBytes());

      if (!keepAlive) {
        /** Channel Future: */
        ctx.write(response)
            .addListener(ChannelFutureListener.CLOSE);
      } else {
        response.headers().set("Connection", "keep-alive");
        ctx.write(response);
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
