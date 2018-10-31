package com.netty.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Lance Sun
 * @date Created in 11:21 2018/10/29
 * @description ByteBuf 使用模式
 */
public class ByteBufUse {
    /**
     * 1.堆缓冲区
     */
    public void backingArray() {
        ByteBuf byteBuf = Unpooled.buffer();
        if (byteBuf.hasArray()) {
            byte[] array = byteBuf.array();
            int offset = byteBuf.arrayOffset() + byteBuf.readerIndex();
            int length = byteBuf.readableBytes();
            handleArray(array, offset, length);
        }
    }

    /**
     * 3.复合缓冲区
     */
    public void compositeByteBuf() {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf headerBuf = Unpooled.buffer();
        ByteBuf bodyBuf = Unpooled.buffer();
        messageBuf.addComponents(headerBuf, bodyBuf);
        //删除位于索引位置为0的ByteBuf
        messageBuf.removeComponent(0);
        //循环遍历所有的buf实例
        for (ByteBuf buf : messageBuf) {
            System.out.println(buf.toString());
        }
    }

    private static void handleArray(byte[] array, int offset, int length) {

    }

    public static void main(String[] args) {

    }
}
