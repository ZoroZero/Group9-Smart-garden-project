package Registeration;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.MainActivity;
import com.example.smartgarden.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import IOT_Server.IOT_Server_Access;

public class RegisterDeviceSearchActivity extends AppCompatActivity {
    MqttAndroidClient client;
    Intent goToSetting = null;
    private EditText device_nameET;
    private EditText device_IdET;
    private String topic;
    private Button searchDeviceBtn;
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
                final String device_id = device_IdET.getText().toString();
                final String device_name = device_nameET.getText().toString();
                if (device_id.equals("") || device_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Required field is empty", Toast.LENGTH_LONG).show();
                    return;
                }
                topic = device_name + "/" + device_id;
                IOT_Server_Access.Subscribe(topic, getApplicationContext());
                startLoading();
                new CountDownTimer(20000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        if (goToSetting != null) {
                            stopLoading();
                            startActivity(goToSetting);
                            finish();
                            this.cancel();
                        }
                    }

                    public void onFinish() {
                        stopLoading();
                        Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
                        IOT_Server_Access.Unsubscribe(topic, getApplicationContext());
                    }
                }.start();
                //searchDevice();
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONObject jsonObject = new JSONObject(new String(message.getPayload()));
                Intent deviceSetting = new Intent(getApplicationContext(), RegisterDeviceSettingActivity.class);
                deviceSetting.putExtra("device_id", jsonObject.getString("device_id"));
                deviceSetting.putExtra("device_type", jsonObject.getString("device_type"));
                deviceSetting.putExtra("device_name", jsonObject.getString("device_name"));

                goToSetting = deviceSetting;
                //startActivity(deviceSetting);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void searchDevice(){
        final String device_id = device_IdET.getText().toString();
        final String device_name = device_nameET.getText().toString();
        if (device_id.equals("") || device_name.equals("")) {
            Toast.makeText(getApplicationContext(), "Required field is empty", Toast.LENGTH_LONG).show();
            return;
        }
        topic = device_name + "/" + device_id;
        IOT_Server_Access.Subscribe(topic, getApplicationContext());
        startLoading();
        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (goToSetting != null) {
                    stopLoading();
                    startActivity(goToSetting);
                    finish();
                    this.cancel();
                }
            }

            public void onFinish() {
                stopLoading();
                Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
                IOT_Server_Access.Unsubscribe(topic, getApplicationContext());
            }
        }.start();
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
}
