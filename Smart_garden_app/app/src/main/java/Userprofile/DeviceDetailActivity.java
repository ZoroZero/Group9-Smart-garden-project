package Userprofile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartgarden.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import Database.Garden_Database_Control;
import DeviceController.DeviceSettingActivity;
import Helper.VolleyCallBack;

public class DeviceDetailActivity extends AppCompatActivity implements VolleyCallBack {

    private TextView device_lastReadingTV;
    private TextView device_lastReadingTimeTV;
    private TextView device_lastReading1TV;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar1;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        // Component
        TextView device_TopicTV = findViewById(R.id.DeviceDetail_DeviceTopic_TV);
        TextView device_ThresholdTV = findViewById(R.id.deviceDetail_DeviceThreshold_TV);
        TextView device_typeTV = findViewById(R.id.deviceDetail_DeviceType_TV);
        device_lastReadingTV = findViewById(R.id.deviceDetail_DeviceLastReading_TV);
        TextView device_readingTypeTV = findViewById(R.id.deviceDetail_readingType_TV);
        device_lastReadingTimeTV = findViewById(R.id.deviceDetail_DeviceLastReadingTime_TV);
        device_lastReading1TV = findViewById(R.id.deviceDetail_DeviceLastReading1_TV);
        TextView device_readingType1TV = findViewById(R.id.deviceDetail_readingType1_TV);
        readingBar = findViewById(R.id.deviceDetail_DeviceLastReading);
        readingBar1 = findViewById(R.id.deviceDetail_DeviceLastReading1);
        ConstraintLayout reading1 = findViewById(R.id.reading);
        Button returnBtn = findViewById(R.id.item_returnButton);
        Button changeSettingBtn = findViewById(R.id.device_changeSettingBtn);

        // Set text
        device_TopicTV.setText(getIntent().getStringExtra("device_detail.device_name") +
                "/" + getIntent().getStringExtra("device_detail.device_id"));
        device_ThresholdTV.setText(getIntent().getStringExtra("device_detail.device_threshold"));
        device_typeTV.setText(getIntent().getStringExtra("device_detail.device_type"));
        if(Objects.requireNonNull(getIntent().getStringExtra("device_detail.device_type")).equals(Constants.LIGHT_SENSOR_TYPE)){
            reading1.setVisibility(View.GONE);
            device_readingType1TV.setText("Light intensity");
            readingBar1.setEndValue(Constants.MAX_LIGHT);
        }
        else{
            device_readingTypeTV.setText("Humidity");
            device_readingType1TV.setText("Temperature");
            readingBar.setEndValue(Constants.MAX_HUMIDITY);
            readingBar1.setEndValue(Constants.MAX_TEMPERATURE);
        }

        // Set return button
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DeviceListOverViewActivity.class));
            }
        });

        // Set change setting button
        changeSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeSetting = new Intent(getApplicationContext(), DeviceSettingActivity.class);
                changeSetting.putExtra("device_setting.device_id", getIntent().getStringExtra("device_detail.device_id"));
                changeSetting.putExtra("device_setting.device_name", getIntent().getStringExtra("device_detail.device_name"));
                changeSetting.putExtra("device_setting.device_type", getIntent().getStringExtra("device_detail.device_type"));
                startActivity(changeSetting);
            }
        });


        // Get reading
        Garden_Database_Control.getDeviceLastReading(getIntent().getStringExtra("device_detail.device_id"), this, this);
        final Handler handler=new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                // Get reading
                Garden_Database_Control.getDeviceLastReading(getIntent().getStringExtra("device_detail.device_id"), getApplicationContext(),  DeviceDetailActivity.this);
                handler.postDelayed(this,500); // set time here to refresh textView
            }
        });
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
                    device_lastReadingTimeTV.setText(reading.getString("date"));
                    if(type.equals(Constants.TEMPHUMI_SENSOR_TYPE)) {
                        String[] measurements = reading.getString("measurement").split(":");
                        device_lastReadingTV.setText(measurements[1] + "%");
                        readingBar.setValue(Integer.parseInt(measurements[1]));

                        device_lastReading1TV.setText(measurements[0] + "\u2103");
                        readingBar1.setValue(Integer.parseInt(measurements[0]));
                    }
                    else{
                        String measurement = reading.getString("measurement");
                        device_lastReading1TV.setText(measurement + " lux");
                        readingBar1.setValue(Integer.parseInt(measurement));
                    }
                }
                else{
                    device_lastReadingTV.setText("No record");
                    device_lastReading1TV.setText("No record");
                    device_lastReadingTimeTV.setText("No record");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
