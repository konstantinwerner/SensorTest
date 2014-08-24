package in.konstant.sensortest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import in.konstant.R;
import in.konstant.Sensors.SensorDevice;

public class SensorArrayAdapter extends BaseAdapter {
    private final static String TAG = "SensorArrayAdapter";
    private final static boolean DBG = true;

    private final Activity context;

    private ArrayList<String> ids;
    private HashMap<String, SensorDevice> devices;

    static class ViewHolder {
        public TextView name;
        public TextView address;
        public View connected;
        public TextView nrOfSensors;
    }

    public SensorArrayAdapter(Activity context) {
        this.context = context;

        devices = new HashMap<String, SensorDevice>();
        ids = new ArrayList<String>();
    }

    @Override
    public SensorDevice getItem(int id) {
        return devices.get(ids.get(id));
    }

    public SensorDevice getItem(String address) {
        return devices.get(address);
    }

    @Override
    public long getItemId(int id) {
        return 0;
    }

    @Override
    public int getCount() {
        return ids.size();
    }

    public Set<String> getKeySet() {
        return devices.keySet();
    }

    public void clear() {
        devices.clear();
        ids.clear();
        notifyDataSetInvalidated();
    }

    public void add(String address, SensorDevice device) {
        devices.put(address, device);
        ids.add(address);
        notifyDataSetChanged();
    }

    public void remove(String address) {
        if (DBG) Log.d(TAG, "remove(" + address + ")");
        this.remove(ids.indexOf(address));
    }

    public void remove(int id) {
        if (DBG) Log.d(TAG, "remove(" + id + ")");
        devices.remove(ids.get(id));
        ids.remove(id);
        notifyDataSetChanged();
    }

    public boolean contains(String address) {
        return devices.containsKey(address);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.arrayadapter_sensorarray, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.tvName);
            viewHolder.address = (TextView) rowView.findViewById(R.id.tvAddress);
            viewHolder.connected = (View) rowView.findViewById(R.id.inConnected);
            viewHolder.nrOfSensors = (TextView) rowView.findViewById(R.id.tvNrOfSensors);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        SensorDevice item = getItem(position);

        holder.name.setText(item.getDeviceName());
        holder.address.setText(item.getBluetoothAddress());

        if (item.getConnected()) {

            holder.nrOfSensors.setText(parent.getContext().getResources().getString(
                            R.string.device_list_nrOfSensors,
                            item.getNumberOfSensors())
            );
        } else {
            holder.nrOfSensors.setText("");
        }

        int ColorId;

        switch (item.getConnectionState()) {
            case SensorDevice.STATE.CONNECTED: ColorId = R.color.connected; break;
            case SensorDevice.STATE.CONNECTING: ColorId = R.color.connecting; break;
            default:
            case SensorDevice.STATE.DISCONNECTED: ColorId = R.color.disconnected; break;
        }

        holder.connected.setBackgroundColor(parent.getContext().getResources().getColor(ColorId));

        return rowView;
    }
}
