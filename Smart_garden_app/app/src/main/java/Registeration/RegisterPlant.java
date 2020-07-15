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

import com.example.smartgarden.MainActivity;
import com.example.smartgarden.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Vector;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class RegisterPlant extends AppCompatActivity implements VolleyCallBack {
    private EditText buy_date_input;
    DatePickerDialog picker;
    private EditText plant_nameET;
    private EditText buy_locationET;
    private EditText amountET;
    private String linked_device_id = "";
    private String linked_device_name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_plant_setting);

        buy_date_input = findViewById(R.id.registerPlantBuyDateEditText);
        plant_nameET = findViewById(R.id.registerPlantNameEditText);
        buy_locationET = findViewById(R.id.registerPlantBuyLocationEditText);
        amountET = findViewById(R.id.registerPlantAmountEditText);
        Spinner linked_sensor_idSpinner = findViewById(R.id.registerPlantSensorID);
        final Spinner linked_sensor_nameSpinner = findViewById(R.id.registerPlantSensorName);
        Button submitBtn = findViewById(R.id.registerPlant_SubmitBtn);

        // Set up spinner
        final Vector<DeviceInformation> sensor = UserLoginManagement.getInstance(this).getSensor();
        final String[] sensor_id = new String[UserLoginManagement.getInstance(this).getSensor().size()];
        for(int i = 0; i < sensor_id.length; i++){
            sensor_id[i] = sensor.get(i).getDevice_id();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, sensor_id);
        linked_sensor_idSpinner.setAdapter(arrayAdapter);

        linked_sensor_idSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                linked_device_id = sensor_id[position];
                Vector<DeviceInformation> sensorWithID = Helper.Helper.findAllDeviceWithID(linked_device_id, sensor);
                final String[] sensor_name = new String[sensorWithID.size()];

                for(int i = 0; i < sensorWithID.size(); i++){
                    sensor_name[i] = sensorWithID.get(i).getDevice_name();
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item, sensor_name);
                linked_sensor_nameSpinner.setAdapter(arrayAdapter);
                linked_sensor_nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        linked_device_name = sensor_name[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                final String plant_name = plant_nameET.getText().toString();
                final String buy_date = buy_date_input.getText().toString();
                final String buy_location = buy_locationET.getText().toString();
                final String amount = amountET.getText().toString();
//                final String linked_sensor_id = linked_sensor_idSpinner.getText().toString();
                if(plant_name.equals("") || buy_date.equals("") || buy_location.equals("") || amount.equals("")
                        || linked_device_id.equals("") || linked_device_name.equals("")){
                    Toast.makeText(getApplicationContext(), "Empty field detected", Toast.LENGTH_LONG).show();
                    return;
                }
                Garden_Database_Control.addNewPlant(plant_name, buy_date, buy_location, amount, linked_device_id, linked_device_name,
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
        Intent showResult = new Intent(getApplicationContext(), RegisterMessageActivity.class);
        assert jsonObject != null;
        try {
            showResult.putExtra("register_type", "Add plant");
            showResult.putExtra("register_message", jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(showResult);
        //Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
    }
}
