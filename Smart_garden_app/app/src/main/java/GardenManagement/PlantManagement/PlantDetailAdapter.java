package GardenManagement.PlantManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartgarden.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlantDetailAdapter  extends BaseAdapter {
    private LayoutInflater mInfoInflater;
    private String[] plant_names;
    private String[] plant_buy_dates;
    private String[] plant_amounts;
    private SimpleDateFormat dateFormat;
    private Date today;

    @SuppressLint("SimpleDateFormat")
    public PlantDetailAdapter(Context context, String[] plant_names, String[] plant_buy_dates,
                              String[] plant_amounts){
        this.plant_names = plant_names;
        this.plant_buy_dates = plant_buy_dates;
        this.plant_amounts = plant_amounts;
        mInfoInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Calendar calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = calendar.getTime();
        Log.i("Date", today.toString());
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
        Date plantedDay = null;
        try {
            plantedDay = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert plantedDay != null;
        long difference = today.getTime() - plantedDay.getTime();
        long differenceDates = Math.abs(difference) / (24 * 60 * 60 * 1000);
        String dayDifference = Long.toString(differenceDates);
        // Set texts
        nameTextView.setText(name);
        if(difference < 0){
            buy_dateTextView.setText("Days till plant: " + dayDifference);
        }
        else {
            buy_dateTextView.setText("Days planted: " + dayDifference);
        }
        amountTextView.setText("Amount: " + amount);
        return v;
    }
}
