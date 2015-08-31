import java.io.File;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import binary.demo.Protocol;


public class Client implements Protocol {

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.upload();
    }

    public void upload()
    {
        try {
            Socket cli = new Socket("127.0.0.1", 9999);
            File file = new File("a.txt");
            FileInputStream fis = new FileInputStream(file);
            DataOutputStream dos = new DataOutputStream(cli.getOutputStream());

            //send file name
            int packetLength = HEADER_LEN + file.getName().length();
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

            //send file content
            byte[] buf = new byte[4096];
            while (true) {
                int len = fis.read(buf, 0, 4096);
                if (len <= 0) {
                    break;
                }

                packetLength = HEADER_LEN + len;
                dos.writeInt(packetLength);
                dos.writeShort(PacketType.FILE_CONTENT.getValue());
                dos.write(buf);
                dos.flush();
            }

            //send file end flag
            packetLength = HEADER_LEN;
            dos.writeInt(packetLength);
            dos.writeShort(PacketType.FILE_END.getValue());
            dos.flush();

            cli.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
