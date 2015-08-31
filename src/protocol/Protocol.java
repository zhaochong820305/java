package binary.demo;

import java.nio.channels.SelectionKey;

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
 *              2 -- file content
 *              3 -- file end
 *
 * */

public interface Protocol {

    static int HEADER_LEN = 6;
    static int MAX_LEN = 4096 + 6;

    enum PacketType {
        FILE_NAME((short)0),
        FILE_LENGTH((short)1),
        FILE_CONTENT((short)2),
        FILE_END((short)3);

        private short value;

        private PacketType(short value) {
            this.value = value;
        }

        public short getValue() {
            return this.value;
        }
    }
}

