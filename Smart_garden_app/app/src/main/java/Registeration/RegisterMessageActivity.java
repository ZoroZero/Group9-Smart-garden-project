package Registeration;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;
import Userprofile.DeviceDetailAdapter;
import Userprofile.ProfileActivity;

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
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        //Update information if success
        if(Objects.requireNonNull(getIntent().getStringExtra("register_message")).contains("Sucessfully")){
            Garden_Database_Control.FetchDevicesInfo(this, this);
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
                    if (get_device_id[i].contains(Constants.OUTPUT_ID))
                        device_type[i] = "Output";
                    else if (get_device_id[i].contains(Constants.LIGHT_SENSOR_ID))
                        device_type[i] = "Light Sensor";
                    else if (get_device_id[i].contains(Constants.TEMPHUMI_SENSOR_ID))
                        device_type[i] = "TempHumi Sensor";
                }
                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, linked_device_id, linked_device_name, device_type);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
