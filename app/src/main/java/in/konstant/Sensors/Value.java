package in.konstant.Sensors;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Value {
    private byte[] raw;
    private Type type;

    public Value(String ascii85, Type type) {
        this.type = type;

        this.raw = decode(ascii85);
    }

    public Value(Number value, Type type) {
        this.type = type;

        switch (this.type) {
            case TYPE_FLOAT:
                this.raw = ByteBuffer.allocate(4).putFloat(value.floatValue()).array();
                break;

            case TYPE_UINT8:
            case TYPE_INT8:
                this.raw = ByteBuffer.allocate(4).put(value.byteValue()).array();
                break;

            case TYPE_UINT16:
            case TYPE_INT16:
                this.raw = ByteBuffer.allocate(4).putShort(value.shortValue()).array();
                break;

            case TYPE_UINT32:
            case TYPE_INT32:
                this.raw = ByteBuffer.allocate(4).putInt(value.intValue()).array();

            default:
                throw new IllegalArgumentException();
        }
    }

    public Number getValue() {
        switch (this.type) {
            case TYPE_FLOAT:
                return ByteBuffer.wrap(this.raw).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            case TYPE_UINT8:
                return (this.raw[0] - 127);
            case TYPE_INT8:
                return this.raw[0];

            case TYPE_UINT16:
            case TYPE_INT16:
                return ByteBuffer.wrap(this.raw).order(ByteOrder.LITTLE_ENDIAN).getShort();

            case TYPE_UINT32:
            case TYPE_INT32:
                return ByteBuffer.wrap(this.raw).order(ByteOrder.LITTLE_ENDIAN).getInt();

            default:
                throw new IllegalArgumentException();
        }
    }

    private byte[] decode(String data) {
        int m = 1;
        int v = 0;

        byte[] bytes = data.getBytes();

        for (int i = 0; i < 5; i++) {
            v += ((bytes[4-i] % 85) - 33) * m;
            m *= 85;
        }

        return ByteBuffer.allocate(4).putInt(v).array();
    }
}
