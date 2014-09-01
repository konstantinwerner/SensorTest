package in.konstant.Sensors;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ASCII85{
    public static byte[] decode(byte[] data) {
        long m = 1;
        long tmp = 0;

        for (int i = 0; i < 5; ++i) {
            tmp += ((data[4-i] % 85) - 33) * m;
            m *= 85;
        }

        int result = (int) (tmp & 0xFFFFFFFF);

        return ByteBuffer.allocate(4).putInt(result).array();
    }

    public static byte[] encode(byte[] data) {
        byte[] result = new byte[5];

        long word = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();

        word &= 0xFFFFFFFF;

        for (int i = 5; i > 0; --i)
        {
            result[i] = (byte) ((word % 85) + 33);
            word /= 85;
        }

        return result;
    }

    public static float decodeToFloat(String data) {
        return ByteBuffer.wrap(decode(data.getBytes())).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public static int decodeToInt(String data) {
        return ByteBuffer.wrap(decode(data.getBytes())).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static String encode(float number) {
        return new String(encode(ByteBuffer.allocate(4).putFloat(number).array()));
    }

    public static String encode(int number) {
        return new String(encode(ByteBuffer.allocate(4).putInt(number).array()));
    }
}
