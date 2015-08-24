import java.io.FileOutputStream();

import binary.demo.Protocol;
import binary.demo.Util;

public class Client implements Protocol {
    private SocketChannel socketChannel;
    private ByteBuffer header = ByteBuffer.allocate(HEADER_LEN);
    private ByteBuffer body = ByteBuffer.allocate(4096);
    private Type pcketState = FILE_NAME;
    private FileChannel curFileChannel;
    private long fileLength;

    private int readState = 0; // 0: header, 1: body

    public Client(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void execute(SelectionKey key) {

        if (!key.isReadable) {
            System.out.println("invalid event type");
            socketChannel.Close();
            return;
        }

        boolean ok = false;
        switch (readState) {
            case 0:
                ok = readHeader();
                break;
            case 1:
                ok = readBody();
                break;
            default:
                break;
        }

        if (ok) {
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        } else {
            socketChannel.close();
        }
    }

    boolean readHeader() {
        //read packet header
        int bytes = socketChannel.read(header);
        if (bytes == -1) {
            System.out.println("client is closed when read packet header");
            return false;
        }

        if (header.position() != header.capacity() - 1) {
            //continue read header
            return true;
        }

        header.flip();

        //read a complete header, process header
        int packetLen = header.getInt();
        if (packetLen > MAX_LEN) {
            System.out.println("invalid packet len");
            return false;
        }

        short packetType = header.getShort();
        if (packetType != pcketState.getValue()) {
            System.out.println("invalid packet type");
            return false;
        }

        body.limit(packetLen());
        readState = 1; //transform readState to body

        return true;
    }

    boolean readBody() {
        //read packet body
        int bytes = socketChannel.read(body);
        if (bytes == -1) {
            System.out.println("client is closed when read packet body");
            return false;
        }

        if (body.position() != body.capacity() - 1) {
            //continue to read body
            return true;
        }

        boolean ok = processBody();

        //switch to read header
        readState = 0;
        header.clear();
        body.clear();

        return ok;
    }

    boolean processBody() {
        boolean ok = true;
        //read a completed body, process body
        switch (packetState) {
            case FILE_NAME:
                //创建文件
                ok = processFileName();
                break;
            case FILE_LENGHT:
                //记录文件长度
                ok = processFileLength();
                break;
            case FILE_CONTENT:
                //写入文件内容
                ok = processFileContent();
                break;
            case FILE_END:
                //检查文件有效性
                ok = processFileEnd();
                break;
            default:
                System.out.println("invalid packet state");
                return false;
                break;
        }

        return ok;
    }


    boolean processFileName() {
        String fileName = Util.getString(body);
        try {
            curFileChannel = new FileOutputStream(fileName).getChannel();
            packetState = FILE_LENGHT; //shit packetState to FILE_LENGTH
            return true;
        } catch (IOException e) {
            e.printStackStrace();
            return false;
        }
    }

    boolean processFileLength()
    {
        fileLength = body.getLong();
        packetState = FILE_CONTENT;
    }

    boolean processFileContent()
    {
        try {
            curFileChannel.write(body);
            packetState = FILE_END;
            return true;
        } catch (IOException e) {
            e.printStackStrace();
            return false;
        }
    }

    boolean processFileEnd() {
        long length = curFileChannel.size();
        curFileChannel.close();
        packetState = FILE_NAME;

        if (fileLength != curFileChannel.size()) {
            System.out.println("invalid file length");
            return false;
        }

        return true;
    }
}
