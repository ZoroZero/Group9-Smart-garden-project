package Userprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartgarden.R;

import java.util.Vector;

import Helper.DeviceInformation;
import com.example.smartgarden.Constants;

public class DeviceDetailAdapter extends BaseAdapter{
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
        TextView device_TopicTV = v.findViewById(R.id.DeviceAdapter_DeviceTopic_TV);
        TextView Device_type = v.findViewById(R.id.DeviceAdapter_DeviceType_TV);
        TextView linked_TopicTV = v.findViewById(R.id.DeviceAdapter_LinkedTopic_TV);
        ImageView device_icon = v.findViewById(R.id.DeviceAdapter_DeviceIcon_IV);
        ImageView next_icon = v.findViewById(R.id.DeviceAdapter_NextIcon);

        // Get name from array
        String id = devices.elementAt(position).getDevice_id();
        String type = devices.elementAt(position).getDevice_type();
        String name = devices.elementAt(position).getDevice_name();


        // Set texts
        device_TopicTV.setText("Topic: " + name + "/" + id );
        Device_type.setText(type);
        linked_TopicTV.setText("Linked topic:" + devices.elementAt(position).getLinked_device_name()+ "/"+
                devices.elementAt(position).getLinked_device_id());

        next_icon.setImageResource(R.drawable.ic_baseline_navigate_next_24);
        switch(type){
            case Constants.LIGHT_SENSOR_TYPE:
                device_icon.setImageResource(R.drawable.ic_light_sensor_icon);
                return v;
            case Constants.TEMPHUMI_SENSOR_TYPE:
                device_icon.setImageResource(R.drawable.ic_temphumi_sensor_icon);
                return v;
            case Constants.OUTPUT_TYPE:
                device_icon.setImageResource(R.drawable.ic_display_light_icon);
                return v;
        }
        return v;
    }

}
