package Registeration;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.MainActivity;
import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import Helper.DeviceInformation;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.UserLoginManagement;

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
                }
                else if(checkUserHasDevice(device_id, device_name)){
                    Toast.makeText(getApplicationContext(), "Device already registered", Toast.LENGTH_LONG).show();
                }
                else if(device_type.equals("output")){
                    if(!device_id.contains("LightD")){
                        Toast.makeText(getApplicationContext(), "Invalid output id", Toast.LENGTH_LONG).show();
                        return;
                    }
                    goToSetting = new Intent(getApplicationContext(), RegisterDeviceSettingActivity.class);
                    goToSetting.putExtra("device_id", device_id);
                    goToSetting.putExtra("device_type", device_type);
                    goToSetting.putExtra("device_name", device_name);
                    startActivity(goToSetting);
                }
                else{
                    if(device_id.contains("Light_D")) {
                        Toast.makeText(getApplicationContext(), "Invalid sensor id", Toast.LENGTH_LONG).show();
                        return;
                    }
                    check_sensor_exist(device_id, device_name);
                }
                //searchDevice();
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //JSONObject jsonObject = new JSONObject(new String(message.getPayload()));

                //deviceSetting.putExtra("device_id", jsonObject.getString("device_id"));
                // deviceSetting.putExtra("device_name", jsonObject.getString("device_name"));
                Intent deviceSetting = new Intent(getApplicationContext(), RegisterDeviceSettingActivity.class);
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
                    startActivity(goToSetting);
                    finish();
                    this.cancel();
                }
            }
            public void onFinish() {
                stopLoading();
                Toast.makeText(getApplicationContext(), "No device " + topic + " found", Toast.LENGTH_LONG).show();
                IOT_Server_Access.Unsubscribe(topic, getApplicationContext());
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
                    device_type = "sensor";
                break;
            case R.id.radio_output:
                if (checked)
                    device_type = "output";
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
