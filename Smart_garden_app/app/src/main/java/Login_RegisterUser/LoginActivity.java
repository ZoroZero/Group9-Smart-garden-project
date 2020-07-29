package Login_RegisterUser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartgarden.R;

import Database.Garden_Database_Control;
import GardenManagement.PlantManagement.PlantListView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText usernameText, passwordText;
    private Button loginButton;
    private TextView goToRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (UserLoginManagement.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }
        usernameText = findViewById(R.id.loginUsernameEditText);
        passwordText = findViewById(R.id.loginPasswordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegister = findViewById(R.id.goToRegisterTextView);
        ProgressDialog progressDialog = new ProgressDialog(this);


        progressDialog.setMessage("Please wait ...");

        loginButton.setOnClickListener(this);
        goToRegister.setOnClickListener(this);
    }

    public void login() {
        final String username = usernameText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();
        if(username.equals("") || password.length() == 0){
            Toast.makeText(getApplicationContext(), "Empty required field", Toast.LENGTH_SHORT).show();
            return;
        }
        Garden_Database_Control.Login(username, password, this);
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if (UserLoginManagement.getInstance(getApplicationContext()).isLoggedIn()) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        if(v == loginButton){
            login();
        }
        else if(v == goToRegister){
            startActivity(new Intent(getApplicationContext(), RegisterUserActivity.class));
        }
    }
}
