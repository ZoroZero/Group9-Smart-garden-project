package Userprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartgarden.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Helper.Helper;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.UserLoginManagement;

public class PlantDetailActivity extends AppCompatActivity implements VolleyCallBack {
    private TextView readingTimeTV;
    private TextView device_lastReadingTV;
    private TextView device_lastReading1TV;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar1;

    @SuppressLint("SetTextI18n")
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
        readingTimeTV= findViewById(R.id.Detail_LastReadingTime_TV);
        device_lastReadingTV = findViewById(R.id.PlantDetail_DeviceLastReading_TV);
        TextView device_readingTypeTV = findViewById(R.id.PlantDetail_readingType_TV);
        device_lastReading1TV = findViewById(R.id.PlantDetail_DeviceLastReading1_TV);
        TextView device_readingType1TV = findViewById(R.id.PlantDetail_readingType1_TV);

        ImageView readingTypeIcon = findViewById(R.id.PlantDetail_readingTypeIcon_TV);
        ImageView readingTypeIcon1 = findViewById(R.id.PlantDetail_readingTypeIcon1_TV);

        readingBar = findViewById(R.id.PlantDetail_DeviceLastReading);
        readingBar1 = findViewById(R.id.PlantDetail_DeviceLastReading1);
        ConstraintLayout readingLayout = findViewById(R.id.PlantDetail_reading);
        // Set text
        plant_nameTV.setText(getIntent().getStringExtra("plant_detail.plant_name"));
        buy_dateTV.setText(getIntent().getStringExtra("plant_detail.buy_date"));
        buy_locationTV.setText(getIntent().getStringExtra("plant_detail.buy_location"));
        amountTV.setText(getIntent().getStringExtra("plant_detail.amount"));

        DeviceInformation sensorInfo = Helper.findDeviceWithDeviceId(getIntent().getStringExtra("plant_detail.linked_sensor_id"),
                UserLoginManagement.getInstance(this).getSensor());

        assert sensorInfo != null;
        if(sensorInfo.getDevice_type().equals(Constants.LIGHT_SENSOR_TYPE)){
            readingLayout.setVisibility(View.GONE);
            device_readingType1TV.setText("Light intensity");
            readingTypeIcon1.setImageResource(R.drawable.ic_light_30);
        }
        else if(sensorInfo.getDevice_type().equals(Constants.TEMPHUMI_SENSOR_TYPE)){
            device_readingTypeTV.setText("Humidity");
            device_readingType1TV.setText("Temperature");
            readingTypeIcon.setImageResource(R.drawable.ic_humidity_30);
            readingTypeIcon1.setImageResource(R.drawable.ic_temphumi_sensor_icon_black);
        }
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
                    final JSONObject reading  = jsonArray.getJSONObject(0);
                    //Log.i("JSON object", String.valueOf(reading));
                    String type = reading.getString("type");
                    readingTimeTV.setText(reading.getString("date"));
                    if(type.equals("Humid")){
                        device_lastReadingTV.setText(reading.getInt("measurement")+"%");
                        readingBar.setValue(reading.getInt("measurement"));
                        // Get temperature reading
                        JSONObject tempReading  = jsonArray.getJSONObject(1);
                        device_lastReading1TV.setText(tempReading.getInt("measurement")+"\u2103");
                        readingBar1.setValue(tempReading.getInt("measurement"));
                    }
                    else{
                        device_lastReading1TV.setText(reading.getInt("measurement")+"%");
                        readingBar1.setValue(reading.getInt("measurement"));
                    }
                }
                else{
                    device_lastReadingTV.setText("No record");
                    device_lastReading1TV.setText("No record");
                    readingTimeTV.setText("No record");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
