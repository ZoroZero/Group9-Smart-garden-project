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

import Helper.Constants;
import com.example.smartgarden.R;

import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class DeviceSettingActivity extends AppCompatActivity implements VolleyCallBack {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        DeviceInformation deviceInformation;
        final String device_type = getIntent().getStringExtra("device_setting.device_type");
        assert device_type != null;
        if(device_type.equals(Constants.OUTPUT_TYPE)){
            deviceInformation = Helper.Helper.findDeviceWithDeviceId(getIntent().getStringExtra("device_setting.device_id"),
                    UserLoginManagement.getInstance(this).getOutput());
        }
        else{
            deviceInformation = Helper.Helper.findDeviceWithDeviceId(getIntent().getStringExtra("device_setting.device_id"),
                    UserLoginManagement.getInstance(this).getSensor());
        }
        TextView deviceId = findViewById(R.id.changeSetting_deviceId_TV);
        TextView deviceType = findViewById(R.id.changeSetting_deviceType_TV);
//        ConstraintLayout threshold1 = findViewById(R.id.changeSetting_threshold1);
        ConstraintLayout current_threshold2 = findViewById(R.id.changeSetting_current_threshold2);
        TextView currentThresholdType1 = findViewById(R.id.changeSetting_ThresHoldType1_TV);
        TextView currentThresholdType2 = findViewById(R.id.changeSetting_ThresHoldType2_TV);
        TextView currentThreshold1 = findViewById(R.id.changeSetting_deviceThreshold1_TV);
        TextView currentThreshold2 = findViewById(R.id.changeSetting_deviceThreshold2_TV);
        ConstraintLayout threshold2 = findViewById(R.id.changeSetting_threshold2);
        TextView new_threshold_Type1 = findViewById(R.id.changeSetting_newThresholdType1);
        TextView new_threshold_Type2 = findViewById(R.id.changeSetting_newThresholdType2);
        final EditText threshold_Input1 = findViewById(R.id.changeSetting_thresholdInput1);
        final EditText threshold_Input2 = findViewById(R.id.changeSetting_thresholdInput2);

        Button submitBtn = findViewById(R.id.changeSetting_submitBtn);
        // Set text
        assert deviceInformation != null;
        deviceId.setText(deviceInformation.getDevice_id());
        deviceType.setText(deviceInformation.getDevice_type());

        if(device_type.contains("Light")){
            threshold2.setVisibility(View.GONE);
            current_threshold2.setVisibility(View.GONE);
            new_threshold_Type1.setText("New preferred light intensity:");
            currentThresholdType1.setText("Light intensity threshold: ");
            currentThreshold1.setText(deviceInformation.getThreshold() + "%");
        }
        else{
            String[] thresholds = deviceInformation.getThreshold().split(":");
            new_threshold_Type1.setText("New preferred temperature(\u2103):");
            new_threshold_Type2.setText("New preferred humidity (%):");
            currentThresholdType1.setText("Temperature threshold: ");
            currentThreshold1.setText(thresholds[0] + "\u2103");
            currentThresholdType2.setText("Humidity threshold: ");
            currentThreshold2.setText(thresholds[1] + "%");
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