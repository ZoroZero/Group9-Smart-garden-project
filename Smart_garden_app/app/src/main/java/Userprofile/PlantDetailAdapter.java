package Userprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartgarden.R;

public class PlantDetailAdapter  extends BaseAdapter {
    private LayoutInflater mInfoInflater;
    private String[] plant_names;
    private String[] plant_buy_dates;
    private String[] plant_amounts;

    public PlantDetailAdapter(Context context, String[] plant_names, String[] plant_buy_dates, String[] plant_amounts){
        this.plant_names = plant_names;
        this.plant_buy_dates = plant_buy_dates;
        this.plant_amounts = plant_amounts;
        mInfoInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return plant_names.length;
    }

    @Override
    public Object getItem(int position) {
        return plant_names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View v = mInfoInflater.inflate(R.layout.plant_detail_view, null);
        // Get view component
        TextView nameTextView = v.findViewById(R.id.PlantDetail_PlantName_TV);
        TextView buy_dateTextView = v.findViewById(R.id.PlantDetail_BuyDate_TV);
        TextView amountTextView = v.findViewById(R.id.PlantDetail_Amount_TV);
        // Get name from array
        String name = plant_names[position];
        String date = plant_buy_dates[position];
        String amount = plant_amounts[position];
        // Set texts
        nameTextView.setText("Plant name: " + name);
        buy_dateTextView.setText("Buy date: " + date);
        amountTextView.setText("Amount: " + amount);
        return v;
    }
}
