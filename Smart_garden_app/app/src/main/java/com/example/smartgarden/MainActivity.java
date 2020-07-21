package com.example.smartgarden;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;



import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import android.view.View;


import android.widget.Spinner;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import Background_service.RecordMeasurementService;
import Database.Garden_Database_Control;
import Helper.Constants;
import Helper.Helper;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;


public class MainActivity extends AppCompatActivity implements VolleyCallBack {
    GraphView graphTemperature,graphHumidity,graphLightLevel;
    TextView  textTemperature,textHumidity,textLightLevel;
    //Constant for device type
    protected final static String TEMP_HUMIDITY = Constants.TEMPHUMI_SENSOR_TYPE;
    protected final static String TEMP = "T";
    protected final static String HUMIDITY = "H";
    protected final static String LIGHT = Constants.LIGHT_SENSOR_TYPE;

    //Constant for value threshold
    protected final static int MAX_TEMP = 50;
    protected final static int MAX_HUMIDITY = 100;
    protected final static int MAX_LIGHT = 500;

    //Constant for mode
    protected final static String VALUE_MODE = "Value";
    protected final static String HOURLY_MODE = "Hourly";
    protected final static String DAILY_MODE = "Daily";
    protected final static String MONTHLY_MODE = "Monthly";

    //Schedule for AI timer
    protected final static int DELAY = 20;
    protected final static int PERIOD = 5000;

    //Unit
    protected final static String TEMP_UNIT = "\u2103";
    protected final static String HUMIDITY_UNIT = "%";
    protected final static String LIGHT_UNIT = "lux";

    //Title size
    protected final static float TITLE_SIZE = 70f;

    //User ID from teammate part
    protected String user_id = "10";

    //Overview text view
    TextView averageTemp_TV;
    TextView averageHumid_TV;
    TextView averageLight_TV;
    TextView number_devices_TV;

    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphTemperature = findViewById(R.id.graphTemperature);
        graphHumidity = findViewById(R.id.graphHumidLevel);
        graphLightLevel = findViewById(R.id.graphLightLevel);

        textTemperature = findViewById(R.id.temp_view);
        textHumidity = findViewById(R.id.humid_view);
        textLightLevel = findViewById(R.id.light_view);

        makeTempDeviceSpinner(TEMP_HUMIDITY);
        makeHumidDeviceSpinner(TEMP_HUMIDITY);
        makeLightDeviceSpinner(LIGHT);


        averageTemp_TV = findViewById(R.id.Report_DeviceLastReading_TV);
        averageHumid_TV = findViewById(R.id.Report_DeviceLastReading1_TV);
        averageLight_TV = findViewById(R.id.Report_DeviceLastReading2_TV);
        number_devices_TV = findViewById(R.id.Report_DeviceLastReading3_TV);

        // Get device info
        Garden_Database_Control.FetchDevicesInfo(this, this);

    }

    private void setupAITimer(final String temp_device_id, final String type){
        Timer mTimer = new Timer();
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                sendDatatoAI(temp_device_id,type);
            }
        };
        mTimer.schedule(mTask, DELAY, PERIOD);

    }

    private void makeTempDeviceSpinner(final String type)
    {
        GetDeviceByType getDeviceByType = new GetDeviceByType(user_id,type);
        Thread thread = new Thread(getDeviceByType);
        thread.start();
        Vector<String> results = getDeviceByType.results;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //get the spinner from the xml.
        final Spinner dropdown = findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, results);
        final int num_of_choices = adapter.getCount();
        dropdown.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                final RadioGroup radioGroup = findViewById(R.id.radio_temp);
                RadioButton rad = (RadioButton) findViewById(R.id.radio_temp1);
                if(rad.isChecked()){
                    String choosing = dropdown.getSelectedItem().toString();
                    try {
                        getMeasurementFromDatabase(choosing,TEMP);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    TextView value_view = findViewById(R.id.mode_temp_view);
                    value_view.setText("Showing some nearest measurements (unit : " + TEMP_UNIT +"):");
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Calendar c;
                        c = Calendar.getInstance();
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH) ;
                        int year = c.get(Calendar.YEAR);
                        switch(checkedId){
                            case R.id.radio_temp1:
                                String choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getMeasurementFromDatabase(choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView value_view = findViewById(R.id.mode_temp_view);
                                value_view.setText("Showing some nearest measurements (unit : " + TEMP_UNIT +"):");
                                break;
                            case R.id.radio_temp2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView hourly_view = findViewById(R.id.mode_temp_view);
                                hourly_view.setText("Measurement by hour (unit : " + TEMP_UNIT +") in day : "+ day + "-"+ (month + 1) + "-" + year);
                                break;

                            case R.id.radio_temp3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisMonth(third_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView daily_view = findViewById(R.id.mode_temp_view);
                                daily_view.setText("Measurement by day (unit : " + TEMP_UNIT +") in month : " + (month + 1) + "-" + year);
                                break;
                            case R.id.radio_temp4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisYear(fourth_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView monthly_view = findViewById(R.id.mode_temp_view);
                                monthly_view.setText("Measurement by month (unit : " + TEMP_UNIT +") in year : "  + year);
                                break;
                            case R.id.radio_temp5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                DatePickerDialog dpd;

                                dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int Dyear, int Dmonth, int DdayOfMonth) {
                                        TextView custom_view = findViewById(R.id.mode_temp_view);
                                        custom_view.setText("Measurement by hour (unit : " + TEMP_UNIT +") in day : " + DdayOfMonth + "-"+ (Dmonth + 1) + "-" + Dyear );
                                        try {
                                            getValueInCustomDate(fifth_choosing,TEMP,DdayOfMonth,(Dmonth+1),Dyear);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, year, month, day);
                                dpd.show();

                            }
                                break;
                        }
                    }
                });

                String AI_choosing = dropdown.getSelectedItem().toString();
                if(num_of_choices == 1)
                    setupAITimer(AI_choosing,TEMP);
                else
                    sendDatatoAI(AI_choosing,TEMP);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    private void makeHumidDeviceSpinner(final String type)
    {
        GetDeviceByType getDeviceByType = new GetDeviceByType(user_id,type);
        Thread thread = new Thread(getDeviceByType);
        thread.start();
        Vector<String> results = getDeviceByType.results;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //get the spinner from the xml.
        final Spinner dropdown = findViewById(R.id.spinner2);
        //create a list of items for the spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, results);
        final int num_of_choices = adapter.getCount();
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                final RadioGroup radioGroup = findViewById(R.id.radio_humidity);
                RadioButton rad = (RadioButton) findViewById(R.id.radio_humid1);

                if(rad.isChecked()){
                    String choosing = dropdown.getSelectedItem().toString();
                    try {
                        getMeasurementFromDatabase(choosing,HUMIDITY);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    TextView value_view = findViewById(R.id.mode_humid_view);
                    value_view.setText("Showing some nearest measurements (unit : " + HUMIDITY_UNIT +"):");
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Calendar c;
                        c = Calendar.getInstance();
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH) ;
                        int year = c.get(Calendar.YEAR);
                        switch(checkedId){
                            case R.id.radio_humid1:
                                String choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getMeasurementFromDatabase(choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView value_view = findViewById(R.id.mode_humid_view);
                                value_view.setText("Showing some nearest measurements (unit : " + HUMIDITY_UNIT +"):");
                                break;
                            case R.id.radio_humid2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView hourly_view = findViewById(R.id.mode_humid_view);
                                hourly_view.setText("Measurement by hour (unit : " + HUMIDITY_UNIT +") in day : "+ day + "-"+ (month + 1) + "-" + year);
                                break;
                            case R.id.radio_humid3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisMonth(third_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView daily_view = findViewById(R.id.mode_humid_view);
                                daily_view.setText("Measurement by day (unit : " + HUMIDITY_UNIT +") in month : " + (month + 1) + "-" + year);
                                break;
                            case R.id.radio_humid4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisYear(fourth_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView monthly_view = findViewById(R.id.mode_humid_view);
                                monthly_view.setText("Measurement by month (unit : " + HUMIDITY_UNIT +") in year : "  + year);
                                break;
                            case R.id.radio_humid5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                DatePickerDialog dpd;

                                dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDateSet(DatePicker view, int Dyear, int Dmonth, int DdayOfMonth) {
                                        TextView custom_view = findViewById(R.id.mode_humid_view);
                                        custom_view.setText("Measurement by hour (unit : " + HUMIDITY_UNIT +") in day : " + DdayOfMonth + "-"+ (Dmonth + 1) + "-" + Dyear );
                                        try {
                                            getValueInCustomDate(fifth_choosing,HUMIDITY,DdayOfMonth,(Dmonth+1),Dyear);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, year, month, day);
                                dpd.show();

                            }
                            break;
                        }
                    }
                });
                String AI_choosing = dropdown.getSelectedItem().toString();
                if(num_of_choices == 1)
                    setupAITimer(AI_choosing,HUMIDITY);
                else
                    sendDatatoAI(AI_choosing,HUMIDITY);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    private void makeLightDeviceSpinner(final String type)
    {
        GetDeviceByType getDeviceByType = new GetDeviceByType(user_id,type);
        Thread thread = new Thread(getDeviceByType);
        thread.start();
        Vector<String> results = getDeviceByType.results;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //get the spinner from the xml.
        final Spinner dropdown = findViewById(R.id.spinner3);
        //create a list of items for the spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, results);
        final int num_of_choices = adapter.getCount();
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                final RadioGroup radioGroup = findViewById(R.id.radio_light);
                RadioButton rad = (RadioButton) findViewById(R.id.radio_light1);
                if(rad.isChecked()){
                    String choosing = dropdown.getSelectedItem().toString();
                    try {
                        getMeasurementFromDatabase(choosing,type);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    TextView value_view = findViewById(R.id.mode_light_view);
                    value_view.setText("Showing some nearest measurements (unit : " + LIGHT_UNIT +"):");
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Calendar c;
                        c = Calendar.getInstance();
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH) ;
                        int year = c.get(Calendar.YEAR);
                        switch(checkedId){
                            case R.id.radio_light1:
                                String choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getMeasurementFromDatabase(choosing,type);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView value_view = findViewById(R.id.mode_light_view);
                                value_view.setText("Showing some nearest measurements (unit : " + LIGHT_UNIT +"):");
                                break;
                            case R.id.radio_light2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,type);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView hourly_view = findViewById(R.id.mode_light_view);
                                hourly_view.setText("Measurement by hour (unit : " + LIGHT_UNIT +") in day : "+ day + "-"+ (month + 1) + "-" + year);
                                break;
                            case R.id.radio_light3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisMonth(third_choosing,type);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView daily_view = findViewById(R.id.mode_light_view);
                                daily_view.setText("Measurement by day (unit : " + LIGHT_UNIT +") in month : " + (month + 1) + "-" + year);
                                break;
                            case R.id.radio_light4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisYear(fourth_choosing,type);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                TextView monthly_view = findViewById(R.id.mode_light_view);
                                monthly_view.setText("Measurement by month (unit : " + LIGHT_UNIT +") in year : "  + year);
                                break;
                            case R.id.radio_light5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                DatePickerDialog dpd;

                                dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDateSet(DatePicker view, int Dyear, int Dmonth, int DdayOfMonth) {
                                        TextView custom_view = findViewById(R.id.mode_light_view);
                                        custom_view.setText("Measurement by hour (unit : " + LIGHT_UNIT +") in day : " + DdayOfMonth + "-"+ (Dmonth + 1) + "-" + Dyear );
                                        try {
                                            getValueInCustomDate(fifth_choosing,LIGHT,DdayOfMonth,(Dmonth+1),Dyear);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, year, month, day);
                                dpd.show();

                            }
                            break;
                        }
                    }
                });
               String AI_choosing = dropdown.getSelectedItem().toString();
               if(num_of_choices == 1)
                    setupAITimer(AI_choosing,LIGHT);
               else
                    sendDatatoAI(AI_choosing,LIGHT);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMeasurementFromDatabase(String temp_device_id, String type) throws ParseException {
        GetDataFromURL getDataFromURL = new GetDataFromURL(temp_device_id,type);
        Thread thread = new Thread(getDataFromURL);
        thread.start();
        Vector<Double> results = getDataFromURL.results;
        Vector<String> dates = getDataFromURL.date;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawDataOnGraph(results,dates,type,VALUE_MODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getValueInCustomDate(String deviceID, String types, int day, int month, int year) throws ParseException {
        GetValueInCustomDate getValueInCustomDate = new GetValueInCustomDate(deviceID,types,day,month,year);
        Thread thread = new Thread(getValueInCustomDate);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Double> results = getValueInCustomDate.results;
        Vector<String>  hours = getValueInCustomDate.hours;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawDataOnGraph(results,hours,types,HOURLY_MODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getValueToday(String deviceID, String types) throws ParseException {
        GetValueToday getValueToday = new GetValueToday(deviceID,types);
        Thread thread = new Thread(getValueToday);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Double> results = getValueToday.results;
        Vector<String>  hours = getValueToday.hours;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawDataOnGraph(results,hours,types,HOURLY_MODE);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getValueThisMonth(String deviceID, String types) throws ParseException {
        GetValueThisMonth getThisMonthValue = new GetValueThisMonth(deviceID,types);
        Thread thread = new Thread(getThisMonthValue);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Double> results = getThisMonthValue.results;
        Vector<String>  days = getThisMonthValue.days;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawDataOnGraph(results,days,types,DAILY_MODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getValueThisYear(String deviceID, String types) throws ParseException {
        GetValueThisYear getValueThisYear = new GetValueThisYear(deviceID,types);
        Thread thread = new Thread(getValueThisYear);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Double> results = getValueThisYear.results;
        Vector<String>  months = getValueThisYear.months;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawDataOnGraph(results,months,types,MONTHLY_MODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawDataOnGraph(Vector<Double> results, Vector<String> date, String graphtype, String mode) throws ParseException {

        GraphView gv;
        if(graphtype.equals(TEMP))
            gv = graphTemperature;
        else if(graphtype.equals(HUMIDITY))
            gv = graphHumidity;
        else
            gv = graphLightLevel;
        DataPoint[] dataPoints = new DataPoint[results.size()];
        final HashMap<Integer, Long> mappoint = new HashMap<>();// declare an array of DataPoint objects with the same size as your list
        final HashMap<Integer, String> daily_mappoint = new HashMap<>();

        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            if(mode.equals(VALUE_MODE)) {
                String temp_date = date.get(i);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat first_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date firstDate = first_date_format.parse(temp_date);
                Calendar first_cal = Calendar.getInstance();
                assert firstDate != null;
                first_cal.setTime(firstDate);
                long second = first_cal.getTimeInMillis();
                mappoint.put(i, second);
                dataPoints[i] = new DataPoint(i, results.get(i));
            }
            else
            {
                daily_mappoint.put(i, date.get(i));
                dataPoints[i] = new DataPoint(i, results.get(i));
            }
        }
        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<DataPoint>(dataPoints);


        configGraph(gv,results.size(),seriesTemp,mode);
        showDataOnGraph(seriesTemp, gv);
        if (mode.equals(VALUE_MODE)) {
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            gv.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {

                    if (isValueX) {
                        Long x_value = mappoint.get((int) (value));
                        return sdf.format(new Date((long) (x_value)));
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                }
            });
        }
        else
        {
            gv.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return super.formatLabel(Double.parseDouble(daily_mappoint.get((int)value)), isValueX);
                    }
                    else
                        return super.formatLabel(value, isValueX);

                }
            });
        }
    }

    private void configGraph(GraphView gv, int num, LineGraphSeries<DataPoint> seriesTemp, String mode){
            int max_y ;
            if(gv == graphTemperature)
            {
                max_y = MAX_TEMP;
                seriesTemp.setColor(Color.rgb(226,91,34));
                gv.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.rgb(174,100,42));
                gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(14);
            }
            else if(gv == graphHumidity)
            {
                max_y = MAX_HUMIDITY;
                seriesTemp.setColor(Color.rgb(56,149,164));
                gv.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.rgb(51,153,204));
                gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(14);
            }
            else {
                max_y = MAX_LIGHT;
                seriesTemp.setColor(Color.rgb(123,135,13));
                gv.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.rgb(208,145,69));
                gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(14);
            }
            gv.getViewport().setMinY(0);
            gv.getViewport().setMaxY(max_y);
            gv.getGridLabelRenderer().setNumVerticalLabels(10);
            gv.getViewport().setMinX(0);
            gv.getViewport().setMaxX(num - 1);
            gv.getGridLabelRenderer().setNumHorizontalLabels(num);
            gv.getViewport().setYAxisBoundsManual(true);
            gv.getViewport().setXAxisBoundsManual(true);
            gv.getGridLabelRenderer().setHorizontalLabelsVisible(true);
            switch (mode) {
                case VALUE_MODE:
                    if (num <= 3) {
                        gv.getGridLabelRenderer().setTextSize(45f);
                    } else if (num == 4) {
                        gv.getGridLabelRenderer().setTextSize(30f);
                    } else {
                        gv.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                    }
                    gv.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                    gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
                    break;
                case HOURLY_MODE:
                    gv.getGridLabelRenderer().setHorizontalAxisTitle("Hour");
                    gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
                    break;
                case DAILY_MODE:
                    gv.getGridLabelRenderer().setHorizontalAxisTitle("Day");
                    gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
                    break;
                case MONTHLY_MODE:
                    gv.getGridLabelRenderer().setHorizontalAxisTitle("Month");
                    gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
                    break;
            }
    }

    private void showDataOnGraph(LineGraphSeries<DataPoint> series, GraphView graph){
        if(graph.getSeries().size() > 0){
            graph.getSeries().remove(0);
        }
        graph.addSeries(series);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(15);

    }

    private void sendDatatoAI(String temp_device_id,String type){
        GetDataFromURL getDataFromURL = new GetDataFromURL(temp_device_id,type);
        Thread thread = new Thread(getDataFromURL);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Vector<Double> results = getDataFromURL.results;
        Vector<String> dates = getDataFromURL.date;


        SendDataToAI sendDataToAI = new SendDataToAI(results,dates);
        Thread second_thread = new Thread(sendDataToAI);
        second_thread.start();

        try {
            second_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double AI_result = sendDataToAI.AI_result;
        recommendThreshold(AI_result,type);

    }

    @SuppressLint("SetTextI18n")
    private void recommendThreshold(double AI_result, String type) {
        if(type.equals(TEMP))
            textTemperature.setText("Recommended temperature threshold " + AI_result);
        else if(type.equals(HUMIDITY))
            textHumidity.setText("Recommended humidity threshold " + AI_result);
        else
            textLightLevel.setText("Recommended light density threshold " + AI_result);
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
                    if(obj.getString("status").equals("null")){
                        get_status[i] = "No record";
                        get_status_date[i] = "No record";
                    }
                    // Get device type and summarize
                    if(Helper.stringContainsItemFromList(get_device_id[i], Constants.OUTPUT_ID)) {
                        get_device_type[i] = Constants.OUTPUT_TYPE;
                        if(!get_status[i].equals("Off")){
                            count_output += 1;
                        }
                    }
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.LIGHT_SENSOR_ID)) {
                        get_device_type[i] = Constants.LIGHT_SENSOR_TYPE;
                        if(!get_status[i].equals("No record")){
                            count_light += 1;
                            sum_light += Integer.parseInt(get_status[i]);
                        }
                    }
                    else if (Helper.stringContainsItemFromList(get_device_id[i], Constants.TEMPHUMI_SENSOR_ID)) {
                        get_device_type[i] = Constants.TEMPHUMI_SENSOR_TYPE;
                        if(!get_status[i].equals("No record")){
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
                }
                else {
                    averageTemp_TV.setText(sum_temp / count_temp_humid + "\u2103");
                    averageHumid_TV.setText(sum_humid / count_temp_humid + "%");
                }

                if (count_light == 0) {
                    averageLight_TV.setText("No reading");
                }
                else {
                    averageLight_TV.setText(sum_light / count_light + " lux");
                }
                number_devices_TV.setText(count_light + count_output + count_temp_humid +"");

            }
            else{
                averageTemp_TV.setText("No reading");
                averageHumid_TV.setText("No reading");
                averageLight_TV.setText("No reading");
                number_devices_TV.setText(0 +"");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
