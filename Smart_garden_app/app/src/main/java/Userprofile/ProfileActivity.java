package Userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartgarden.MainActivity;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import Background_service.RecordMeasurementService;
import Database.Garden_Database_Control;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.LoginActivity;
import Login_RegisterUser.UserLoginManagement;
import Registeration.RegisterDeviceSearchActivity;
import Registeration.RegisterPlant;

public class ProfileActivity extends AppCompatActivity implements VolleyCallBack {
    private ListView deviceListView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if(!UserLoginManagement.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        IOT_Server_Access.connect(getApplicationContext());
        TextView usernameTextView = findViewById(R.id.PlantListViewTextView);
        deviceListView = findViewById(R.id.PlantList_PLantListView);
        usernameTextView.setText("Hello " + UserLoginManagement.getInstance(this).getUsername());

        //Display devices info
        Garden_Database_Control.FetchDevicesInfo(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.normal_menu, menu);
        menu.add(0, 1, 1,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_home_black_24dp),
                        getResources().getString(R.string.home)));

        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_list_black_24dp),
                        getResources().getString(R.string.view_plant_list)));

        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_add_black_24dp),
                        getResources().getString(R.string.register_device)));

        menu.add(0, 4, 4,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_local_florist_black_24dp),
                        getResources().getString(R.string.register_plant)));

        menu.add(0, 5, 5,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_view_report_black_24dp),
                        getResources().getString(R.string.view_report)));

        menu.add(0, 6, 6,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_settings_black_24dp),
                        getResources().getString(R.string.setting)));

        menu.add(0, 7, 7, menuIconWithText(getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp),
                getResources().getString(R.string.log_out)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            case 2:
                startActivity(new Intent(this, PlantListView.class));
                finish();
                return true;
            case 3:
                startActivity(new Intent(getApplicationContext(), RegisterDeviceSearchActivity.class));
                finish();
                return true;
            case 4:
                startActivity(new Intent(getApplicationContext(), RegisterPlant.class));
                finish();
                return true;
            case 5:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            case 6:
                return false;
            case 7:
                UserLoginManagement.getInstance(this).logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public void onSuccessResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("error")) {
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                String[] get_device_id = new String[jsonArray.length()];
                String[] get_device_name = new String[jsonArray.length()];
                String[] device_topic = new String[jsonArray.length()];
                String[] linked_device_id = new String[jsonArray.length()];
                String[] linked_device_name = new String[jsonArray.length()];
                String[] linked_device_topic = new String[jsonArray.length()];
                String[] device_type = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    get_device_id[i] = obj.getString("device_id");
                    get_device_name[i] = obj.getString("device_name");
                    device_topic[i] = get_device_name[i] +"/" + get_device_id[i];
                    linked_device_id[i] = obj.getString("linked_device_id");
                    linked_device_name[i] = obj.getString("linked_device_name");
                    linked_device_topic[i] = linked_device_name[i] + "/" + linked_device_id[i];
                    if(get_device_id[i].contains("ld"))
                        device_type[i] = "output";
                    else
                        device_type[i] = "sensor";
                }
                DeviceDetailAdapter itemAdapter = new DeviceDetailAdapter(getApplicationContext(), device_topic, device_type);
                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, linked_device_id, linked_device_name, device_type);
                deviceListView.setAdapter(itemAdapter);

                // Start background service to record device measure
                RecordMeasurementService mYourService = new RecordMeasurementService();
                Intent mServiceIntent = new Intent(this, mYourService.getClass());
                if (!isMyServiceRunning(mYourService.getClass())) {
                    startService(mServiceIntent);
                }
            }
        } catch (Exception e) {
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
