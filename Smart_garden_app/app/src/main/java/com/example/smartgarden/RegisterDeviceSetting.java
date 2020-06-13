package app.advance.hcmut.cse.smartgardensystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RegisterDeviceSetting extends AppCompatActivity{
    Button btn_summit;
    Button btn_return;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_device_setting);

        btn_return = findViewById(R.id.register_return_button);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.register_return_button){
                    returnDeviceTab();
                }
            }
        });

        btn_summit = findViewById(R.id.submitSettingButton);
        btn_summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.submitSettingButton){
                    summitRegister();
                }
            }
        });
    }

    public void returnDeviceTab(){
        Intent intent = new Intent(this, DeviceTab.class);
        startActivity(intent);
    }

    public void summitRegister(){
        Intent intent = new Intent(this, RegisterDeviceMessage.class);
        startActivity(intent);
    }
}
