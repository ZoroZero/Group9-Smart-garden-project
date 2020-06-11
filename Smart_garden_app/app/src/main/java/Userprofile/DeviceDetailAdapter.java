package Userprofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartgarden.R;

public class DeviceDetailAdapter extends BaseAdapter {
    private LayoutInflater mInfoInflater;
    public String[] device_ids;
    public String[] device_names;

    public DeviceDetailAdapter(Context context, String[] device_ids, String[] device_names){
        this.device_ids = device_ids;
        this.device_names = device_names;
        mInfoInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return device_ids.length;
    }

    @Override
    public Object getItem(int position) {
        return device_ids[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInfoInflater.inflate(R.layout.device_detail_view, null);
        // Get view component
        TextView nameText = v.findViewById(R.id.detail_device_id);
        TextView desText = v.findViewById(R.id.device_detail_name);
        // Get name from array
        String id = device_ids[position];
        String name = device_names[position];
        // Set texts
        nameText.setText("Device id: " + id);
        desText.setText("Device name: " + name);
        return v;
    }
}
