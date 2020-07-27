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

class DeviceTypeItemAdapter extends BaseAdapter {
    private LayoutInflater mInfoInflater;
    String[] device_type;
    int[] device_count;

    public DeviceTypeItemAdapter(Context context, String[] device_type, int[] device_count){
        this.device_type = device_type;
        this.device_count = device_count;
        mInfoInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return device_type.length;
    }

    @Override
    public Object getItem(int position) {
        return device_type[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    private int getPicture(String type){
//        switch (type){
//            case "sensor": return R.drawable.sensor_icon_50;
//            case "output": return R.drawable.light_iot_icon_50;
//            default: return -1;
//        }
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View v = mInfoInflater.inflate(R.layout.device_type_detail_adapter, null);
        // Get view component
        TextView typeTextView = v.findViewById(R.id.devicetype_devicetype);
        TextView deviceCountTextView = v.findViewById(R.id.devicetype_devicecount);
        ImageView icon = v.findViewById(R.id.devicetype_icon);
        // Get name from array
        String type = device_type[position];
        String deviceCount = device_count[position] + " devices";
        // Set texts
        typeTextView.setText(type.toUpperCase());
        deviceCountTextView.setText(deviceCount);
        if(type.equals("sensor")){
            icon.setImageResource(R.drawable.sensor_icon_50dp);
        }
        else if(type.equals("output")){
            icon.setImageResource(R.drawable.light_iot_icon_50);
        }
        return v;
    }
}
