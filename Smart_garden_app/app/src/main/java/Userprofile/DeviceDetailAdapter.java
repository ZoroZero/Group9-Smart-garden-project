package Userprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartgarden.R;

import java.util.Vector;

import Helper.DeviceInformation;

public class DeviceDetailAdapter extends BaseAdapter {
    private LayoutInflater mInfoInflater;
    private Vector<DeviceInformation> devices;

    public DeviceDetailAdapter(Context context, Vector<DeviceInformation> devices){
        this.devices = devices;
        mInfoInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.elementAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View v = mInfoInflater.inflate(R.layout.device_detail_view, null);
        // Get view component
        TextView nameText = v.findViewById(R.id.detail_device_id);
        TextView desText = v.findViewById(R.id.device_detail_name);
        // Get name from array
        String id = devices.elementAt(position).getDevice_id();;
        String name = devices.elementAt(position).getDevice_name();
        // Set texts
        nameText.setText("Device topic: " + id);
        desText.setText("Device type: " + name);
        return v;
    }
}
