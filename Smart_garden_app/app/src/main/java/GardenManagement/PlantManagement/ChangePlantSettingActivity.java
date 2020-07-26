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
import Helper.DeviceInformation;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class ChangePlantSettingActivity extends AppCompatActivity implements VolleyCallBack {
    private String new_linked_sensor_id = "";
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
        TextView currentDeviceIDTV = findViewById(R.id.plantCurrDeviceIDTextVIew);
        Spinner linked_sensor_idSpinner = findViewById(R.id.changePlantSetting_SensorID);
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
        currentDeviceIDTV.setText(getIntent().getStringExtra("plant_detail.linked_sensor_id"));
        newBuyLocationET.setText(getIntent().getStringExtra("plant_detail.buy_location"));
        newAmountET.setText(getIntent().getStringExtra("plant_detail.amount"));

        // Set up spinner
        Vector<DeviceInformation> sensor = UserLoginManagement.getInstance(this).getSensor();
        final String[] sensor_id = new String[UserLoginManagement.getInstance(this).getSensor().size() + 1];
        for(int i = 0; i < sensor_id.length - 1; i++){
            sensor_id[i] = sensor.get(i).getDevice_id();
        }
        sensor_id[sensor_id.length - 1] = "None";
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, sensor_id);
        linked_sensor_idSpinner.setAdapter(arrayAdapter);

        linked_sensor_idSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_linked_sensor_id = sensor_id[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        startLoading();
        final String new_buy_location = newBuyLocationET.getText().toString();
        final String new_amount = newAmountET.getText().toString();
        if(new_amount.equals("") || new_buy_location.equals("") || new_linked_sensor_id.equals("")){
            Toast.makeText(getApplicationContext(), "Empty required field", Toast.LENGTH_LONG).show();
        }
        else if(new_buy_location.length() >= 200){
            Toast.makeText(getApplicationContext(), "New buy location is too long", Toast.LENGTH_LONG).show();
        }
        else if(new_amount.equals(getIntent().getStringExtra("plant_detail.amount")) &&
                new_buy_location.equals(getIntent().getStringExtra("plant_detail.buy_location"))&&
                new_linked_sensor_id.equals(getIntent().getStringExtra("plant_detail.linked_sensor_id"))){
            Toast.makeText(getApplicationContext(), "No change needed to applied", Toast.LENGTH_LONG).show();
        }
        else{
            Garden_Database_Control.changePlantSetting(getIntent().getStringExtra("plant_detail.plant_name"),
                    getIntent().getStringExtra("plant_detail.buy_date"), new_buy_location, new_amount,
                    new_linked_sensor_id, getApplicationContext(), ChangePlantSettingActivity.this);
        }
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