package Registeration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.R;

import Userprofile.ProfileActivity;

public class RegisterMessageActivity extends AppCompatActivity {
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
    }
}
