package Login_RegisterUser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener, VolleyCallBack {
    public EditText usernameText;
    public TextView passwordText;
    public Button registerButton;
    public TextView goToLoginButton;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        usernameText = findViewById(R.id.registerUser_username_ET);
        passwordText = findViewById(R.id.registerUser_password_ET);
        registerButton = findViewById(R.id.regButton);
        goToLoginButton = findViewById(R.id.goToLoginTextView);

        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(this);
        goToLoginButton.setOnClickListener(this);
    }

    public void registerUser(){
        final String username = usernameText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();
        Garden_Database_Control.RegisterUser(username, password, this, this);
    }

    @Override
    public void onClick(View v) {
        if(v == registerButton){
            registerUser();
        }
        if(v == goToLoginButton){
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }
    }

    @Override
    public void onSuccessResponse(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            if(!jsonObject.getBoolean("error")) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
