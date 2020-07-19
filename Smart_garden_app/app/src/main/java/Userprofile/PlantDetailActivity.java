package Userprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import Helper.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Helper.Helper;
import Helper.VolleyCallBack;
import Login_RegisterUser.HomeActivity;
import Login_RegisterUser.UserLoginManagement;

public class PlantDetailActivity extends AppCompatActivity implements VolleyCallBack {
    private TextView readingTimeTV;
    private TextView device_lastReadingTV;
    private TextView device_lastReading1TV;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar;
    private pl.pawelkleczkowski.customgauge.CustomGauge readingBar1;

    // Loading animation
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

//        IOT_Server_Access.connect(this);
        // Components

        TextView plant_nameTV = findViewById(R.id.Detail_Plantname_TV);
        TextView buy_dateTV = findViewById(R.id.Detail_BuyDate_TV);
        TextView buy_locationTV = findViewById(R.id.Detail_BuyLocation_TV);
        TextView amountTV = findViewById(R.id.Detail_Amount_TV);
        readingTimeTV= findViewById(R.id.Detail_LastReadingTime_TV);
        device_lastReadingTV = findViewById(R.id.PlantDetail_DeviceLastReading_TV);
        TextView device_readingTypeTV = findViewById(R.id.PlantDetail_readingType_TV);
        device_lastReading1TV = findViewById(R.id.PlantDetail_DeviceLastReading1_TV);
        TextView device_readingType1TV = findViewById(R.id.PlantDetail_readingType1_TV);

        ImageView readingTypeIcon = findViewById(R.id.PlantDetail_readingTypeIcon_TV);
        ImageView readingTypeIcon1 = findViewById(R.id.PlantDetail_readingTypeIcon1_TV);

        readingBar = findViewById(R.id.PlantDetail_DeviceLastReading);
        readingBar1 = findViewById(R.id.PlantDetail_DeviceLastReading1);
        ConstraintLayout readingLayout = findViewById(R.id.PlantDetail_reading);
        progressBarHolder = findViewById(R.id.progressBarHolder);
        Button removePlant_Btn = findViewById(R.id.PlantDetail_RemovePlant_Btn);

        // Set text
        plant_nameTV.setText(getIntent().getStringExtra("plant_detail.plant_name"));
        buy_dateTV.setText(getIntent().getStringExtra("plant_detail.buy_date"));
        buy_locationTV.setText(getIntent().getStringExtra("plant_detail.buy_location"));
        amountTV.setText(getIntent().getStringExtra("plant_detail.amount"));

        // Get linked device
        DeviceInformation sensorInfo = Helper.findDeviceWithDeviceId(getIntent().getStringExtra("plant_detail.linked_sensor_id"),
                UserLoginManagement.getInstance(this).getSensor());

        assert sensorInfo != null;
        if(sensorInfo.getDevice_type().equals(Constants.LIGHT_SENSOR_TYPE)){
            readingLayout.setVisibility(View.GONE);
            device_readingType1TV.setText("Light intensity");
            readingTypeIcon1.setImageResource(R.drawable.ic_light_30);
            readingBar1.setEndValue(Constants.MAX_LIGHT);
        }
        else if(sensorInfo.getDevice_type().equals(Constants.TEMPHUMI_SENSOR_TYPE)){
            device_readingTypeTV.setText("Humidity");
            device_readingType1TV.setText("Temperature");
            readingTypeIcon.setImageResource(R.drawable.ic_humidity_30);
            readingTypeIcon1.setImageResource(R.drawable.ic_temphumi_sensor_icon_black);
            readingBar.setEndValue(Constants.MAX_HUMID);
            readingBar1.setEndValue(Constants.MAX_TEMP);
        }


        // Get reading
        Garden_Database_Control.getDeviceLastReading(getIntent().getStringExtra("plant_detail.linked_sensor_id"),
                this, this);

        removePlant_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get prompts.xml view
               askForPermission(PlantDetailActivity.this);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessResponse(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            if(!jsonObject.getBoolean("error")){
                if(jsonObject.has("reading")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("reading");
                    final JSONObject reading  = jsonArray.getJSONObject(0);
                    //Log.i("JSON object", String.valueOf(reading));
                    String type = reading.getString("type");
                    readingTimeTV.setText(reading.getString("date"));
                    if(type.equals(Constants.TEMPHUMI_SENSOR_TYPE)) {
                        String[] measurements = reading.getString("measurement").split(":");
                        device_lastReadingTV.setText(measurements[1] + "%");
                        readingBar.setValue(Math.min(Integer.parseInt(measurements[1]), readingBar.getEndValue()));
                        device_lastReading1TV.setText(measurements[0] + "\u2103");
                        readingBar1.setValue(Math.min(Integer.parseInt(measurements[0]), readingBar1.getEndValue()));
                    }
                    else{
                        String measurement = reading.getString("measurement");
                        device_lastReading1TV.setText(measurement + " lux");
                        readingBar1.setValue(Math.min(Integer.parseInt(measurement), readingBar1.getEndValue()));
                    }
                }
                else{
                    device_lastReadingTV.setText("No record");
                    device_lastReading1TV.setText("No record");
                    readingTimeTV.setText("No record");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void askForPermission(final Context context){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.yes_no_user_opinion_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alert dialog builder
        alertDialogBuilder.setView(promptsView);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                askForPassword(context);
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void askForPassword(final Context context){
        // get layout
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.ask_for_password_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alert dialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String password = Helper.md5(userInput.getText().toString());

                                if(password.equals(UserLoginManagement.getInstance(context).getUserEncryptedPassword())){
                                    Garden_Database_Control.removePlant(getIntent().getStringExtra("plant_detail.plant_name"),
                                            getIntent().getStringExtra("plant_detail.buy_date"),
                                            getIntent().getStringExtra("plant_detail.buy_location"),
                                            PlantDetailActivity.this);
                                    // Start loading animation

                                    startLoading();
                                    new CountDownTimer(3000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                        }
                                        public void onFinish() {
                                            stopLoading();
                                            startActivity(new Intent(context, HomeActivity.class));
                                            finish();
                                        }
                                    }.start();

                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void startLoading(){
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
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
