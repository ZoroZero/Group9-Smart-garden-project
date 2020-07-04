package Registeration;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import Background_service.RecordMeasurementService;
import Database.Garden_Database_Control;
import Helper.Helper;
import Helper.VolleyCallBack;
import Login_RegisterUser.HomeActivity;
import Login_RegisterUser.UserLoginManagement;

public class RegisterMessageActivity extends AppCompatActivity implements VolleyCallBack {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_plant_message);

        TextView type = findViewById(R.id.registerMessageTypeTV);
        TextView message = findViewById(R.id.registerPlantMessageTV);
        Button returnBtn = findViewById(R.id.registerMessageReturnButton);
        //Set text
        type.setText(getIntent().getStringExtra("register_type"));
        message.setText(getIntent().getStringExtra("register_message"));

        //Set button on click event
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        Garden_Database_Control.FetchDevicesInfo(this, this);
    }

    @Override
    public void onSuccessResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("error")) {
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                final String[] get_device_id = new String[jsonArray.length()];
                final String[] get_device_name = new String[jsonArray.length()];
                final String[] linked_device_id = new String[jsonArray.length()];
                final String[] linked_device_name = new String[jsonArray.length()];
                final String[] device_type = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    get_device_id[i] = obj.getString("device_id");
                    get_device_name[i] = obj.getString("device_name");
                    linked_device_id[i] = obj.getString("linked_device_id");
                    linked_device_name[i] = obj.getString("linked_device_name");
                    if(Helper.stringContainsItemFromList(get_device_id[i], Constants.OUTPUT_ID))
                        device_type[i] = "Output";
                    else if (get_device_id[i].contains(Constants.LIGHT_SENSOR_ID))
                        device_type[i] = "Light Sensor";
                    else if (get_device_id[i].contains(Constants.TEMPHUMI_SENSOR_ID))
                        device_type[i] = "TempHumi Sensor";
                }
                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, linked_device_id, linked_device_name, device_type);

                //Start background service to record device measure
                RecordMeasurementService mYourService = new RecordMeasurementService();
                Intent mServiceIntent = new Intent(this, mYourService.getClass());
                if (!isMyServiceRunning(mYourService.getClass())) {
                    startService(mServiceIntent);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }
}
