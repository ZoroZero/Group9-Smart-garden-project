package app.advance.hcmut.cse.smartgardensystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DeviceSearch extends AppCompatActivity{
    Button btn_return;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_device_search);

        btn_return = findViewById(R.id.button_return);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button_return){
                    returnDeviceTab();
                }
            }
        });
    }

    public void returnDeviceTab(){
        Intent intent = new Intent(this, DeviceTab.class);
        startActivity(intent);
    }
}
