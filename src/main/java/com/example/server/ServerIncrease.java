package com.example.server;

import com.example.ServerCfg;
import com.example.rpc.Message;
import com.example.rpc.Payload;
import com.example.utils.MessageIOUtils;
import com.example.utils.PayloadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.out;

/**
 * @author yanshilong5@jd.com
 * @date 2020/3/10 0:59
 * @since JDK 1.8
 * desc：服务器端主服务
 */
public class ServerIncrease {
    private static final Logger logger = LogManager.getLogger(ServerIncrease.class);
    private ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocateDirect(1024);
    private Selector selector;

    public ServerIncrease() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //设置非阻塞模式
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(ServerCfg.portNumber));

        //打开 selector
        this.selector = Selector.open();

        //在 selector 注册感兴趣的事件
        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

    private void start() throws Exception{

        while(true){
            //调用阻塞的select,等待 selector上注册的事件发生
            this.selector.select();

            //获取就绪事件
            Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                //先移除该事件,避免重复通知
                iterator.remove();
                // 新连接
                if(selectionKey.isAcceptable()){
                    out.println("isAcceptable");
                    ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();

                    // 新注册channel
                    SocketChannel socketChannel  = server.accept();
                    if(socketChannel==null){
                        continue;
                    }
                    //非阻塞模式
                    socketChannel.configureBlocking(false);

                    //注册读事件（服务端一般不注册 可写事件）
                    socketChannel.register(selector, SelectionKey.OP_READ);


                    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                    buffer.put("hi new channel".getBytes());
                    buffer.flip();
                    int writeBytes= socketChannel.write(buffer);

                }

                // 服务端关心的可读，意味着有数据从client传来了数据
                if(selectionKey.isReadable()){

                    //读请求
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    readBuffer.clear();
                    socketChannel.read(readBuffer);
                    readBuffer.flip();

                    String receiveData= "server发送的消息";
                    //这里将收到的数据发回给客户端
                    writeBuffer.clear();
                    writeBuffer.put(receiveData.getBytes());
                    writeBuffer.flip();
                    while(writeBuffer.hasRemaining()){
                        //防止写缓冲区满，需要检测是否完全写入
                        out.println("写入数据:"+socketChannel.write(writeBuffer));
                    }
                }

            }
        }
    }

    public static void main(String[] args) throws Exception{
        new ServerIncrease().start();
    }

}
