package Registeration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import Helper.Constants;
import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import DeviceController.Device_Control;
import Helper.DeviceInformation;
import Helper.Helper;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.UserLoginManagement;

public class RegisterLightSettingActivity extends AppCompatActivity implements VolleyCallBack {

    // MQTT client
    MqttAndroidClient client = null;

    // Component
    private EditText thresholdET;
    private EditText linkedDeviceId;
    private EditText linkedDeviceName;
    private String linked_device_id;
    private String linked_device_name;
    private Button submitBtn;
    private Button useDefaultBtn;
    // Loading animation
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_device_setting);
        // Set client
        if (IOT_Server_Access.client == null) {
            IOT_Server_Access.connect(getApplicationContext());
        }
        client = IOT_Server_Access.client;

        // Component
        TextView deviceTypeTV = findViewById(R.id.deviceTypeTextView);
        thresholdET = findViewById(R.id.thresholdInputEditText);
        submitBtn = findViewById(R.id.submitSettingButton);
        linkedDeviceId = findViewById(R.id.registerDevice_Linked_device_id_ET);
        linkedDeviceName = findViewById(R.id.registerDevice_Linked_device_name_ET);
        progressBarHolder = findViewById(R.id.register_setting_progressBarHolder);
        useDefaultBtn = findViewById(R.id.useDefaultButton);

        // Set device_type
        String device_type = getIntent().getStringExtra("device_type");
        assert device_type != null;
        deviceTypeTV.setText(device_type.toUpperCase());

        // Set submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                registerLightSensor();
                //Device_Control.turnDeviceOff(linked_device_id, linked_device_name, getApplicationContext());
            }
        });

        // Use default
        useDefaultBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                thresholdET.setText(Constants.DEFAULT_LIGHT+"");
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                //startActivity(deviceSetting);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void registerLightSensor(){
        final String threshold = thresholdET.getText().toString();
        final String device_id = getIntent().getStringExtra("device_id");
        final String device_name = getIntent().getStringExtra("device_name");
        linked_device_id = linkedDeviceId.getText().toString();
        linked_device_name = linkedDeviceName.getText().toString();
        if (linked_device_id.equals("") || linked_device_name.equals("") || threshold.equals("")) {
            Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
            return;
        }
        if(linked_device_id.length() >= 50 || linked_device_name.length() >= 50){
            Toast.makeText(getApplicationContext(), "Device id or device name is too long", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Helper.stringContainsItemFromList(linked_device_id, Constants.OUTPUT_ID)){
            Toast.makeText(getApplicationContext(), "Invalid output id", Toast.LENGTH_LONG).show();
            return;
        }
        if(Helper.checkUserHasDevice(linked_device_id, getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Device is already registered", Toast.LENGTH_LONG).show();
            return;
        }

        try{
            int thresholdCheck = Integer.parseInt(threshold);
            if(thresholdCheck >= Constants.MAX_LIGHT){
                Toast.makeText(getApplicationContext(), "Threshold is too high", Toast.LENGTH_LONG).show();
                return;
            }
            else if(thresholdCheck < Constants.MIN_LIGHT){
                Toast.makeText(getApplicationContext(), "Threshold is too low", Toast.LENGTH_LONG).show();
                return;
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Invalid threshold", Toast.LENGTH_LONG).show();
            return;
        }

        Garden_Database_Control.registerDevice(device_id, device_name,
                linked_device_id, linked_device_name, threshold, getApplicationContext(), RegisterLightSettingActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onSuccessResponse(String result) {
        startLoading();
        final Intent showResult = new Intent(getApplicationContext(), RegisterMessageActivity.class);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            showResult.putExtra("register_type", "Register new device");
            showResult.putExtra("register_message", jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Device_Control.turnDeviceOff(linked_device_id, linked_device_name, getApplicationContext());
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                stopLoading();
                startActivity(showResult);
                finish();
            }
        }.start();

        //startActivity(showResult);
        //finish();
    }

    private void startLoading(){
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        submitBtn.setClickable(false);
        useDefaultBtn.setClickable(false);
    }

    private void stopLoading(){
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }
}
