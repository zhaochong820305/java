import java.io.File;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import binary.demo.Protocol;


public class Client implements Protocol {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: Client <file name>");
            return;
        }

        System.out.println("upload file name: " + args[0]);

        Client client = new Client();
        client.upload(args[0]);
    }

    public void upload(String fileName)
    {
        try {
            Socket cli = new Socket("127.0.0.1", 9527);
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            DataOutputStream dos = new DataOutputStream(cli.getOutputStream());

            //send file name
            int packetLength = HEADER_LEN + file.getName().length();
            System.out.println("send file name, packet length: " + packetLength
                        + ", packet type: " + PacketType.FILE_NAME.getValue()
                    + ", load: " + file.getName().length());
            dos.writeInt(packetLength);
            dos.writeShort(PacketType.FILE_NAME.getValue());
            dos.write(file.getName().getBytes());
            dos.flush();

            //send file length
            packetLength = HEADER_LEN + Long.SIZE / 8;
            dos.writeInt(packetLength);
            dos.writeShort(PacketType.FILE_LENGTH.getValue());
            dos.writeLong(file.length());
            dos.flush();
            System.out.println("send file length, packet length: " + packetLength
                        + ", packet type: " + PacketType.FILE_LENGTH.getValue()
                    + ", load: " + (Long.SIZE / 8) + ", file length: " + file.length());

            //send file content
            byte[] buf = new byte[4096];
            while (true) {
                int len = fis.read(buf, 0, 4096);
                if (len <= 0) {
                    System.out.println("no content to be readed and break");
                    break;
                }

                packetLength = HEADER_LEN + len;
                dos.writeInt(packetLength);
                dos.writeShort(PacketType.FILE_CONTENT.getValue());
                dos.write(buf, 0, len);
                dos.flush();
                System.out.println("send file content, packet length: " + packetLength
                        + ", packet type: " + PacketType.FILE_CONTENT.getValue()
                        + ", load: " + len);
            }

            //send file end flag
            packetLength = HEADER_LEN;
            dos.writeInt(packetLength);
            dos.writeShort(PacketType.FILE_END.getValue());
            dos.flush();
            System.out.println("send file end flag, packet length: " + packetLength
                    + ", packet type: " + PacketType.FILE_END.getValue() 
                    + ", load: " + 0);
            dos.close();
            cli.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
