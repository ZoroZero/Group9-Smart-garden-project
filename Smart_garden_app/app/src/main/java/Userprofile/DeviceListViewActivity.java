package Userprofile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.MainActivity;
import com.example.smartgarden.R;

import java.util.Objects;
import java.util.Vector;

import Helper.DeviceInformation;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.HomeActivity;
import Login_RegisterUser.LoginActivity;
import Login_RegisterUser.UserLoginManagement;
import Registeration.RegisterDeviceSearchActivity;
import Registeration.RegisterPlant;
public class DeviceListViewActivity extends AppCompatActivity {

    private Vector<DeviceInformation> deviceInformationVector;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_view);

        // Components
        ListView deviceListView = findViewById(R.id.DeviceList_ListView);
        ImageView icon = findViewById(R.id.DeviceList_Icon);
        TextView deviceListTypeTv = findViewById(R.id.DeviceListDetail_DeviceType_TV);
        TextView deviceListCountTV = findViewById(R.id.DeviceListDetail_DeviceCount_TV);

        // Get device list
        if(Objects.equals(getIntent().getStringExtra("device_list.type"), "sensor")){
            deviceListTypeTv.setText("Sensor: ");
            deviceInformationVector = UserLoginManagement.getInstance(getApplicationContext()).getSensor();
            icon.setImageResource(R.drawable.sensor_icon_50dp);
        }
        else{
            deviceListTypeTv.setText("Output: ");
            deviceInformationVector = UserLoginManagement.getInstance(getApplicationContext()).getOutput();
            icon.setImageResource(R.drawable.light_iot_icon_50);
        }

        deviceListCountTV.setText(deviceInformationVector.size() + " devices");
        IOT_Server_Access.connect(getApplicationContext());

        // Set back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DeviceDetailAdapter deviceDetails = new DeviceDetailAdapter(getApplicationContext(), deviceInformationVector);
        deviceListView.setAdapter(deviceDetails);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDeviceListDetail;
                if(Objects.equals(getIntent().getStringExtra("device_list.type"), "sensor")){
                    showDeviceListDetail =  new Intent(getApplicationContext(), DeviceDetailActivity.class);
                }
                else{
                    showDeviceListDetail = new Intent(getApplicationContext(), OutputDetailActivity.class);
                }

                showDeviceListDetail.putExtra("device_detail.device_id",
                        deviceInformationVector.elementAt(position).getDevice_id());
                showDeviceListDetail.putExtra("device_detail.device_name",
                        deviceInformationVector.elementAt(position).getDevice_name());
                showDeviceListDetail.putExtra("device_detail.device_type",
                        deviceInformationVector.elementAt(position).getDevice_type());
                showDeviceListDetail.putExtra("device_detail.device_threshold",
                        deviceInformationVector.elementAt(position).getThreshold());
                startActivity(showDeviceListDetail);
            }
        });
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

}