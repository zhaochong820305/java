import java.io.File;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/*
 *  +------------+------+-------------+
 *  |    len     | type | packet body |
 *  +------------+------+-------------+
 *  |  4 byte    |   2  |   len - 6   |
 *  |  header(6 bytes)  |     body    |
 *
 * header len: 4 + 2 = 6
 *
 * packet body len: packet len - 6
 *
 * packet type: 0 -- file name
 *              1 -- file length
 *              3 -- file content
 *
 * */


public class Client {

    public final int HEADER_LEN = 6;

    public enum PacketType {
        FILE_NAME((short)0),
        FILE_LENGTH((short)1),
        FILE_CONTENT((short)2);

        private short value;

        private PacketType(short value) {
            this.value = value;
        }

        public short getValue() {
            return this.value;
        }
    }

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
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, 1024);
                if (len <= 0) {
                    break;
                }

                packetLength = HEADER_LEN + len;
                dos.writeInt(packetLength);
                dos.writeShort(PacketType.FILE_CONTENT.getValue());
                dos.write(buf);
                dos.flush();
            }

            cli.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
