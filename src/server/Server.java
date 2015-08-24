import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.nio.channels.FileChannel;

public class Server {
    private int port = 9527;
    private Selector selector;

    public Server(int port) {
        this.port = port;
    }

	public static void main(String args[]) throws Exception {
        Server srv = new Server(9527);
        srv.Run();
	}

    public void init() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        this.selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    void listen() {

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                System.out.println("no channel to be process");
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            for (SelectionKey key: selectedKeys) {
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    Client client = new Client(socketChannel);
                    SelectionKey clientKey = socketChannel.register(selector, SelectionKey.OP_READ);
                    clientKey.attach(client);
                } else if (key.isReadable() {
                    Client client = key.attatchment();
                    client.execute(key);
                }
            }

            selectedKeys.clear();
        }
    }
}

