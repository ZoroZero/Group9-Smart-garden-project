package Registeration;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.Constants;
import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import Helper.DeviceInformation;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.HomeActivity;
import Login_RegisterUser.UserLoginManagement;
import Helper.Helper;

public class RegisterDeviceSearchActivity extends AppCompatActivity {
    MqttAndroidClient client;
    Intent goToSetting = null;
    private EditText device_nameET;
    private EditText device_IdET;
    private Button searchDeviceBtn;

    private String topic;
    private String device_type = "";
    private String device_id;
    private String device_name;

    // Loading animation
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_device_search);

        if (IOT_Server_Access.client == null) {
            IOT_Server_Access.connect(getApplicationContext());
        }
        client = IOT_Server_Access.client;

        searchDeviceBtn = findViewById(R.id.searchDeviceBtn);
        device_IdET = findViewById(R.id.registerDeviceIDEditText);
        device_nameET = findViewById(R.id.registerDeviceNameEditText);

        progressBarHolder = findViewById(R.id.progressBarHolder);
        searchDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                device_id = device_IdET.getText().toString();
                device_name = device_nameET.getText().toString();
                if(device_id.equals("") || device_name.equals("") || device_type.equals("")){
                    Toast.makeText(getApplicationContext(), "Required field is empty", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(checkUserHasDevice(device_id, device_name)){
                    Toast.makeText(getApplicationContext(), "Device already registered", Toast.LENGTH_LONG).show();
                    return;
                }
                if(Helper.stringContainsItemFromList(device_id, Constants.OUTPUT_ID)) {
                    Toast.makeText(getApplicationContext(), "Invalid sensor id", Toast.LENGTH_LONG).show();
                    return;
                }
                check_sensor_exist(device_id, device_name);
                //searchDevice();
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONArray jsonObject = new JSONArray(new String(message.getPayload()));

                JSONObject device_info = jsonObject.getJSONObject(0);
                // Check light sensor id
                if(device_type.contains("Light") && !Helper.stringContainsItemFromList(device_info.getString("device_id"), Constants.LIGHT_SENSOR_ID)){
                    Toast.makeText(getApplicationContext(), "Invalid light sensor id", Toast.LENGTH_LONG).show();
                    return;
                }

                //Check Temp humid sensor id
                if(device_type.contains("Temperature humidity") && !Helper.stringContainsItemFromList(device_info.getString("device_id"), Constants.TEMPHUMI_SENSOR_ID)){
                    Toast.makeText(getApplicationContext(), "Invalid temperature humidity sensor id", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent deviceSetting = null;
                if(device_type.equals("Light sensor")) {
                    deviceSetting = new Intent(getApplicationContext(), RegisterDeviceSettingActivity.class);
                }
                else if(device_type.equals("Temperature humidity sensor")){
                    deviceSetting = new Intent(getApplicationContext(), RegisterTemperatureHumiditySettingActivity.class);
                }
                assert deviceSetting != null;
                deviceSetting.putExtra("device_id", device_id);
                deviceSetting.putExtra("device_type", device_type);
                deviceSetting.putExtra("device_name", device_name);


                goToSetting = deviceSetting;
                //startActivity(deviceSetting);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        // Setup menu
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startLoading(){
        searchDeviceBtn.setEnabled(false);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void stopLoading(){
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        searchDeviceBtn.setEnabled(true);
    }


    private void check_sensor_exist(String device_id, String device_name){
        topic = device_name + "/" + device_id;
        IOT_Server_Access.Subscribe(topic, getApplicationContext());
        startLoading();
        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(goToSetting != null) {
                    stopLoading();
                    IOT_Server_Access.Unsubscribe(topic);
                    startActivity(goToSetting);
                    finish();
                    this.cancel();
                }
            }
            public void onFinish() {
                stopLoading();
                Toast.makeText(getApplicationContext(), "No device " + topic + " with " + device_type + " found", Toast.LENGTH_LONG).show();
                IOT_Server_Access.Unsubscribe(topic);
            }
        }.start();
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_sensor:
                if (checked)
                    device_type = "Light sensor";
                break;
            case R.id.radio_output:
                if (checked)
                    device_type = "Temperature humidity sensor";
                break;
        }
    }

    // Check if device has existed on user
    public boolean checkUserHasDevice(String device_id, String device_name){
        DeviceInformation[] user_device_information = UserLoginManagement.getInstance(this).getDevice_list();
        if(user_device_information == null){
            return false;
        }
        for (DeviceInformation deviceInformation : user_device_information) {
            if (device_id.equals(deviceInformation.getDevice_id()) && device_name.equals(deviceInformation.getDevice_name())) {
                return true;
            }
        }
        return false;
    }
}
