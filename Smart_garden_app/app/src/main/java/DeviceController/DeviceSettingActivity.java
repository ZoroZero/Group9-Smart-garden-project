package DeviceController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartgarden.R;

import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;

public class DeviceSettingActivity extends AppCompatActivity implements VolleyCallBack {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);

        final String device_type = getIntent().getStringExtra("device_setting.device_type");
        TextView deviceId = findViewById(R.id.changeSetting_deviceId_TV);
        TextView deviceType = findViewById(R.id.changeSetting_deviceType_TV);
//        ConstraintLayout threshold1 = findViewById(R.id.changeSetting_threshold1);
        ConstraintLayout threshold2 = findViewById(R.id.changeSetting_threshold2);
        TextView threshold_Type1 = findViewById(R.id.changeSetting_thresholdType1);
        TextView threshold_Type2 = findViewById(R.id.changeSetting_thresholdType2);
        final EditText threshold_Input1 = findViewById(R.id.changeSetting_thresholdInput1);
        final EditText threshold_Input2 = findViewById(R.id.changeSetting_thresholdInput2);
        Button submitBtn = findViewById(R.id.changeSetting_submitBtn);
        // Set text
        deviceId.setText(getIntent().getStringExtra("device_setting.device_id"));
        deviceType.setText(getIntent().getStringExtra("device_setting.device_type"));
        assert device_type != null;
        if(device_type.contains("Light")){
            threshold2.setVisibility(View.GONE);
            threshold_Type1.setText("New preferred light intensity:");
        }
        else{
            threshold_Type1.setText("New preferred temperature(\u2103):");
            threshold_Type2.setText("New preferred humidity (%):");
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(device_type.contains("Light")){
                    final String threshold = threshold_Input1.getText().toString();
                    Garden_Database_Control.changeDeviceThreshold(getIntent().getStringExtra("device_setting.device_id"),
                            threshold, getApplicationContext(), DeviceSettingActivity.this);
                }
                else{
                    final String temp_threshold = threshold_Input1.getText().toString();
                    final String humid_threshold = threshold_Input2.getText().toString();
                    final String threshold = temp_threshold + ":" + humid_threshold;
                    Garden_Database_Control.changeDeviceThreshold(getIntent().getStringExtra("device_setting.device_id"),
                            threshold, getApplicationContext(), DeviceSettingActivity.this);
                }
            }
        });
    }

    @Override
    public void onSuccessResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}