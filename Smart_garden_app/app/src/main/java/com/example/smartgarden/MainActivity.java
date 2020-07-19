package com.example.smartgarden;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.app.DatePickerDialog;
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


import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;



public class MainActivity extends AppCompatActivity {
    GraphView graphTemperature,graphHumidity,graphLightLevel;
    TextView  textTemperature,textHumidity,textLightLevel;
    //Constant for device type
    protected final static String TEMP_HUMIDITY =  "TH";
    protected final static String TEMP = "T";
    protected final static String HUMIDITY = "H";
    protected final static String LIGHT = "L";

    //Constant for value threshold
    protected final static int MAX_TEMP = 50;
    protected final static int MAX_HUMIDITY = 100;
    protected final static int MAX_LIGHT = 500;

    //Constant for mode
    protected final static String VALUE_MODE = "VALUE";
    protected final static String DAY_MODE = "DAY";
    protected final static String MONTH_MODE = "MONTH";
    protected final static String YEAR_MODE = "YEAR";

    //Schedule for AI timer
    protected final static int DELAY = 20;
    protected final static int PERIOD = 5000;

    //User ID from teammate part
    protected String user_id = "10";


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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    sendDatatoAI(choosing,TEMP);
                    TextView value_view = findViewById(R.id.mode_temp_view);
                    value_view.setText("Showing some nearest values: ");
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
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
                                sendDatatoAI(choosing,TEMP);
                                TextView value_view = findViewById(R.id.mode_temp_view);
                                value_view.setText("Showing some nearest values: ");
                                break;
                            case R.id.radio_temp2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(second_choosing,TEMP);
                                TextView hourly_view = findViewById(R.id.mode_temp_view);
                                hourly_view.setText("Measurement by hour in day : "+ day + "-"+ (month + 1) + "-" + year);
                                break;

                            case R.id.radio_temp3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisMonth(third_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(third_choosing,TEMP);
                                TextView daily_view = findViewById(R.id.mode_temp_view);
                                daily_view.setText("Measurement by day in month : " + (month + 1) + "-" + year);
                                break;
                            case R.id.radio_temp4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisYear(fourth_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(fourth_choosing,TEMP);
                                TextView monthly_view = findViewById(R.id.mode_temp_view);
                                monthly_view.setText("Measurement by month in year : "  + year);
                                break;
                            case R.id.radio_temp5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                sendDatatoAI(fifth_choosing,TEMP);
                                DatePickerDialog dpd;

                                dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int Dyear, int Dmonth, int DdayOfMonth) {
                                        TextView custom_view = findViewById(R.id.mode_temp_view);
                                        custom_view.setText("Measurement by hour in day : " + DdayOfMonth + "-"+ (Dmonth + 1) + "-" + Dyear );
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
                    sendDatatoAI(choosing,HUMIDITY);
                    TextView value_view = findViewById(R.id.mode_humid_view);
                    value_view.setText("Showing some nearest values: ");
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
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
                                sendDatatoAI(choosing,HUMIDITY);
                                TextView value_view = findViewById(R.id.mode_humid_view);
                                value_view.setText("Showing some nearest values: ");
                                break;
                            case R.id.radio_humid2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(second_choosing,HUMIDITY);
                                TextView hourly_view = findViewById(R.id.mode_humid_view);
                                hourly_view.setText("Measurement by hour in day : "+ day + "-"+ (month + 1) + "-" + year);
                                break;
                            case R.id.radio_humid3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisMonth(third_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(third_choosing,HUMIDITY);
                                TextView daily_view = findViewById(R.id.mode_humid_view);
                                daily_view.setText("Measurement by day in month : " + (month + 1) + "-" + year);
                                break;
                            case R.id.radio_humid4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisYear(fourth_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(fourth_choosing,HUMIDITY);
                                TextView monthly_view = findViewById(R.id.mode_humid_view);
                                monthly_view.setText("Measurement by month in year : "  + year);
                                break;
                            case R.id.radio_humid5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                sendDatatoAI(fifth_choosing,HUMIDITY);
                                DatePickerDialog dpd;

                                dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int Dyear, int Dmonth, int DdayOfMonth) {
                                        TextView custom_view = findViewById(R.id.mode_humid_view);
                                        custom_view.setText("Measurement by hour in day : " + DdayOfMonth + "-"+ (Dmonth + 1) + "-" + Dyear );
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
                    sendDatatoAI(choosing,LIGHT);
                    TextView value_view = findViewById(R.id.mode_light_view);
                    value_view.setText("Showing some nearest values: ");
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
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
                                sendDatatoAI(choosing,LIGHT);
                                TextView value_view = findViewById(R.id.mode_light_view);
                                value_view.setText("Showing some nearest values: ");
                                break;
                            case R.id.radio_light2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,type);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(second_choosing,LIGHT);
                                TextView hourly_view = findViewById(R.id.mode_light_view);
                                hourly_view.setText("Measurement by hour in day : "+ day + "-"+ (month + 1) + "-" + year);
                                break;
                            case R.id.radio_light3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisMonth(third_choosing,type);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(third_choosing,LIGHT);
                                TextView daily_view = findViewById(R.id.mode_light_view);
                                daily_view.setText("Measurement by day in month : " + (month + 1) + "-" + year);
                                break;
                            case R.id.radio_light4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueThisYear(fourth_choosing,type);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                sendDatatoAI(fourth_choosing,LIGHT);
                                TextView monthly_view = findViewById(R.id.mode_light_view);
                                monthly_view.setText("Measurement by month in year : "  + year);
                                break;
                            case R.id.radio_light5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                sendDatatoAI(fifth_choosing,LIGHT);
                                DatePickerDialog dpd;

                                dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int Dyear, int Dmonth, int DdayOfMonth) {
                                        TextView custom_view = findViewById(R.id.mode_light_view);
                                        custom_view.setText("Measurement by hour in day : " + DdayOfMonth + "-"+ (Dmonth + 1) + "-" + Dyear );
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
        drawDataOnGraph(results,hours,types,DAY_MODE);
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
        drawDataOnGraph(results,hours,types,DAY_MODE);
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
        drawDataOnGraph(results,days,types,MONTH_MODE);
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
        drawDataOnGraph(results,months,types,YEAR_MODE);
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawDataOnGraph(Vector<Double> results, Vector<String> date, String graphtype, String mode) throws ParseException {

        GraphView gv;
        if(graphtype == TEMP)
            gv = graphTemperature;
        else if(graphtype == HUMIDITY)
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
                SimpleDateFormat first_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date firstDate = first_date_format.parse(temp_date);
                Calendar first_cal = Calendar.getInstance();
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


        configGraph(gv,results.size(),seriesTemp);
        showDataOnGraph(seriesTemp, gv);
        if (mode.equals(VALUE_MODE)) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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

    private void configGraph(GraphView gv, int num, LineGraphSeries<DataPoint> seriesTemp){
            int max_y ;
            if(gv == graphTemperature)
            {
                max_y = MAX_TEMP;
                seriesTemp.setColor(Color.rgb(226,91,34));
            }
            else if(gv == graphHumidity)
            {
                max_y = MAX_HUMIDITY;
                seriesTemp.setColor(Color.rgb(56,149,164));
            }
            else {
                max_y = MAX_LIGHT;
                seriesTemp.setColor(Color.rgb(123,135,13));
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
            if (num <= 3)
            {
                gv.getGridLabelRenderer().setTextSize(45f);
            }
            else if(num == 4)
            {
                gv.getGridLabelRenderer().setTextSize(30f);
            }
            else
            {
                gv.getGridLabelRenderer().setHorizontalLabelsVisible(false);
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
    private void recommendThreshold(double AI_result, String type) {
        if(type.equals(TEMP))
            textTemperature.setText("Recommended temperature threshold " + String.valueOf(AI_result));
        else if(type.equals(HUMIDITY))
            textHumidity.setText("Recommended humidity threshold " + String.valueOf(AI_result));
        else
            textLightLevel.setText("Recommended light density threshold " + String.valueOf(AI_result));


    }


}
