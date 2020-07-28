package GardenManagement.DeviceManagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import Helper.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import DeviceController.Device_Control;
import Helper.DeviceInformation;
import Helper.Helper;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class OutputDetailActivity extends AppCompatActivity implements View.OnClickListener, VolleyCallBack {

    private TextView device_statusTV;
    private String deviceStatus = "";
    private TextView device_lastReadingTV;
    private TextView device_lastReading1TV;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar1;
    private DeviceInformation deviceInformation;
    private DeviceInformation linkedSensorInformation;
    private SeekBar lightPowerSB;
    private TextView lightPowerTV;
    ImageButton wake_Btn;
    ImageButton sleep_Btn;
    private int intensity = 0;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_detail);

        //Component
        TextView device_IDTV = findViewById(R.id.OutputDeviceDetail_DeviceID_TV);
        TextView device_NameTV = findViewById(R.id.OutputDeviceDetail_DeviceName_TV);
        TextView device_TopicTV = findViewById(R.id.OutputDeviceDetail_DeviceTopic_TV);
        TextView linked_device_idTV = findViewById(R.id.outputDeviceDetail_LinkedDeviceId_TV);
        TextView linked_device_nameTV = findViewById(R.id.OutputDeviceDetail_LinkedSensorName_TV);
        TextView device_typeTV = findViewById(R.id.outputDeviceDetail_DeviceType_TV);
        device_statusTV = findViewById(R.id.outputDeviceDetail_DeviceStatus_TV);
        device_lastReadingTV = findViewById(R.id.OutputDetail_DeviceLastReading_TV);

        TextView device_readingTypeTV = findViewById(R.id.OutputDetail_readingType_TV);
        device_lastReading1TV = findViewById(R.id.OutputDetail_DeviceLastReading1_TV);
        TextView device_readingType1TV = findViewById(R.id.OutputDetail_readingType1_TV);
        ImageView readingTypeIcon = findViewById(R.id.OutputDetail_readingTypeIcon_TV);
        ImageView readingTypeIcon1 = findViewById(R.id.OutputDetail_readingTypeIcon1_TV);
        readingBar = findViewById(R.id.OutputDetail_DeviceLastReading);
        readingBar1 = findViewById(R.id.OutputDetail_DeviceLastReading1);
        ConstraintLayout readingLayout = findViewById(R.id.OutputDetail_reading);


        wake_Btn = findViewById(R.id.outputDeviceDetail_Wake_Btn);
        sleep_Btn = findViewById(R.id.outputDeviceDetail_Sleep_Btn);
        Button return_Btn = findViewById(R.id.item_returnButton);
        Button changeSetting_Btn = findViewById(R.id.outputDevice_changeSettingBtn);

        lightPowerSB = findViewById(R.id.outputDeviceDetail_LightPower_SeekBar);
        lightPowerTV = findViewById(R.id.outputDeviceDetail_LightPower_TV);

        //Set text
        device_IDTV.setText(getIntent().getStringExtra("device_detail.device_id"));
        device_NameTV.setText(getIntent().getStringExtra("device_detail.device_name"));
        device_TopicTV.setText(getIntent().getStringExtra("device_detail.device_name") + "/" +
                getIntent().getStringExtra("device_detail.device_id"));

        linked_device_idTV.setText(getIntent().getStringExtra("device_detail.linked_device_id"));
        linked_device_nameTV.setText(getIntent().getStringExtra("device_detail.linked_device_name"));
        device_typeTV.setText(getIntent().getStringExtra("device_detail.device_type"));
        getDeviceInfo();

        // Get Devices information
        deviceInformation = Helper.findDeviceWithDeviceId(getIntent().getStringExtra("device_detail.device_id"),
                UserLoginManagement.getInstance(getApplicationContext()).getOutput());
        assert deviceInformation != null;
        linkedSensorInformation = Helper.findDeviceWithDeviceId(deviceInformation.getLinked_device_id(),
                UserLoginManagement.getInstance(getApplicationContext()).getSensor());

        //Set device reading view
        assert linkedSensorInformation != null;
        if(linkedSensorInformation.getDevice_type().equals(Constants.LIGHT_SENSOR_TYPE)){
            readingLayout.setVisibility(View.GONE);
            device_readingType1TV.setText("Light intensity");
            readingTypeIcon1.setImageResource(R.drawable.ic_light_30);
            readingBar1.setEndValue(Constants.MAX_LIGHT);
        }
        else if(linkedSensorInformation.getDevice_type().equals(Constants.TEMPHUMI_SENSOR_TYPE)){
            device_readingTypeTV.setText("Humidity");
            device_readingType1TV.setText("Temperature");
            readingTypeIcon.setImageResource(R.drawable.ic_humidity_30);
            readingTypeIcon1.setImageResource(R.drawable.ic_temphumi_sensor_icon_black);
            readingBar.setEndValue(Constants.MAX_HUMID);
            readingBar1.setEndValue(Constants.MAX_TEMP);
        }

        // Set seek bar
        lightPowerSB.setMax(100);
        if(!deviceInformation.getStatus().equals("Off")){
            String lightIntensity = deviceInformation.getStatus().split("-")[1];
            lightPowerSB.setProgress(Math.min(100, Integer.parseInt(lightIntensity)*100/255));
            lightPowerTV.setText("Light intensity: " + Integer.parseInt(lightIntensity)/255*100);
        }
        else{
            lightPowerTV.setText("Light intensity: " + 0);
            lightPowerSB.setProgress(0);
            lightPowerSB.setClickable(false);
        }

        lightPowerSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lightPowerTV.setText("Light intensity: " + progress);
                intensity = progress*255/100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("Pressed", "pressed");
                Device_Control.turnDeviceLightIntensity(deviceInformation.getDevice_id(), deviceInformation.getDevice_name(),
                        getApplicationContext(), intensity +"");
            }
        });


        wake_Btn.setOnClickListener(this);
        sleep_Btn.setOnClickListener(this);
        return_Btn.setOnClickListener(this);
        changeSetting_Btn.setOnClickListener(this);

        final Handler handler=new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                // Get reading
                getDeviceInfo();
                handler.postDelayed(this,2000); // set time here to refresh textView
            }
        });
    }

    private void getDeviceInfo(){
        //Fetch output status.
        Garden_Database_Control.FetchDevicesInfo(this, this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("error")) {
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                final String[] get_device_id = new String[jsonArray.length()];
                final String[] get_device_name = new String[jsonArray.length()];
                final String[] get_linked_device_id = new String[jsonArray.length()];
                final String[] get_linked_device_name = new String[jsonArray.length()];
                final String[] get_device_type = new String[jsonArray.length()];
                final String[] get_threshold = new String[jsonArray.length()];
                final String[] get_status = new String[jsonArray.length()];
                final String[] get_status_date = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    get_device_id[i] = obj.getString("device_id");
                    get_device_name[i] = obj.getString("device_name");
                    get_linked_device_id[i] = obj.getString("linked_device_id");
                    get_linked_device_name[i] = obj.getString("linked_device_name");
                    get_threshold[i] = obj.getString("threshold");
                    get_status[i] = obj.getString("status");
                    get_status_date[i] = obj.getString("date");
                    if (obj.getString("status").equals("null")) {
                        get_status[i] = "No record";
                        get_status_date[i] = "No record";
                    }

                    if (Helper.stringContainsItemFromList(get_device_id[i], Constants.OUTPUT_ID))
                        get_device_type[i] = Constants.OUTPUT_TYPE;
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.LIGHT_SENSOR_ID))
                        get_device_type[i] = Constants.LIGHT_SENSOR_TYPE;
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.TEMPHUMI_SENSOR_ID))
                        get_device_type[i] = Constants.TEMPHUMI_SENSOR_TYPE;
                }

                // Update user devices
                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, get_linked_device_id,
                        get_linked_device_name, get_device_type, get_threshold, get_status, get_status_date);
                deviceInformation = Helper.findDeviceWithDeviceId(getIntent().getStringExtra("device_detail.device_id"),
                        UserLoginManagement.getInstance(getApplicationContext()).getOutput());

                // Set status
                assert deviceInformation != null;
                // Set sensor reading
                linkedSensorInformation = Helper.findDeviceWithDeviceId(deviceInformation.getLinked_device_id(),
                        UserLoginManagement.getInstance(getApplicationContext()).getSensor());
                assert linkedSensorInformation != null;
                if(linkedSensorInformation.getDevice_type().equals(Constants.TEMPHUMI_SENSOR_TYPE)) {
                    if(linkedSensorInformation.getStatus().equals("No record")){
                        device_lastReadingTV.setText("No record");
                        device_lastReading1TV.setText("No record");
                    }
                    else {
                        String[] measurements = linkedSensorInformation.getStatus().split(":");
                        device_lastReadingTV.setText(measurements[1] + "%");
                        readingBar1.setValue(Math.min(Integer.parseInt(measurements[1]), readingBar.getEndValue()));
                        device_lastReading1TV.setText(measurements[0] + "\u2103");
                        readingBar1.setValue(Math.min(Integer.parseInt(measurements[0]), readingBar1.getEndValue()));
                    }
                }
                else{
                    if(linkedSensorInformation.getStatus().equals("No record")){
                        device_lastReading1TV.setText("No record");
                    }
                    else {
                        String measurement = linkedSensorInformation.getStatus();
                        device_lastReading1TV.setText(measurement + " lux");
                        readingBar1.setValue(Math.min(Integer.parseInt(measurement), readingBar1.getEndValue()));
                    }
                }


                if(deviceStatus.equals(deviceInformation.getStatus())){
                    return;
                }
                deviceStatus = deviceInformation.getStatus();

                if(deviceStatus.equals("Off")) {
                    device_statusTV.setText(deviceStatus);
                }
                else{
                    device_statusTV.setText("On");
                }

                // Set seek bar
                if(!deviceInformation.getStatus().equals("Off")){
                    String lightIntensity = deviceInformation.getStatus().split("-")[1];
                    lightPowerSB.setProgress(Integer.parseInt(lightIntensity)*100/255);
                    lightPowerTV.setText("Light intensity: " + Integer.parseInt(lightIntensity)*100/255);
                }
                else{
                    lightPowerTV.setText("Light intensity: " + 0);
                    lightPowerSB.setProgress(0);
                    lightPowerSB.setClickable(false);
                }

                // Set buttons
                if ("Off".equals(deviceStatus)) {
                    sleep_Btn.setBackgroundResource(R.drawable.round_disable_shadow_btn);
                    wake_Btn.setBackgroundResource(R.drawable.round_green_shadow_btn);
                } else {
                    wake_Btn.setBackgroundResource(R.drawable.round_disable_shadow_btn);
                    sleep_Btn.setBackgroundResource(R.drawable.round_green_shadow_btn);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.outputDeviceDetail_Sleep_Btn:
                if ("Off".equals(deviceStatus)) {
                    Log.i("Message", "Device is already at sleep now");
                }
                else {
                    Device_Control.putDeviceToOff(getIntent().getStringExtra("device_detail.device_id"),
                            getIntent().getStringExtra("device_detail.device_name"), getApplicationContext());
                    deviceStatus = "Off";

                    device_statusTV.setText(deviceStatus);
                    // Set clickable
                    lightPowerSB.setEnabled(false);
                    // Set button style
                    sleep_Btn.setBackgroundResource(R.drawable.round_disable_shadow_btn);
                    wake_Btn.setBackgroundResource(R.drawable.round_green_shadow_btn);
                }
                break;
            case R.id.outputDeviceDetail_Wake_Btn:
                if ("Off".equals(deviceStatus)) {
                    Device_Control.turnDeviceOff(getIntent().getStringExtra("device_detail.device_id"),
                            getIntent().getStringExtra("device_detail.device_name"), getApplicationContext());
                    deviceStatus = "On-0";

                    device_statusTV.setText("On");
                    // set clickable
                    lightPowerSB.setEnabled(true);
                    // Set button style
                    wake_Btn.setBackgroundResource(R.drawable.round_disable_shadow_btn);
                    sleep_Btn.setBackgroundResource(R.drawable.round_green_shadow_btn);
                }
                else {
                    Log.i("Message", "Device is already activate now");
                }
                break;
            case R.id.item_returnButton:
                startActivity(new Intent(getApplicationContext(), DeviceListOverViewActivity.class));
                finish();
                break;
            case R.id.outputDevice_changeSettingBtn:
                Intent changeSetting = new Intent(getApplicationContext(), OutputSettingActivity.class);
                changeSetting.putExtra("device_setting.device_id", deviceInformation.getDevice_id());
                startActivity(changeSetting);

        }
    }
}
