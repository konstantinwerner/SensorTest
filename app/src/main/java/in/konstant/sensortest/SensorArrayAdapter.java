package in.konstant.sensortest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import in.konstant.R;
import in.konstant.Sensors.SensorDevice;

public class SensorArrayAdapter extends BaseAdapter {
    private final Activity context;

    private ArrayList<String> ids;
    private HashMap<String, SensorDevice> devices;

    static class ViewHolder {
        public TextView name;
        public TextView address;
        public View connected;
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
        ids.remove(devices.get(address));   // TODO: Removing does not work
        devices.remove(address);
        notifyDataSetChanged();
    }

    public void remove(int id) {
        devices.remove(ids.get(id));
        ids.remove(id);
        notifyDataSetChanged();
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

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        SensorDevice item = getItem(position);

        if (item != null) { // TODO: Necessary?

            holder.name.setText(item.getDeviceName());
            holder.address.setText(item.getBluetoothAddress());

            if (item.getConnected()) {
                holder.connected.setBackgroundColor(parent.getContext().getResources().getColor(R.color.connected));
            } else {
                holder.connected.setBackgroundColor(parent.getContext().getResources().getColor(R.color.disconnected));
            }
        }

        return rowView;
    }
}
