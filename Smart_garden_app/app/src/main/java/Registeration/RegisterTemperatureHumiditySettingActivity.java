package Registeration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.Constants;
import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import DeviceController.Device_Control;
import Helper.Helper;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;

public class RegisterTemperatureHumiditySettingActivity extends AppCompatActivity implements VolleyCallBack {

    // MQTT client
    MqttAndroidClient client = null;

    // Component
    private EditText thresholdTempET;
    private EditText thresholdHumidET;
    private EditText linkedDeviceId;
    private EditText linkedDeviceName;

    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_temperature_humidity_setting);
        // Set client
        if (IOT_Server_Access.client == null) {
            IOT_Server_Access.connect(getApplicationContext());
        }
        client = IOT_Server_Access.client;

        // Component
        TextView deviceTypeTV = findViewById(R.id.deviceTypeTextView);
        thresholdTempET = findViewById(R.id.register_TempHumi_thresholdTemp_EditText);
        thresholdHumidET = findViewById(R.id.register_TempHumi_thresholdHumi_EditText);
        Button submitBtn = findViewById(R.id.submitSettingButton);
        linkedDeviceId = findViewById(R.id.registerDevice_Linked_device_id_ET);
        linkedDeviceName = findViewById(R.id.registerDevice_Linked_device_name_ET);
        progressBarHolder = findViewById(R.id.register_setting_progressBarHolder);

        Button useDefaultBtn = findViewById(R.id.register_TempHumi_useDefaultButton);
        // Set device_type
        String device_type = getIntent().getStringExtra("device_type");
        assert device_type != null;
        deviceTypeTV.setText(device_type.toUpperCase());

        // Set submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //Get params
                final String tempThreshold = thresholdTempET.getText().toString();
                final String humidThreshold = thresholdHumidET.getText().toString();
                final String device_id = getIntent().getStringExtra("device_id");
                final String device_name = getIntent().getStringExtra("device_name");
                final String linked_device_id = linkedDeviceId.getText().toString();
                final String linked_device_name = linkedDeviceName.getText().toString();
                final String threshold = tempThreshold + ":" + humidThreshold;
                //Check if empty
                if (linked_device_id.equals("") || linked_device_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Check output id format
                if(!Helper.stringContainsItemFromList(linked_device_id, Constants.OUTPUT_ID)){
                    Toast.makeText(getApplicationContext(), "Invalid output id", Toast.LENGTH_LONG).show();
                    return;
                }

                // Register device
                Garden_Database_Control.registerDevice(device_id, device_name,
                        linked_device_id, linked_device_name, threshold, getApplicationContext(),
                        RegisterTemperatureHumiditySettingActivity.this);
                Device_Control.turnDeviceOff(linked_device_id, linked_device_name, getApplicationContext());
            }
        });

        useDefaultBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                thresholdTempET.setText(Constants.DEFAULT_TEMP +"");
                thresholdHumidET.setText(Constants.DEFAULT_HUMID +"");
            }
        });
    }



    @Override
    public void onSuccessResponse(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent showResult = new Intent(getApplicationContext(), RegisterMessageActivity.class);
        assert jsonObject != null;
        try {
            showResult.putExtra("register_type", "Register new device");
            showResult.putExtra("register_message", jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(showResult);
        finish();
    }
}
