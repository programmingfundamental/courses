package labnet;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

public record Frame(int type, long id, byte[] body) {
    public static final int PING = 1, ECHO = 2, QUIT = 3, LOGIN = 0x10, BROADCAST = 0x12,
            SEND = 0x11, USERS = 0x13, STATS = 0x14, AUTH = 0x15, WORK = 0x20,
            OK = 0x80, DATA = 0x81, EVENT = 0x82, ERROR = 0xff;
    public static final int HEADER = 6, MAX_PAYLOAD = 65536;
    public static final byte[] EMPTY = {};

    public static byte[] bytes(String text) { return text.getBytes(StandardCharsets.UTF_8); }

    public static String text(byte[] data) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(data)).toString();
    }

    public ByteBuffer encode() {
        int length = Long.BYTES + body.length;
        if (length > MAX_PAYLOAD) throw new IllegalArgumentException("Frame too large");
        return ByteBuffer.allocate(HEADER + length).put((byte) 1).put((byte) type)
                .putInt(length).putLong(id).put(body).flip();
    }

    public static int length(int version, int length) throws IOException {
        if (version != 1 || length < Long.BYTES || length > MAX_PAYLOAD)
            throw new IOException("Invalid LabNet header");
        return length;
    }

    public static Frame read(DataInputStream in) throws IOException {
        return read(in.readUnsignedByte(), in);
    }
    public static Frame read(int version, DataInputStream in) throws IOException {
        int type = in.readUnsignedByte();
        int size = length(version, in.readInt());
        long id = in.readLong();
        byte[] body = new byte[size - Long.BYTES];
        in.readFully(body);
        return new Frame(type, id, body);
    }

    public static boolean validName(String name) { return name.matches("[A-Za-z0-9_-]{1,24}"); }

    // Both EVENT and AUTH use one unsigned name-length byte followed by name and value.
    public record Named(String name, byte[] value) {
        public byte[] pack() {
            if (!validName(name)) throw new IllegalArgumentException("Invalid name");
            byte[] prefix = bytes(name);
            return ByteBuffer.allocate(1 + prefix.length + value.length)
                    .put((byte) prefix.length).put(prefix).put(value).array();
        }

        public static Named unpack(byte[] body) throws IOException {
            if (body.length == 0) throw new IOException("Missing name");
            int size = Byte.toUnsignedInt(body[0]);
            if (size < 1 || size > 24 || size + 1 > body.length) throw new IOException("Invalid name length");
            String name = new String(body, 1, size, StandardCharsets.US_ASCII);
            if (!validName(name)) throw new IOException("Invalid name");
            return new Named(name, java.util.Arrays.copyOfRange(body, size + 1, body.length));
        }
    }
}
