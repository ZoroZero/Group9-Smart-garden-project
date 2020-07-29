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

import Helper.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

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
        if(Objects.equals(getIntent().getStringExtra("register_type"), "Register new device"))
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
                    if(obj.getString("status").equals("null")){
                        get_status[i] = "No record";
                        get_status_date[i] = "No record";
                    }

                    if(Helper.stringContainsItemFromList(get_device_id[i], Constants.OUTPUT_ID))
                        get_device_type[i] = Constants.OUTPUT_TYPE;
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.LIGHT_SENSOR_ID))
                        get_device_type[i] = Constants.LIGHT_SENSOR_TYPE;
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.TEMPHUMI_SENSOR_ID))
                        get_device_type[i] = Constants.TEMPHUMI_SENSOR_TYPE;
                }
                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, get_linked_device_id,
                        get_linked_device_name, get_device_type, get_threshold, get_status, get_status_date);

                // Start background service to record device measure
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
