package Userprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartgarden.R;

import IOT_Server.IOT_Server_Access;
import Login_RegisterUser.LoginActivity;
import Login_RegisterUser.UserLoginManagement;
import Registeration.RegisterDeviceSearchActivity;

public class ProfileActivity extends AppCompatActivity {
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

        Button goToRegDevice = findViewById(R.id.goToRegisterDevice);
        goToRegDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterDeviceSearchActivity.class));
            }
        });
        //DatabaseHelper.FetchDevicesInfo(this, this);
    }
}
