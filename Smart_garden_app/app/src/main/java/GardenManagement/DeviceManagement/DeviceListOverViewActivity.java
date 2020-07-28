package GardenManagement.DeviceManagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import GardenManagement.PlantManagement.PlantListView;
import Helper.Constants;
import Report.ViewReport;
import com.example.smartgarden.R;

import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.HomeActivity;
import Login_RegisterUser.LoginActivity;
import Login_RegisterUser.UserLoginManagement;
import Registeration.RegisterDeviceSearchActivity;
import Registeration.RegisterPlant;

public class DeviceListOverViewActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_overview);

        IOT_Server_Access.connect(getApplicationContext());
        // Components
        GridView deviceListView = findViewById(R.id.DeviceList_DeviceList_View);
        TextView deviceCountTV = findViewById(R.id.DeviceOverView_DeviceCount_TV);
//       // Display devices info
//       Garden_Database_Control.FetchDevicesInfo(this, this);

        int[] deviceCount = {UserLoginManagement.getInstance(getApplicationContext()).getSensor().size(),
                UserLoginManagement.getInstance(getApplicationContext()).getOutput().size()};
        DeviceTypeItemAdapter itemAdapter = new DeviceTypeItemAdapter(getApplicationContext(), Constants.DEVICE_TYPE, deviceCount);
        deviceListView.setAdapter(itemAdapter);

        // Set device change tab when click
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDeviceListDetail =  new Intent(getApplicationContext(), DeviceListViewActivity.class);
                showDeviceListDetail.putExtra("device_list.type",
                        Constants.DEVICE_TYPE[position]);
                startActivity(showDeviceListDetail);
            }
        });
        deviceCountTV.setText(UserLoginManagement.getInstance(getApplicationContext()).getDevice_list().length + " devices");

        // Set back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
                startActivity(new Intent(this, HomeActivity.class));
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
                startActivity(new Intent(getApplicationContext(), ViewReport.class));
                finish();
                return true;
            case 6:
                return false;
            case 7:
                UserLoginManagement.getInstance(this).logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case android.R.id.home:
                finish();
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

//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onSuccessResponse(String result) {
//        try {
//            JSONObject jsonObject = new JSONObject(result);
//            if (!jsonObject.getBoolean("error")) {
//                JSONArray jsonArray = jsonObject.getJSONArray("list");
//                final String[] get_device_id = new String[jsonArray.length()];
//                final String[] get_device_name = new String[jsonArray.length()];
//                final String[] get_linked_device_id = new String[jsonArray.length()];
//                final String[] get_linked_device_name = new String[jsonArray.length()];
//                final String[] get_device_type = new String[jsonArray.length()];
//                final String[] get_threshold = new String[jsonArray.length()];
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject obj = jsonArray.getJSONObject(i);
//                    get_device_id[i] = obj.getString("device_id");
//                    get_device_name[i] = obj.getString("device_name");
//                    get_linked_device_id[i] = obj.getString("linked_device_id");
//                    get_linked_device_name[i] = obj.getString("linked_device_name");
//                    get_threshold[i] = obj.getString("threshold");
//                    if(Helper.stringContainsItemFromList(get_device_id[i], Constants.OUTPUT_ID))
//                        get_device_type[i] = "Output";
//                    else if (get_device_id[i].contains(Constants.LIGHT_SENSOR_ID))
//                        get_device_type[i] = "Light Sensor";
//                    else if (get_device_id[i].contains(Constants.TEMPHUMI_SENSOR_ID))
//                        get_device_type[i] = "TempHumi Sensor";
//
//                }
//                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, get_linked_device_id,
//                        get_linked_device_name, get_device_type, get_threshold);
//                int[] deviceCount = {UserLoginManagement.getInstance(getApplicationContext()).getSensor().size(),
//                        UserLoginManagement.getInstance(getApplicationContext()).getOutput().size()};
//                DeviceTypeItemAdapter itemAdapter = new DeviceTypeItemAdapter(getApplicationContext(), Constants.DEVICE_TYPE, deviceCount);
//                deviceListView.setAdapter(itemAdapter);
//
//                // Set device change tab when click
//                deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent showDeviceListDetail =  new Intent(getApplicationContext(), DeviceListViewActivity.class);
//                        showDeviceListDetail.putExtra("device_list.type",
//                                Constants.DEVICE_TYPE[position]);
//                        startActivity(showDeviceListDetail);
//                    }
//                });
//
//                deviceCountTV.setText(get_device_id.length + " devices");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
