package GardenManagement.DeviceManagement;

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
import Login_RegisterUser.UserLoginManagement;

import Helper.Constants;

public class DeviceDetailAdapter extends BaseAdapter{
    private LayoutInflater mInfoInflater;
    private Vector<DeviceInformation> devices;

    public DeviceDetailAdapter(Context context, String listType){
        if(listType.equals(Constants.DEVICE_TYPE[0])){
            this.devices = UserLoginManagement.getInstance(context).getSensor();
        }
        else{
            this.devices = UserLoginManagement.getInstance(context).getOutput();
        }

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
        ImageView device_icon = v.findViewById(R.id.DeviceAdapter_DeviceIcon_IV);
        ImageView next_icon = v.findViewById(R.id.DeviceAdapter_NextIcon);
        ImageView statusIcon = v.findViewById(R.id.DeviceAdapter_DeviceStatusIcon_IV);
        ImageView statusIcon1 = v.findViewById(R.id.DeviceAdapter_DeviceStatusIcon1_IV);
        TextView statusTV = v.findViewById(R.id.DeviceAdapter_DeviceStatus_TV);
        TextView statusTV1 = v.findViewById(R.id.DeviceAdapter_DeviceStatus1_TV);

        // Get name from array
        String id = devices.elementAt(position).getDevice_id();
        String type = devices.elementAt(position).getDevice_type();

        // Set texts
        device_TopicTV.setText("Device ID: " + id);
        Device_type.setText(type);

        next_icon.setImageResource(R.drawable.ic_baseline_navigate_next_24);
        switch(type){
            case Constants.LIGHT_SENSOR_TYPE:
                device_icon.setImageResource(R.drawable.ic_light_sensor_icon);
                statusIcon.setImageResource(R.drawable.ic_light_30);
                statusTV1.setVisibility(View.GONE);
                statusIcon1.setVisibility(View.GONE);
                if(devices.elementAt(position).getStatus().equals("No record")) {
                    statusTV.setText("No record");
                    return v;
                }
                statusTV.setText(devices.elementAt(position).getStatus() + " lux");
                return v;
            case Constants.TEMPHUMI_SENSOR_TYPE:
                device_icon.setImageResource(R.drawable.ic_temphumi_sensor_icon);
                statusIcon.setImageResource(R.drawable.ic_temphumi_sensor_icon_black);
                statusIcon1.setImageResource(R.drawable.ic_humidity_30);
                if(devices.elementAt(position).getStatus().equals("No record")){
                    statusTV.setText("No record");
                    statusTV1.setText("No record");
                    return v;
                }
                String[] measurements = devices.elementAt(position).getStatus().split(":");
                statusTV.setText(measurements[0] + "\u2103");
                statusTV1.setText(measurements[1] + "%");
                return v;
            case Constants.OUTPUT_TYPE:
                device_icon.setImageResource(R.drawable.ic_display_light_icon);
                if(devices.elementAt(position).getStatus().equals("Off")) {
                    statusIcon.setImageResource(R.drawable.ic_power_off_24);
                    statusTV.setText(devices.elementAt(position).getStatus());
                }
                else{
                    String[] status = devices.elementAt(position).getStatus().split("-");
                    statusIcon.setImageResource(R.drawable.ic_light_30);
                    statusTV.setText(status[0] + " - " + Integer.parseInt(status[1])*100/255 +"%" );
                }

                statusTV1.setVisibility(View.GONE);
                statusIcon1.setVisibility(View.GONE);
                return v;
        }
        return v;
    }

    public void changeData(Context context, String listType){
        if(listType.equals(Constants.DEVICE_TYPE[0])){
            this.devices = UserLoginManagement.getInstance(context).getSensor();
        }
        else{
            this.devices = UserLoginManagement.getInstance(context).getOutput();
        }
    }
}
