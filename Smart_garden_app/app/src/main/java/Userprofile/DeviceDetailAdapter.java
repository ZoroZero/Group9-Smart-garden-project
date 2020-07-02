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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Helper.VolleyCallBack;

public class DeviceDetailAdapter extends BaseAdapter {
    private LayoutInflater mInfoInflater;
    private Vector<DeviceInformation> devices;
    private TextView status;
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
        TextView device_Id = v.findViewById(R.id.DeviceAdapter_DeviceId_TV);
        TextView Device_type = v.findViewById(R.id.DeviceAdapter_DeviceType_TV);
        ImageView device_icon = v.findViewById(R.id.DeviceAdapter_DeviceIcon_IV);
        status = v.findViewById(R.id.DeviceAdapter_DeviceStatus_TV);
        // Get name from array
        String id = devices.elementAt(position).getDevice_id();;
        String type = devices.elementAt(position).getDevice_type();
        switch(type){
            case "Output":status.setText("Status");
            case "Light Sensor": status.setVisibility(View.GONE);
            case "TempHumi Sensor": status.setVisibility(View.GONE);
        }
        // Set texts
        device_Id.setText("Device id: " + id);
        Device_type.setText(type.toUpperCase());
        device_icon.setImageResource(R.drawable.ic_view_device_list_black_24dp);
        return v;
    }
}
