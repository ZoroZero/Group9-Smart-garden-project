package Login_RegisterUser;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartgarden.Constants;
import com.example.smartgarden.MainActivity;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import Background_service.RecordMeasurementService;
import Database.Garden_Database_Control;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;
import Registeration.RegisterDeviceSearchActivity;
import Registeration.RegisterPlant;
import Userprofile.PlantListView;
import Userprofile.ProfileActivity;
import Helper.Helper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, VolleyCallBack {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // If not login
        if(!UserLoginManagement.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // Connect to IOT server
        IOT_Server_Access.connect(getApplicationContext());

        //Component
        Button viewDeviceListBtn = findViewById(R.id.Home_ViewDeviceList_Btn);
        Button viewPlantListBtn = findViewById(R.id.Home_ViewPlantList_Btn);
        Button registerDeviceBtn = findViewById(R.id.Home_RegisterDevice_Btn);
        Button registerPlantBtn = findViewById(R.id.Home_RegisterPlant_Btn);
        Button viewReportBtn = findViewById(R.id.Home_ViewReport_Btn);
        Button logoutBtn = findViewById(R.id.Home_Logout_Btn);
        TextView helloUser = findViewById(R.id.Home_HelloUser_TV);

        // Set hello user
        helloUser.setText("Hello " + UserLoginManagement.getInstance(this).getUsername().toUpperCase());

        //Set on click
        viewDeviceListBtn.setOnClickListener(this);
        viewPlantListBtn.setOnClickListener(this);
        registerDeviceBtn.setOnClickListener(this);
        registerPlantBtn.setOnClickListener(this);
        viewReportBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        // Get device info
        Garden_Database_Control.FetchDevicesInfo(this, this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.Home_ViewDeviceList_Btn:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return;
            case R.id.Home_ViewPlantList_Btn:
                startActivity(new Intent(getApplicationContext(), PlantListView.class));
                finish();
                return;
            case R.id.Home_RegisterDevice_Btn:
                startActivity(new Intent(getApplicationContext(), RegisterDeviceSearchActivity.class));
                finish();
                return;
            case R.id.Home_RegisterPlant_Btn:
                startActivity(new Intent(getApplicationContext(), RegisterPlant.class));
                finish();
                return;
            case R.id.Home_ViewReport_Btn:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return;
            case R.id.Home_Logout_Btn:
                UserLoginManagement.getInstance(getApplicationContext()).logOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
        }
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
