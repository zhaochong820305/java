import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.io.FileOutputStream;
import java.io.IOException;

import binary.demo.Protocol;
import binary.demo.Util;

public class User implements Protocol {
    private SocketChannel socketChannel;
    private ByteBuffer header = ByteBuffer.allocate(HEADER_LEN);
    private ByteBuffer body = ByteBuffer.allocate(4096);
    private PacketType packetState = PacketType.FILE_NAME;
    private FileChannel curFileChannel;
    private long fileLength;

    private int readState = 0; // 0: header, 1: body

    public User(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void execute(SelectionKey key) {

        System.out.println("enter execute"); 

        try {
            if (!key.isReadable()) {
                System.out.println("invalid event type");
                socketChannel.close();
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
                SelectionKey userKey = socketChannel.register(key.selector(), SelectionKey.OP_READ);
                userKey.attach(this);
            } else {
                System.out.println("ready to close channel");
                socketChannel.close();
            }

        } catch (IOException e) {
            System.out.println("user execute exception");
            e.printStackTrace();
        }
    }

    boolean readHeader() {

        System.out.println("enter readHeader");

        //read packet header
        try {
            int bytes = socketChannel.read(header);
            if (bytes == -1) {
                System.out.println("client is closed when read packet header");
                return false;
            }

            System.out.println("read header bytes: " + bytes);

            if (header.position() != header.capacity()) {
                System.out.println("invalid header buffer, position: " + 
                        header.position() + 
                        ", capacity: " + 
                        header.capacity());
                //continue read header
                return true;
            }

            header.flip();

            //read a complete header, process header
            int packetLen = header.getInt();
            if (packetLen > MAX_LEN) {
                System.out.println("invalid packet len: " + packetLen
                        + ", expects a value of length less: " + MAX_LEN);
                return false;
            } else if (packetLen <= 0) {
                System.out.println("packet len can not be zero");
                System.out.println("header buffer position: " + 
                        header.position() + 
                        ", capacity: " + 
                        header.capacity());
                System.out.println("header hex output: ");
                Util.printHexString(header.array()); 
                return false;
            }


            short packetType = header.getShort();
            if (packetType != packetState.getValue()) {
                System.out.println("invalid packet type: " + packetType
                        + ", expects type: " + packetState.getValue());

                Util.printHexString(header.array()); 
                return false;
            }

            System.out.println("packet len: " + packetLen
                    + ", packet type: " + packetType
                    + ", load: " + (packetLen - HEADER_LEN));

            int bodyLen = packetLen - HEADER_LEN;
            System.out.println("set body limit to: " + bodyLen);
            body.limit(bodyLen);
            readState = 1; //transform readState to body

            return true;
        } catch (IOException e) {
            System.out.println("read header exception.");
            e.printStackTrace();
            return false;
        }
    }

    boolean readBody() {
        System.out.println("enter readBody");
        try {
            //read packet body
            int bytes = socketChannel.read(body);
            if (bytes == -1) {
                System.out.println("client is closed when read packet body");
                return false;
            }

            System.out.println("read body bytes: " + bytes);

            if (body.position() != body.limit()) {
                //continue to read body
                System.out.println("invalid body buffer, position: " + 
                        body.position() + 
                        ", capacity: " + 
                        body.limit());
                return true;
            }

            body.flip();

            boolean ok = processBody();

            //switch to read header
            readState = 0;
            header.clear();
            body.clear();

            return ok;
        } catch (IOException e) {
            System.out.println("read body exception.");
            e.printStackTrace();
            return false;
        }
    }

    boolean processBody() {

        System.out.println("enter processBody");

        boolean ok = true;
        //read a completed body, process body
        switch (packetState) {
            case FILE_NAME:
                //创建文件
                ok = processFileName();
                break;
            case FILE_LENGTH:
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
        }

        return ok;
    }


    boolean processFileName() {
        String fileName = Util.getString(body);
        String filePath = System.getProperty("user.dir") + "/upload/" + fileName;
        System.out.println("enter processFileName, file path: " + filePath);
        try {
            curFileChannel = new FileOutputStream(filePath).getChannel();
            packetState = PacketType.FILE_LENGTH; //shit packetState to FILE_LENGTH
            return true;
        } catch (IOException e) {
            System.out.println("process file name exception");
            e.printStackTrace();
            return false;
        }
    }

    boolean processFileLength()
    {
        fileLength = body.getLong();
        System.out.println("enter processFileLength, file length: " + fileLength);
        packetState = PacketType.FILE_CONTENT;
        return true;
    }

    boolean processFileContent()
    {
        System.out.println("enter processFilecontent");
        try {
            curFileChannel.write(body);
            packetState = PacketType.FILE_END;
            return true;
        } catch (IOException e) {
            System.out.println("process file content exception");
            e.printStackTrace();
            return false;
        }
    }

    boolean processFileEnd() {

        System.out.println("enter processFileEnd");

        try {
            long length = curFileChannel.size();
            curFileChannel.close();
            packetState = PacketType.FILE_NAME;

            if (fileLength != length) {
                System.out.println("invalid file length");
                return false;
            }
        } catch (IOException e) {
            System.out.println("process file end exception");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
