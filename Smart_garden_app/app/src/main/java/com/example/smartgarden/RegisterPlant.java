package app.advance.hcmut.cse.smartgardensystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RegisterPlant extends AppCompatActivity {
    Button btn_summit;
    Button btn_return;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_plant_setting);

        btn_return = findViewById(R.id.btn_plant_return);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_plant_return){
                    returnDeviceTab();
                }
            }
        });

        btn_summit = findViewById(R.id.btn_add_plant);
        btn_summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_add_plant){
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
        Intent intent = new Intent(this, RegisterPlantMessage.class);
        startActivity(intent);
    }
}
