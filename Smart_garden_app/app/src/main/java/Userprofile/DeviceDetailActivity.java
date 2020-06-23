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

import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Objects;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;

public class DeviceDetailActivity extends AppCompatActivity implements VolleyCallBack {

    private TextView device_lastReadingTV;
    private TextView device_lastReadingTimeTV;
    private TextView device_readingTypeTV;
    private TextView device_lastReading1TV;
    private TextView device_readingType1TV;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        // Component
        TextView device_idTV = findViewById(R.id.DeviceDetail_DeviceID_TV);
        TextView device_nameTV = findViewById(R.id.deviceDetail_DeviceName_TV);
        TextView device_typeTV = findViewById(R.id.deviceDetail_DeviceType_TV);
        device_lastReadingTV = findViewById(R.id.deviceDetail_DeviceLastReading_TV);
        device_readingTypeTV = findViewById(R.id.deviceDetail_readingType_TV);
        device_lastReadingTimeTV = findViewById(R.id.deviceDetail_DeviceLastReadingTime_TV);
        device_lastReading1TV = findViewById(R.id.deviceDetail_DeviceLastReading1_TV);
        device_readingType1TV = findViewById(R.id.deviceDetail_readingType1_TV);

        ConstraintLayout reading1 = findViewById(R.id.reading1);
        Button returnBtn = findViewById(R.id.item_returnButton);

        // Set text
        device_idTV.setText(getIntent().getStringExtra("device_detail.device_id"));
        device_nameTV.setText(getIntent().getStringExtra("device_detail.device_name"));
        device_typeTV.setText(getIntent().getStringExtra("device_detail.device_type"));
        if(Objects.requireNonNull(getIntent().getStringExtra("device_detail.device_type")).contains("Light")){
            reading1.setVisibility(View.GONE);
        }

        // Set return button
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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
                    JSONObject reading  = jsonArray.getJSONObject(0);
                    //Log.i("JSON object", String.valueOf(reading));
                    String type = reading.getString("type");
                    //String readingText = "";
                    device_readingTypeTV.setText(reading.getString("type"));
                    device_lastReadingTV.setText(reading.getInt("measurement")+"");
                    if(type.equals("Humid")){
                        JSONObject tempReading  = jsonArray.getJSONObject(1);
                        device_readingType1TV.setText(tempReading.getString("type"));
                        device_lastReading1TV.setText(tempReading.getInt("measurement")+"");
                    }
                    device_lastReadingTimeTV.setText(reading.getString("date"));
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
