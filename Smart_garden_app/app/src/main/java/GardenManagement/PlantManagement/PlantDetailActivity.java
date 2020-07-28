package GardenManagement.PlantManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import Helper.Constants;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Helper.Helper;
import Helper.VolleyCallBack;
import Login_RegisterUser.HomeActivity;
import Login_RegisterUser.UserLoginManagement;

public class PlantDetailActivity extends AppCompatActivity implements VolleyCallBack {

    TextView averageTemp_TV;
    TextView averageHumid_TV;
    TextView averageLight_TV;
    TextView planted_day_TV;
    // Loading animation
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = calendar.getTime();

//        IOT_Server_Access.connect(this);
        // Components
        averageTemp_TV = findViewById(R.id.Home_DeviceLastReading_TV);
        averageHumid_TV = findViewById(R.id.Home_DeviceLastReading1_TV);
        averageLight_TV = findViewById(R.id.Home_DeviceLastReading2_TV);
        planted_day_TV = findViewById(R.id.Home_DeviceLastReading3_TV);
        TextView planted_type_TV = findViewById(R.id.Home_readingType3_TV);

        TextView plant_nameTV = findViewById(R.id.Detail_Plantname_TV);
        TextView buy_dateTV = findViewById(R.id.Detail_BuyDate_TV);
        TextView buy_locationTV = findViewById(R.id.Detail_BuyLocation_TV);
        TextView amountTV = findViewById(R.id.Detail_Amount_TV);

        progressBarHolder = findViewById(R.id.progressBarHolder);
        Button removePlant_Btn = findViewById(R.id.PlantDetail_RemovePlant_Btn);
        Button changePlantSetting_Btn = findViewById(R.id.PlantDetail_ChangeSettingPlant_Btn);

        // Set text
        plant_nameTV.setText(getIntent().getStringExtra("plant_detail.plant_name"));
        buy_dateTV.setText(getIntent().getStringExtra("plant_detail.buy_date"));
        buy_locationTV.setText(getIntent().getStringExtra("plant_detail.buy_location"));
        amountTV.setText(getIntent().getStringExtra("plant_detail.amount"));

        // Get planted day
        Date plantedDay = null;
        try {
            plantedDay = dateFormat.parse(Objects.requireNonNull(getIntent().getStringExtra("plant_detail.buy_date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert plantedDay != null;
        long difference = today.getTime() - plantedDay.getTime();
        long differenceDates = Math.abs(difference) / (24 * 60 * 60 * 1000);
        String dayDifference = Long.toString(differenceDates);
        // Set texts
        if(difference < 0){
            planted_day_TV.setText(dayDifference);
            planted_type_TV.setText("till plant");
        }
        else {
            planted_day_TV.setText(dayDifference);
        }

        // Get reading
        Garden_Database_Control.FetchDevicesInfo(this, this);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Get reading
                        Garden_Database_Control.FetchDevicesInfo(getApplicationContext(), PlantDetailActivity.this);
                        handler.postDelayed(this, 2000); // set time here to refresh textView
                    }
        });

        removePlant_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ask user permission
               askForPermission(PlantDetailActivity.this);
            }
        });

        changePlantSetting_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeSetting = new Intent(getApplicationContext(), ChangePlantSettingActivity.class);
                changeSetting.putExtra("plant_detail.plant_name", getIntent().getStringExtra("plant_detail.plant_name"));
                changeSetting.putExtra("plant_detail.buy_date", getIntent().getStringExtra("plant_detail.buy_date"));
                changeSetting.putExtra("plant_detail.buy_location", getIntent().getStringExtra("plant_detail.buy_location"));
                changeSetting.putExtra("plant_detail.amount", getIntent().getStringExtra("plant_detail.amount"));
                startActivity(changeSetting);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("error")) {
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                final String[] get_device_id = new String[jsonArray.length()];
                final String[] get_device_name = new String[jsonArray.length()];
                final String[] get_linked_device_id = new String[jsonArray.length()];
                final String[] get_linked_device_name = new String[jsonArray.length()];
                final String[] get_device_type = new String[jsonArray.length()];
                final String[] get_threshold = new String[jsonArray.length()];
                final String[] get_status = new String[jsonArray.length()];
                final String[] get_status_date = new String[jsonArray.length()];
                float sum_temp = 0;
                float sum_humid = 0;
                float sum_light = 0;
                int count_temp_humid = 0;
                int count_light = 0;
                int count_output = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    get_device_id[i] = obj.getString("device_id");
                    get_device_name[i] = obj.getString("device_name");
                    get_linked_device_id[i] = obj.getString("linked_device_id");
                    get_linked_device_name[i] = obj.getString("linked_device_name");
                    get_threshold[i] = obj.getString("threshold");
                    get_status[i] = obj.getString("status");
                    get_status_date[i] = obj.getString("date");
                    if (obj.getString("status").equals("null")) {
                        get_status[i] = "No record";
                        get_status_date[i] = "No record";
                    }
                    // Get device type and summarize
                    if (Helper.stringContainsItemFromList(get_device_id[i], Constants.OUTPUT_ID)) {
                        get_device_type[i] = Constants.OUTPUT_TYPE;
                        if (!get_status[i].equals("Off")) {
                            count_output += 1;
                        }
                    } else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.LIGHT_SENSOR_ID)) {
                        get_device_type[i] = Constants.LIGHT_SENSOR_TYPE;
                        if (!get_status[i].equals("No record")) {
                            count_light += 1;
                            sum_light += Integer.parseInt(get_status[i]);
                        }
                    } else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.TEMPHUMI_SENSOR_ID)) {
                        get_device_type[i] = Constants.TEMPHUMI_SENSOR_TYPE;
                        if (!get_status[i].equals("No record")) {
                            count_temp_humid += 1;
//                            Log.i("Status", get_status[i]);
                            sum_temp += Integer.parseInt(get_status[i].split(":")[0]);
                            sum_humid += Integer.parseInt(get_status[i].split(":")[1]);
                        }
                    }
                }
                UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, get_linked_device_id,
                        get_linked_device_name, get_device_type, get_threshold, get_status, get_status_date);
                // Summarize reading for user
                if (count_temp_humid == 0) {
                    averageTemp_TV.setText("No reading");
                    averageHumid_TV.setText("No reading");
                } else {
                    averageTemp_TV.setText(sum_temp / count_temp_humid + "\u2103");
                    averageHumid_TV.setText(sum_humid / count_temp_humid + "%");
                }

                if (count_light == 0) {
                    averageLight_TV.setText("No reading");
                } else {
                    averageLight_TV.setText(sum_light / count_light + " lux");
                }
            }
            else{
                averageTemp_TV.setText("No reading");
                averageHumid_TV.setText("No reading");
                averageLight_TV.setText("No reading");
            }
        }catch (Exception e){
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
                                else{
                                    Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
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
