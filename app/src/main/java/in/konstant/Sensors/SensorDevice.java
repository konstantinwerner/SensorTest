package in.konstant.Sensors;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import in.konstant.BT.BTDevice;

public class SensorDevice extends HandlerThread implements Handler.Callback {
    private final static String TAG = "SensorDevice";
    private final static boolean DBG = true;

    public static final class MESSAGE {
        public static final int CREATED = 1;
        public static final int CONNECTED = 2;
        public static final int DISCONNECTED = 3;
        public static final int CONNECTION_LOST = 4;
        public static final int CONNECTION_FAILED = 5;
        public static final int DESTROYED = 6;

    }

    private final static byte START_CHAR = '{';
    private final static byte STOP_CHAR = '}';

    private final Context mContext;

    private BTDevice mBTDevice;
    Handler BTHandler, mCallback;
    private String mAddress;

    private boolean mConnected = false;

    private String queue = "";

    public SensorDevice(Context context) {
        super(TAG);
        if (DBG) Log.d(TAG, "SensorDevice() created");

        mContext = context;

        start();
    }

    public void setCallback(Handler callback) {
        mCallback = callback;
    }

    @Override
    protected void onLooperPrepared() {
        BTHandler = new Handler(getLooper(), this);
        mBTDevice = new BTDevice(mContext, BTHandler);
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CREATED, mAddress));
    }

    @Override
    public boolean quit() {
        if (DBG) Log.d(TAG, "quit()");
        mBTDevice.destroy();
        mCallback.sendMessage(Message.obtain(null, MESSAGE.DESTROYED, mAddress));
        return super.quit();
    }

    public void connect(String address) {
        if (DBG) Log.d(TAG, "connect(" + address + ")");
        mAddress = address;
        mBTDevice.connect(address);
    }

    public void disconnect() {
        if (DBG) Log.d(TAG, "disconnect()");
        mBTDevice.disconnect();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (DBG) Log.d(TAG, "handleMessage(" + msg.what + ")");

        //String address = msg.getData().getString(BTDevice.EXTRA_ADDRESS);

        switch (msg.what) {
            case BTDevice.MESSAGE.CONNECTED:
                handleConnected();
                return true;

            case BTDevice.MESSAGE.CONNECTION_FAILED:
                handleConnectionFailed();
                return true;

            case BTDevice.MESSAGE.CONNECTION_LOST:
                handleConnectionLost();
                return true;

            case BTDevice.MESSAGE.DISCONNECTED:
                handleDisconnected();
                return true;

            case BTDevice.MESSAGE.DATA_RECEIVED:
                byte[] received = msg.getData().getByteArray(BTDevice.EXTRA_DATA);
                handleDataReceived(received);
                return true;

            case BTDevice.MESSAGE.DATA_SENT:
                byte[] sent = msg.getData().getByteArray(BTDevice.EXTRA_DATA);
                handleDataSent(sent);
                return true;

            default:
                return false;
        }
    }

    private void handleConnected() {
        mConnected = true;
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CONNECTED, mAddress));
    }

    private void handleDisconnected() {
        mConnected = false;
        mCallback.sendMessage(Message.obtain(null, MESSAGE.DISCONNECTED, mAddress));
    }

    private void handleConnectionFailed() {
        mConnected = false;
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CONNECTION_FAILED, mAddress));
    }

    private void handleConnectionLost() {
        mConnected = false;
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CONNECTION_LOST, mAddress));
    }

    private void handleDataReceived(byte[] data) {
        for (int b = 0; b < data.length && data[b] != 0; ++b) {
            switch (data[b]) {
                case START_CHAR:
                    queue = "";
                    break;

                case STOP_CHAR:
                    processReply(queue);
                    break;

                default:
                    queue += Byte.toString(data[b]);
                    break;
            }
            data[b] = 0;
        }
    }

    private void handleDataSent(byte[] data) {

    }

    private void processReply(String reply) {
        String[] args = reply.split("|");


    }
}
