package Userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;
import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.LoginActivity;
import Login_RegisterUser.UserLoginManagement;
import Registeration.RegisterDeviceSearchActivity;
import Registeration.RegisterPlant;

public class ProfileActivity extends AppCompatActivity implements VolleyCallBack {
    private ListView deviceListView;
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
        TextView usernameTextView = findViewById(R.id.UsernameTextView);
        deviceListView = findViewById(R.id.deviceListView);
        usernameTextView.setText("Hello " + UserLoginManagement.getInstance(this).getUsername());

        //Display devices info
        Garden_Database_Control.FetchDevicesInfo(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.normal_menu, menu);
        menu.add(0, 1, 1,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp),
                        getResources().getString(R.string.log_out)));

        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_add_black_24dp),
                        getResources().getString(R.string.register_device)));

        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_local_florist_black_24dp),
                        getResources().getString(R.string.register_plant)));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                UserLoginManagement.getInstance(this).logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case 2:
                startActivity(new Intent(getApplicationContext(), RegisterDeviceSearchActivity.class));
                finish();
                return true;
            case 3:
                startActivity(new Intent(getApplicationContext(), RegisterPlant.class));
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

    @Override
    public void onSuccessResponse(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.getBoolean("error")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    String[] get_device_id = new String[jsonArray.length()];
                    String[] get_device_name = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        get_device_id[i] = obj.getString("device_id") ;
                        get_device_name[i] = obj.getString("device_name");
                    }
                    DeviceDetailAdapter deviceDetailAdapter = new DeviceDetailAdapter(getApplicationContext(), get_device_id, get_device_name);
                    deviceListView.setAdapter(deviceDetailAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
