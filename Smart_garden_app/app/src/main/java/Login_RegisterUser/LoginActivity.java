package Login_RegisterUser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartgarden.R;

import Database.Garden_Database_Control;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText usernameText, passwordText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        if (UserLoginManagement.getInstance(this).isLoggedIn()) {
////            //finish();
////            //startActivity(new Intent(this, ProfileActivity.class));
////        }
        usernameText = (EditText) findViewById(R.id.loginUsernameEditText);
        passwordText = (EditText) findViewById(R.id.loginPasswordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Please wait ...");

        loginButton.setOnClickListener(this);
    }


    public void login() {
        final String username = usernameText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();
        Garden_Database_Control.Login(username, password, this);
//        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//            finish();
//        }
    }

    @Override
    public void onClick(View v) {
        if(v == loginButton){
            login();
        }
    }
}
