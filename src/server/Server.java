import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.util.Set;

public class Server {
    private int port = 9527;
    private Selector selector;

    public Server(int port) {
        this.port = port;
    }

	public static void main(String args[]) throws Exception {
        try {
            Server srv = new Server(9527);
            srv.init();
            srv.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public void init() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        this.selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    void listen() throws IOException {

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                System.out.println("no channel to be process");
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            for (SelectionKey key: selectedKeys) {
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    User user = new User(socketChannel);
                    SelectionKey userKey = socketChannel.register(selector, SelectionKey.OP_READ);
                    userKey.attach(user);
                } else if (key.isReadable()) {
                    User user = (User)key.attachment();
                    if (user == null) {
                        System.out.println("attachment failed, no object attached at SelectionKey");
                        continue;
                    }
                    user.execute(key);
                }
            }

            selectedKeys.clear();
        }
    }
}

