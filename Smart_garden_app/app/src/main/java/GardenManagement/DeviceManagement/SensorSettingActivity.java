package GardenManagement.DeviceManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import Helper.Constants;
import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Helper.VolleyCallBack;
import Helper.Helper;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.UserLoginManagement;

public class SensorSettingActivity extends AppCompatActivity implements VolleyCallBack {
    MqttAndroidClient client;
    Button submitBtn;
    private String device_type;
    private EditText new_device_nameET;
    private EditText new_output_idET;
    private EditText new_output_nameET;
    private EditText threshold_Input1;
    private EditText threshold_Input2;
    private DeviceInformation deviceInformation;
    private boolean changeSetting = false;
    // Loading animation
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);

        if (IOT_Server_Access.client == null) {
            IOT_Server_Access.connect(getApplicationContext());
        }
        client = IOT_Server_Access.client;

        device_type = getIntent().getStringExtra("device_setting.device_type");
        assert device_type != null;
        deviceInformation = Helper.findDeviceWithDeviceId(getIntent().getStringExtra("device_setting.device_id"),
                    UserLoginManagement.getInstance(this).getSensor());

        // Component
        TextView deviceId = findViewById(R.id.changeSetting_deviceId_TV);
        TextView deviceType = findViewById(R.id.changeSetting_deviceType_TV);
        TextView deviceName = findViewById(R.id.changeSetting_deviceName_TV);
        TextView outputID = findViewById(R.id.changeSetting_outputID_TV);
        TextView outputName = findViewById(R.id.changeSetting_outputName_TV);
//        ConstraintLayout threshold1 = findViewById(R.id.changeSetting_threshold1);
        ConstraintLayout current_threshold2 = findViewById(R.id.changeSetting_current_threshold2);
        TextView currentThresholdType1 = findViewById(R.id.changeSetting_ThresHoldType1_TV);
        TextView currentThresholdType2 = findViewById(R.id.changeSetting_ThresHoldType2_TV);
        TextView currentThreshold1 = findViewById(R.id.changeSetting_deviceThreshold1_TV);
        TextView currentThreshold2 = findViewById(R.id.changeSetting_deviceThreshold2_TV);
        ConstraintLayout threshold2 = findViewById(R.id.changeSetting_threshold2);
        TextView new_threshold_Type1 = findViewById(R.id.changeSetting_newThresholdType1);
        TextView new_threshold_Type2 = findViewById(R.id.changeSetting_newThresholdType2);

        new_device_nameET = findViewById(R.id.changeSetting_newDeviceName_ET);
        new_output_idET = findViewById(R.id.changeSetting_outputID_ET);
        new_output_nameET = findViewById(R.id.changeSetting_outputName_ET);
        threshold_Input1 = findViewById(R.id.changeSetting_thresholdInput1);
        threshold_Input2 = findViewById(R.id.changeSetting_thresholdInput2);

        submitBtn = findViewById(R.id.changeSetting_submitBtn);
        progressBarHolder = findViewById(R.id.changeDeviceSetting_progressBarHolder);

        // Set text
        assert deviceInformation != null;
        deviceId.setText(deviceInformation.getDevice_id());
        deviceType.setText(deviceInformation.getDevice_type());
        deviceName.setText(deviceInformation.getDevice_name());
        outputID.setText(deviceInformation.getLinked_device_id());
        outputName.setText(deviceInformation.getLinked_device_name());
        new_device_nameET.setText(deviceInformation.getDevice_name());
        new_output_idET.setText(deviceInformation.getLinked_device_id());
        new_output_nameET.setText(deviceInformation.getLinked_device_name());

        // Set threshold display
        if(device_type.contains("Light")){
            threshold2.setVisibility(View.GONE);
            current_threshold2.setVisibility(View.GONE);
            new_threshold_Type1.setText("New preferred light intensity:");
            currentThresholdType1.setText("Light intensity threshold: ");
            currentThreshold1.setText(deviceInformation.getThreshold() + " lux");
        }
        else{
            String[] thresholds = deviceInformation.getThreshold().split(":");
            new_threshold_Type1.setText("New preferred temperature(\u2103):");
            new_threshold_Type2.setText("New preferred humidity (%):");
            currentThresholdType1.setText("Temperature threshold: ");
            currentThreshold1.setText(thresholds[0] + "\u2103");
            currentThresholdType2.setText("Humidity threshold: ");
            currentThreshold2.setText(thresholds[1] + "%");
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSensorDeviceSetting();
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONArray jsonObject = new JSONArray(new String(message.getPayload()));
                String new_device_type = "";
                JSONObject device_info = jsonObject.getJSONObject(0);
                // Check sensor id
                if(Helper.stringContainsItemFromList(device_info.getString("device_id"), Constants.TEMPHUMI_SENSOR_ID)){
                    new_device_type = Constants.TEMPHUMI_SENSOR_TYPE;
                }
                else if(Helper.stringContainsItemFromList(device_info.getString("device_id"), Constants.LIGHT_SENSOR_ID)){
                    new_device_type = Constants.LIGHT_SENSOR_TYPE;
                }

                if(new_device_type.equals("") || !device_type.equals(new_device_type)){
                    Toast.makeText(getApplicationContext(), "Invalid sensor id", Toast.LENGTH_LONG).show();
                }
                else{
                    changeSetting = true;
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void changeSensorDeviceSetting(){
        final String device_id = deviceInformation.getDevice_id();
        final String new_input_name = new_device_nameET.getText().toString();
        final String new_output_id = new_output_idET.getText().toString();
        final String new_output_name = new_output_nameET.getText().toString();
        if(new_input_name.equals("") || new_output_id.equals("") || new_output_name.equals("")){
            Toast.makeText(getApplicationContext(), "Empty required field", Toast.LENGTH_LONG).show();
            return;
        }
        else if(!new_output_id.equals(deviceInformation.getLinked_device_id()) && Helper.checkUserHasDevice(new_output_id, this) ){
            Toast.makeText(getApplicationContext(), "Device id is already registered", Toast.LENGTH_LONG).show();
            return;
        }
        if(device_type.equals(Constants.LIGHT_SENSOR_TYPE)){
            final String threshold = threshold_Input1.getText().toString();
            if(threshold.equals("")){
                Toast.makeText(getApplicationContext(), "Empty required field", Toast.LENGTH_LONG).show();
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
            if(new_input_name.equals(deviceInformation.getDevice_name()) && new_output_id.equals(deviceInformation.getLinked_device_id())
                    && new_output_name.equals(deviceInformation.getLinked_device_name()) && threshold.equals(deviceInformation.getThreshold())){
                Toast.makeText(getApplicationContext(), "No changes happen", Toast.LENGTH_LONG).show();
                return;
            }
            searchNewSensorDevice(device_id, new_input_name, new_output_id, new_output_name, threshold);
        }
        else{
            final String temp_threshold = threshold_Input1.getText().toString();
            final String humid_threshold = threshold_Input2.getText().toString();
            if(temp_threshold.equals("") || humid_threshold.equals("")){
                Toast.makeText(getApplicationContext(), "Empty required field", Toast.LENGTH_LONG).show();
                return;
            }

            try{
                int tempThresholdCheck = Integer.parseInt(temp_threshold);
                int humidThresholdCheck = Integer.parseInt(humid_threshold);
                if(tempThresholdCheck >= Constants.MAX_TEMP){
                    Toast.makeText(getApplicationContext(), "Temperature threshold is too high", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(tempThresholdCheck < Constants.MIN_TEMP){
                    Toast.makeText(getApplicationContext(), "Temperature threshold is too low", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(humidThresholdCheck >= Constants.MAX_HUMID){
                    Toast.makeText(getApplicationContext(), "Humidity threshold is too high", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(humidThresholdCheck < Constants.MIN_HUMID){
                    Toast.makeText(getApplicationContext(), "Humidity threshold is too low", Toast.LENGTH_LONG).show();
                    return;
                }
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), "Invalid threshold", Toast.LENGTH_LONG).show();
                return;
            }

            final String threshold = temp_threshold + ":" + humid_threshold;
            if(new_input_name.equals(deviceInformation.getDevice_name()) && new_output_id.equals(deviceInformation.getLinked_device_id())
                    && new_output_name.equals(deviceInformation.getLinked_device_name()) && threshold.equals(deviceInformation.getThreshold())){
                Toast.makeText(getApplicationContext(), "No changes happen", Toast.LENGTH_LONG).show();
                return;
            }
            searchNewSensorDevice(device_id, new_input_name, new_output_id, new_output_name, threshold);
        }
    }

    @Override
    public void onSuccessResponse(String result) {
        try {
            final JSONObject jsonObject = new JSONObject(result);
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    stopLoading();
                    try {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent returnAct = new Intent(getApplicationContext(), DeviceListViewActivity.class);
                    returnAct.putExtra("device_list.type", "sensor");
                    startActivity(returnAct);
                    finish();
                }
            }.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startLoading(){
        submitBtn.setEnabled(false);
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
        submitBtn.setEnabled(true);
    }

    private void searchNewSensorDevice(final String device_id, final String device_name, final String new_output_id,
                              final String new_output_name, final String new_threshold){
        if(device_name.equals(deviceInformation.getDevice_name())){
            startLoading();
            Garden_Database_Control.changeDeviceSettings(device_id, device_name, new_output_id,
                    new_output_name, new_threshold, getApplicationContext(), SensorSettingActivity.this);
        }
        else {
            final String topic = device_name + "/" + device_id;
            IOT_Server_Access.Subscribe(topic, getApplicationContext());
            startLoading();
            new CountDownTimer(20000, 1000) {
                public void onTick(long millisUntilFinished) {
                    if (changeSetting) {
                        IOT_Server_Access.Unsubscribe(topic);
                        Garden_Database_Control.changeDeviceSettings(device_id, device_name, new_output_id,
                                new_output_name, new_threshold, getApplicationContext(), SensorSettingActivity.this);
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
    }
}