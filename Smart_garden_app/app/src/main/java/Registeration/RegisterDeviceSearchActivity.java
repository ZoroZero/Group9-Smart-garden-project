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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import Helper.Constants;
import Helper.Helper;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.HomeActivity;

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
                if(device_id.equals("") || device_name.equals("")){
                    Toast.makeText(getApplicationContext(), "Required field is empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(device_id.length() >= 50 || device_name.length() >= 50){
                    Toast.makeText(getApplicationContext(), "Device id or device name is too long ", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(Helper.checkUserHasDevice(device_id, getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "Device is already registered", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Helper.stringContainsItemFromList(device_id, Constants.LIGHT_SENSOR_ID)
                        && !Helper.stringContainsItemFromList(device_id, Constants.TEMPHUMI_SENSOR_ID)) {
                    Toast.makeText(getApplicationContext(), "Invalid sensor id", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(Helper.stringContainsItemFromList(device_id, Constants.OUTPUT_ID)){
                    Toast.makeText(getApplicationContext(), "Invalid sensor id", Toast.LENGTH_LONG).show();
                    return;
                }
                searchDevice(device_id, device_name);
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
                // Check sensor id
                if(Helper.stringContainsItemFromList(device_info.getString("device_id"), Constants.TEMPHUMI_SENSOR_ID)){
                    device_type = Constants.TEMPHUMI_SENSOR_TYPE;
                }
                else if(Helper.stringContainsItemFromList(device_info.getString("device_id"), Constants.LIGHT_SENSOR_ID)){
                    device_type = Constants.LIGHT_SENSOR_TYPE;
                }

                if(device_type.equals("")){
                    Toast.makeText(getApplicationContext(), "Invalid sensor id", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent deviceSetting = null;
                if(device_type.equals(Constants.LIGHT_SENSOR_TYPE)) {
                    deviceSetting = new Intent(getApplicationContext(), RegisterLightSettingActivity.class);
                }
                else if(device_type.equals(Constants.TEMPHUMI_SENSOR_TYPE)){
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

    private void searchDevice(String device_id, String device_name){
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
                Toast.makeText(getApplicationContext(), "No device " + topic + " found", Toast.LENGTH_LONG).show();
                IOT_Server_Access.Unsubscribe(topic);
            }
        }.start();
    }


}
