package Registeration;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.MainActivity;
import com.example.smartgarden.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;

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
                final String plant_name = plant_nameET.getText().toString();
                final String buy_date = buy_date_input.getText().toString();
                final String buy_location = buy_locationET.getText().toString();
                final String amount = amountET.getText().toString();

                if(plant_name.equals("") || buy_date.equals("") || buy_location.equals("") || amount.equals("")){
                    Toast.makeText(getApplicationContext(), "Empty field detected", Toast.LENGTH_LONG).show();
                    return;
                }
                Garden_Database_Control.addNewPlant(plant_name, buy_date, buy_location, amount,
                        getApplicationContext(), RegisterPlant.this);
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
        Intent showResult = new Intent(getApplicationContext(), MainActivity.class);
        assert jsonObject != null;
        try {
            showResult.putExtra("register_message", jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(showResult);
        //Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
    }
}
