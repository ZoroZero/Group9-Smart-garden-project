package app.advance.hcmut.cse.smartgardensystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DeviceSetting extends AppCompatActivity {
    Button btn_return;
    Button btn_summit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_device_properties);

        btn_summit = findViewById(R.id.btn_add_plant);
        btn_summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_add_plant){
                    summitChangeSetting();
                }
            }
        });

        btn_return = findViewById(R.id.btn_settingReturn);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_settingReturn){
                    returnDeviceTab();
                }
            }
        });
    }

    public void returnDeviceTab(){
        Intent intent = new Intent(this, DeviceTab.class);
        startActivity(intent);
    }

    public void summitChangeSetting(){
        Intent intent = new Intent(this, DeviceSettingMessage.class);
        startActivity(intent);
    }
}