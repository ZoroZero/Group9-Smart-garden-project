package Login_RegisterUser;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import Helper.Constants;
import Report.ViewReport;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import Background_service.RecordMeasurementService;
import Database.Garden_Database_Control;
import Helper.Helper;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;
import Registeration.RegisterDeviceSearchActivity;
import Registeration.RegisterPlant;
import GardenManagement.DeviceManagement.DeviceListOverViewActivity;
import GardenManagement.PlantManagement.PlantListView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, VolleyCallBack {

    TextView averageTemp_TV;
    TextView averageHumid_TV;
    TextView averageLight_TV;
    TextView number_devices_TV;
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

        averageTemp_TV = findViewById(R.id.Home_DeviceLastReading_TV);
        averageHumid_TV = findViewById(R.id.Home_DeviceLastReading1_TV);
        averageLight_TV = findViewById(R.id.Home_DeviceLastReading2_TV);
        number_devices_TV = findViewById(R.id.Home_DeviceLastReading3_TV);
        // Set hello user
        helloUser.setText(UserLoginManagement.getInstance(this).getUsername().toUpperCase() + "'s garden");
        //Set on click
        viewDeviceListBtn.setOnClickListener(this);
        viewPlantListBtn.setOnClickListener(this);
        registerDeviceBtn.setOnClickListener(this);
        registerPlantBtn.setOnClickListener(this);
        viewReportBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        // Get device info
        Garden_Database_Control.FetchDevicesInfo(this, this);

//        FirebaseMessaging.getInstance().subscribeToTopic("Smart_garden");
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("Get id", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        // Log and toast
//                        Log.d("TAG", token);
//                        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
//                    }
//                } );

        // Init notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_Name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(Constants.CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }

        final Handler handler=new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                // Get device info
                Garden_Database_Control.FetchDevicesInfo(getApplicationContext(), HomeActivity.this);
                handler.postDelayed(this,3000); // set time here to refresh textView
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.Home_ViewDeviceList_Btn:
                startActivity(new Intent(getApplicationContext(), DeviceListOverViewActivity.class));
                return;
            case R.id.Home_ViewPlantList_Btn:
                startActivity(new Intent(getApplicationContext(), PlantListView.class));
                return;
            case R.id.Home_RegisterDevice_Btn:
                startActivity(new Intent(getApplicationContext(), RegisterDeviceSearchActivity.class));
                return;
            case R.id.Home_RegisterPlant_Btn:
                startActivity(new Intent(getApplicationContext(), RegisterPlant.class));
                return;
            case R.id.Home_ViewReport_Btn:
                startActivity(new Intent(getApplicationContext(), ViewReport.class));
                return;
            case R.id.Home_Logout_Btn:
                UserLoginManagement.getInstance(getApplicationContext()).logOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
        }
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
                float sum_temp = 0;
                float sum_humid = 0;
                float sum_light = 0;
                int count_temp_humid = 0;
                int count_light = 0;
                int count_output = 0;
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
                    // Get device type and summarize
                    if(Helper.stringContainsItemFromList(get_device_id[i], Constants.OUTPUT_ID)) {
                        get_device_type[i] = Constants.OUTPUT_TYPE;
                        if(!get_status[i].equals("Off")){
                            count_output += 1;
                        }
                    }
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.LIGHT_SENSOR_ID)) {
                        get_device_type[i] = Constants.LIGHT_SENSOR_TYPE;
                        if(!get_status[i].equals("No record")){
                            count_light += 1;
                            sum_light += Integer.parseInt(get_status[i]);
                        }
                    }
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.TEMPHUMI_SENSOR_ID)) {
                        get_device_type[i] = Constants.TEMPHUMI_SENSOR_TYPE;
                        if(!get_status[i].equals("No record")){
                            count_temp_humid += 1;
//                            Log.i("Status", get_status[i]);
                            sum_temp += Integer.parseInt(get_status[i].split(":")[0]);
                            sum_humid += Integer.parseInt(get_status[i].split(":")[1]);
                        }
                    }
                }
                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, get_linked_device_id,
                        get_linked_device_name, get_device_type, get_threshold, get_status, get_status_date);
                // Summarize reading for user
                if (count_temp_humid == 0) {
                    averageTemp_TV.setText("No reading");
                    averageHumid_TV.setText("No reading");
                }
                else {
                    averageTemp_TV.setText(sum_temp / count_temp_humid + "\u2103");
                    averageHumid_TV.setText(sum_humid / count_temp_humid + "%");
                }

                if (count_light == 0) {
                    averageLight_TV.setText("No reading");
                }
                else {
                    averageLight_TV.setText(sum_light / count_light + " lux");
                }
                number_devices_TV.setText(count_light + count_output + count_temp_humid +"");

                //Start background service to record device measure
                RecordMeasurementService mYourService = new RecordMeasurementService();
                Intent mServiceIntent = new Intent(this, mYourService.getClass());
                if (!isMyServiceRunning(mYourService.getClass())) {
                    startService(mServiceIntent);
                }

            }
            else{
                averageTemp_TV.setText("No reading");
                averageHumid_TV.setText("No reading");
                averageLight_TV.setText("No reading");
                number_devices_TV.setText(0 +"");
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
//                Log.i ("Service status", "Running");
                return true;
            }
        }
//        Log.i ("Service status", "Not running");
        return false;
    }
}
