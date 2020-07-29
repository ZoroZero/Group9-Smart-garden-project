package Registeration;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Vector;

import Database.Garden_Database_Control;
import Helper.Constants;
import Helper.DeviceInformation;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class RegisterPlant extends AppCompatActivity implements VolleyCallBack {
    private EditText buy_date_input;
    DatePickerDialog picker;
    private EditText plant_nameET;
    private EditText buy_locationET;
    private EditText amountET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_plant_setting);

        buy_date_input = findViewById(R.id.registerPlantBuyDateEditText);
        plant_nameET = findViewById(R.id.registerPlantNameEditText);
        buy_locationET = findViewById(R.id.registerPlantBuyLocationEditText);
        amountET = findViewById(R.id.registerPlantAmountEditText);
        Button submitBtn = findViewById(R.id.registerPlant_SubmitBtn);

        buy_date_input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegisterPlant.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                buy_date_input.setText(year + "-" +  (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPlant();
            }
        });
    }

    @Override
    public void onSuccessResponse(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent showResult = new Intent(getApplicationContext(), RegisterMessageActivity.class);
        assert jsonObject != null;
        try {
            showResult.putExtra("register_type", "Add new plant");
            showResult.putExtra("register_message", jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(showResult);
        //Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
    }

    private void registerPlant(){
        final String plant_name = plant_nameET.getText().toString();
        final String buy_date = buy_date_input.getText().toString();
        final String buy_location = buy_locationET.getText().toString();
        final String amount = amountET.getText().toString();
//                final String linked_sensor_id = linked_sensor_idSpinner.getText().toString();
        if(plant_name.equals("") || buy_date.equals("") || buy_location.equals("") || amount.equals("")){
            Toast.makeText(getApplicationContext(), "Empty field detected", Toast.LENGTH_LONG).show();
            return;
        }
        try{
            int thresholdCheck = Integer.parseInt(amount);
            if(thresholdCheck < Constants.MIN_PLANT_AMOUNT){
                Toast.makeText(getApplicationContext(), "Invalid amount", Toast.LENGTH_LONG).show();
                return;
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Invalid amount format", Toast.LENGTH_LONG).show();
            return;
        }
        if(plant_name.length() >= 90 || buy_location.length() > 200){
            Toast.makeText(getApplicationContext(), "Overflow field detected", Toast.LENGTH_LONG).show();
            return;
        }
        Garden_Database_Control.addNewPlant(plant_name, buy_date, buy_location, amount,
                getApplicationContext(), RegisterPlant.this);
    }
}
