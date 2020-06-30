package Userprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;

public class PlantDetailActivity extends AppCompatActivity implements VolleyCallBack {
    private TextView readingTV;
    private TextView readingTimeTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        IOT_Server_Access.connect(this);
        // Components

        TextView plant_nameTV = findViewById(R.id.Detail_Plantname_TV);
        TextView buy_dateTV = findViewById(R.id.Detail_BuyDate_TV);
        TextView buy_locationTV = findViewById(R.id.Detail_BuyLocation_TV);
        TextView amountTV = findViewById(R.id.Detail_Amount_TV);
        readingTV = findViewById(R.id.Detail_LastReading_TV);
        readingTimeTV= findViewById(R.id.Detail_LastReadingTime_TV);

        // Set text
        plant_nameTV.setText(getIntent().getStringExtra("plant_detail.plant_name"));
        buy_dateTV.setText(getIntent().getStringExtra("plant_detail.buy_date"));
        buy_locationTV.setText(getIntent().getStringExtra("plant_detail.buy_location"));
        amountTV.setText(getIntent().getStringExtra("plant_detail.amount"));

        // Get reading
        Garden_Database_Control.getDeviceLastReading(getIntent().getStringExtra("plant_detail.linked_sensor_id"), this, this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessResponse(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            if(!jsonObject.getBoolean("error")){
                if(jsonObject.has("reading")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("reading");
                    JSONObject reading  = jsonArray.getJSONObject(0);
                    //Log.i("JSON object", String.valueOf(reading));
                    String type = reading.getString("type");
                    String readingText = "";
                    readingText = reading.getString("type") + ": " + reading.getInt("measurement");
                    if(type.equals("Humid")){
                        JSONObject tempReading  = jsonArray.getJSONObject(1);
                        readingText += " " + tempReading.getString("type") + ": " + tempReading.getInt("measurement");
                    }
                    readingTV.setText(readingText);
                    readingTimeTV.setText(reading.getString("date"));
                }
                else{
                    readingTV.setText("NULL");
                    readingTimeTV.setText("NULL");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
