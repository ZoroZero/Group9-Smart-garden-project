package GardenManagement.PlantManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartgarden.R;

import org.json.JSONObject;

import java.util.Vector;

import Database.Garden_Database_Control;
import Helper.Constants;
import Helper.DeviceInformation;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class ChangePlantSettingActivity extends AppCompatActivity implements VolleyCallBack {
    private EditText newBuyLocationET;
    private EditText newAmountET;
    private Button submitBtn;
    private Button returnBtn;
    // Loading animation
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_plant_setting);
        // Components
        TextView plantNameTV = findViewById(R.id.plantName_TV);
        TextView plantDateTV = findViewById(R.id.plantDate_TV);
        TextView currentBuyLocationTV = findViewById(R.id.plantCurrBuyLocationTextVIew);
        TextView currentAmountTV = findViewById(R.id.plantCurrAmountTextVIew);
        newBuyLocationET = findViewById(R.id.changePlantSetting_BuyLocationEditText);
        newAmountET = findViewById(R.id.changePlantSetting_AmountEditText);
        progressBarHolder = findViewById(R.id.changePlantSetting_progressBarHolder);
        submitBtn = findViewById(R.id.changePlantSetting__SubmitBtn);
        returnBtn = findViewById(R.id.changePlantSetting__returnBtn);

        // Set text
        plantNameTV.setText(getIntent().getStringExtra("plant_detail.plant_name"));
        plantDateTV.setText(getIntent().getStringExtra("plant_detail.buy_date"));
        currentBuyLocationTV.setText(getIntent().getStringExtra("plant_detail.buy_location"));
        currentAmountTV.setText(getIntent().getStringExtra("plant_detail.amount"));
        newBuyLocationET.setText(getIntent().getStringExtra("plant_detail.buy_location"));
        newAmountET.setText(getIntent().getStringExtra("plant_detail.amount"));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePlantProperties();
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changePlantProperties(){

        final String new_buy_location = newBuyLocationET.getText().toString();
        final String new_amount = newAmountET.getText().toString();
        if(new_amount.equals("") || new_buy_location.equals("")){
            Toast.makeText(getApplicationContext(), "Empty required field", Toast.LENGTH_LONG).show();
            return;
        }
        else if(new_buy_location.length() >= 200){
            Toast.makeText(getApplicationContext(), "New buy location is too long", Toast.LENGTH_LONG).show();
            return;
        }
        else if(new_amount.equals(getIntent().getStringExtra("plant_detail.amount")) &&
                new_buy_location.equals(getIntent().getStringExtra("plant_detail.buy_location"))){
            Toast.makeText(getApplicationContext(), "No change needed to applied", Toast.LENGTH_LONG).show();
            return;
        }

        try{
            int thresholdCheck = Integer.parseInt(new_amount);
            if(thresholdCheck < Constants.MIN_PLANT_AMOUNT){
                Toast.makeText(getApplicationContext(), "Invalid amount", Toast.LENGTH_LONG).show();
                return;
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Invalid amount format", Toast.LENGTH_LONG).show();
            return;
        }
        startLoading();
        Garden_Database_Control.changePlantSetting(getIntent().getStringExtra("plant_detail.plant_name"),
                    getIntent().getStringExtra("plant_detail.buy_date"), new_buy_location, new_amount,
                     getApplicationContext(), ChangePlantSettingActivity.this);
    }

    @Override
    public void onSuccessResponse(String result) {
//        startLoading();
        JSONObject jsonObject;
        boolean go_back = false;
        try {
            jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("error")) {
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                go_back = true;
            }
            else{
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
            final boolean finalGo_back = go_back;
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    stopLoading();
                    if(finalGo_back) {
                        startActivity(new Intent(getApplicationContext(), PlantListView.class));
                        finish();
                    }
                }
            }.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startLoading(){
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        returnBtn.setClickable(false);
        submitBtn.setClickable(false);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void stopLoading(){
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }
}