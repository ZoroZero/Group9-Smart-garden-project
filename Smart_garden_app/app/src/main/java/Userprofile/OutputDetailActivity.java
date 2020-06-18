package Userprofile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import DeviceController.Device_Control;
import Helper.VolleyCallBack;

public class OutputDetailActivity extends AppCompatActivity implements View.OnClickListener, VolleyCallBack {

    private TextView device_statusTV;
    private String deviceStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_detail);

        //Component
        TextView device_idTV = findViewById(R.id.OutputDeviceDetail_DeviceID_TV);
        TextView device_nameTV = findViewById(R.id.outputDeviceDetail_DeviceName_TV);
        TextView device_typeTV = findViewById(R.id.outputDeviceDetail_DeviceType_TV);
        device_statusTV = findViewById(R.id.outputDeviceDetail_DeviceStatus_TV);
        Button turnOn_Btn = findViewById(R.id.outputDeviceDetail_TurnOn_Btn);
        Button turnOff_Btn = findViewById(R.id.outputDeviceDetail_TurnOff_Btn);
        Button sleep_Btn = findViewById(R.id.outputDeviceDetail_Sleep_Btn);
        Button return_Btn = findViewById(R.id.item_returnButton);
        //Set text
        device_idTV.setText(getIntent().getStringExtra("device_detail.device_id"));
        device_nameTV.setText(getIntent().getStringExtra("device_detail.device_name"));
        device_typeTV.setText(getIntent().getStringExtra("device_detail.device_type"));

        getDeviceInfo();

        // Set onclick event
        turnOn_Btn.setOnClickListener(this);
        turnOff_Btn.setOnClickListener(this);
        sleep_Btn.setOnClickListener(this);
        return_Btn.setOnClickListener(this);

    }

    private void getDeviceInfo(){
        //Fetch output status.
        Garden_Database_Control.getOutputStatus(getIntent().getStringExtra("device_detail.device_id"), this, this);
    }

    @Override
    public void onSuccessResponse(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            if(!jsonObject.getBoolean("error")){
                if(jsonObject.has("status_list")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("status_list");
                    JSONObject status  = jsonArray.getJSONObject(0);
                    //Log.i("JSON object", String.valueOf(reading));
                    deviceStatus = status.getString("status");
                    device_statusTV.setText(deviceStatus);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.outputDeviceDetail_TurnOn_Btn:
                switch (deviceStatus) {
                    case "Off":
                        Toast.makeText(getApplicationContext(), "Can not turn on right now", Toast.LENGTH_SHORT).show();
                        break;
                    case "On-0":
                        Device_Control.turnDeviceOn(getIntent().getStringExtra("device_detail.device_id"),
                                getIntent().getStringExtra("device_detail.device_name"), getApplicationContext());
                        deviceStatus = "On-255";
                        device_statusTV.setText(deviceStatus);
                        break;
                    case "On-255":
                        Toast.makeText(getApplicationContext(), "Device is already on now", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case R.id.outputDeviceDetail_TurnOff_Btn:
                switch (deviceStatus) {
                    case "Off":
                    case "On-255":
                        Device_Control.turnDeviceOff(getIntent().getStringExtra("device_detail.device_id"),
                                getIntent().getStringExtra("device_detail.device_name"), getApplicationContext());
                        deviceStatus = "On-0";
                        device_statusTV.setText(deviceStatus);
                        break;
                    case "On-0":
                        Toast.makeText(getApplicationContext(), "Device is already off now", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case R.id.outputDeviceDetail_Sleep_Btn:
                switch (deviceStatus) {
                    case "On-0":
                    case "On-255":
                        Device_Control.putDeviceToOff(getIntent().getStringExtra("device_detail.device_id"),
                                getIntent().getStringExtra("device_detail.device_name"), getApplicationContext());
                        deviceStatus = "Off";
                        device_statusTV.setText(deviceStatus);
                        break;
                    case "Off":
                        Toast.makeText(getApplicationContext(), "Device is already at sleep now", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case R.id.item_returnButton:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                break;
        }
    }
}
