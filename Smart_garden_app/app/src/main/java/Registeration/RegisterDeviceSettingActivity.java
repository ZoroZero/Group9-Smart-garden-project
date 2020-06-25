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

import com.example.smartgarden.Constants;
import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import Database.Garden_Database_Control;
import DeviceController.Device_Control;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;
import Helper.Helper;

public class RegisterDeviceSettingActivity extends AppCompatActivity implements VolleyCallBack {

    // MQTT client
    MqttAndroidClient client = null;

    // Component
    private EditText thresholdET;
    private EditText linkedDeviceId;
    private EditText linkedDeviceName;
    private boolean linked = false;
    private Button submitBtn;
    private String linked_device_id;
    private String linked_device_name;

    // Search button
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

        Button useDefaultBtn = findViewById(R.id.useDefaultButton);

        // Set device_type
        String device_type = getIntent().getStringExtra("device_type");
        assert device_type != null;
        deviceTypeTV.setText(device_type.toUpperCase());

        // Set submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                final String threshold = thresholdET.getText().toString();
                final String device_id = getIntent().getStringExtra("device_id");
                final String device_name = getIntent().getStringExtra("device_name");
                linked_device_id = linkedDeviceId.getText().toString();
                linked_device_name = linkedDeviceName.getText().toString();
                if (linked_device_id.equals("") || linked_device_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Helper.stringContainsItemFromList(linked_device_id, Constants.OUTPUT_ID)){
                    Toast.makeText(getApplicationContext(), "Invalid output id", Toast.LENGTH_LONG).show();
                    return;
                }

                Garden_Database_Control.registerDevice(device_id, device_name,
                        linked_device_id, linked_device_name, threshold, getApplicationContext(), RegisterDeviceSettingActivity.this);
                Device_Control.turnDeviceOff(linked_device_id, linked_device_name, getApplicationContext());
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
                linked = true;
                //startActivity(deviceSetting);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


    }

    protected void checkLinkedDevice(final String device_id, final String device_name,
                                     final String linked_device_id, final String linked_device_name, final String threshold) {
        final String topic = linked_device_name + "/" + linked_device_id;
        IOT_Server_Access.Subscribe(topic, getApplicationContext());
        startLoading();
        new CountDownTimer(20000, 1000) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onTick(long millisUntilFinished) {
                if (linked) {
                    stopLoading();
                    Garden_Database_Control.registerDevice(linked_device_id, linked_device_name,
                            device_id, device_name, threshold, getApplicationContext(), RegisterDeviceSettingActivity.this);
                    Device_Control.turnDeviceOff(device_id, device_name, getApplicationContext());
                    this.cancel();
                }
            }

            public void onFinish() {
                stopLoading();
                Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
                IOT_Server_Access.Unsubscribe(topic);
            }
        }.start();
    }

    private void startLoading() {
        submitBtn.setEnabled(false);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        submitBtn.setEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onSuccessResponse(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Device_Control.turnDeviceOff(linked_device_id, linked_device_name, getApplicationContext());
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
