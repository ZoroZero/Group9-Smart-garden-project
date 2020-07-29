package Report;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.app.DatePickerDialog;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.example.smartgarden.R;
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


import java.util.Vector;

import Database.DatabaseService.GetDataFromURL;
import Database.DatabaseService.GetDeviceByType;
import Database.DatabaseService.GetValueInCustomDate;
import Database.DatabaseService.GetValueThisMonth;
import Database.DatabaseService.GetValueThisYear;
import Database.DatabaseService.GetValueToday;
import Database.DatabaseService.SendDataToAI;
import Helper.Constants;
import Login_RegisterUser.UserLoginManagement;


public class ViewReport extends AppCompatActivity {
    GraphView graphTemperature,graphHumidity,graphLightLevel;
    TextView  textTemperature,textHumidity,textLightLevel;
    //Constant for device type
    public final static String TEMP_HUMIDITY = Constants.TEMPHUMI_SENSOR_TYPE;
    public final static String TEMP = "T";
    public final static String HUMIDITY = "H";
    public final static String LIGHT = Constants.LIGHT_SENSOR_TYPE;

    //Constant for value threshold
    protected final static int MAX_TEMP = 50;
    protected final static int MAX_HUMIDITY = 100;
    protected final static int MAX_LIGHT = 500;

    //Constant for mode
    protected final static String VALUE_MODE = "Value";
    protected final static String HOURLY_MODE = "Hourly";
    protected final static String DAILY_MODE = "Daily";
    protected final static String MONTHLY_MODE = "Monthly";
    protected final static String CUSTOM_MODE = "Custom";

    //Unit
    protected final static String TEMP_UNIT = "\u2103";
    protected final static String HUMIDITY_UNIT = "%";
    protected final static String LIGHT_UNIT = "lux";

    //Title size
    protected final static float TITLE_SIZE = 70f;

    //For refresh
    protected String temp_last_choosing;
    protected String temp_last_mode;
    protected String humidity_last_choosing;
    protected String humidity_last_mode;
    protected String light_last_choosing;
    protected String light_last_mode;

    //User ID from teammate part
    protected String user_id = UserLoginManagement.getInstance(this).getUserId() + "";

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
        final String temp_type = TEMP;
        final String temp_unit = TEMP_UNIT;
        //create a list of items for the spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, results);
        final int num_of_choices = adapter.getCount();
        dropdown.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
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
                    temp_last_choosing = choosing;
                    temp_last_mode = VALUE_MODE;
                    try {
                        showDataByMode(choosing,temp_type,VALUE_MODE,temp_unit);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case R.id.radio_temp1:
                                String choosing = dropdown.getSelectedItem().toString();
                                temp_last_choosing = choosing;
                                temp_last_mode = VALUE_MODE;
                                try {
                                    showDataByMode(choosing,temp_type,VALUE_MODE,temp_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_temp2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                temp_last_choosing = second_choosing;
                                temp_last_mode = HOURLY_MODE;
                                try {
                                    showDataByMode(second_choosing,temp_type,HOURLY_MODE,temp_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case R.id.radio_temp3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                temp_last_choosing = third_choosing;
                                temp_last_mode = DAILY_MODE;
                                try {
                                    showDataByMode(third_choosing,temp_type,DAILY_MODE,temp_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_temp4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                temp_last_choosing = fourth_choosing;
                                temp_last_mode = MONTHLY_MODE;
                                try {
                                    showDataByMode(fourth_choosing,temp_type,MONTHLY_MODE,temp_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_temp5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                temp_last_choosing = fifth_choosing;
                                temp_last_mode = CUSTOM_MODE;
                                try {
                                    showDataByMode(fifth_choosing,temp_type,CUSTOM_MODE,temp_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                                break;
                        }
                    }
                });

                String AI_choosing = dropdown.getSelectedItem().toString();
                sendDatatoAI(AI_choosing,TEMP);
//                if(num_of_choices == 1)
//                    setupAITimer(AI_choosing,TEMP);
//                else
//                    sendDatatoAI(AI_choosing,TEMP);

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
        final String humid_type = HUMIDITY;
        final String humid_unit = HUMIDITY_UNIT;
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
                    humidity_last_choosing = choosing;
                    humidity_last_mode = VALUE_MODE;
                    try {
                        showDataByMode(choosing,humid_type,VALUE_MODE,humid_unit);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case R.id.radio_humid1:
                                String choosing = dropdown.getSelectedItem().toString();
                                humidity_last_choosing = choosing;
                                humidity_last_mode = VALUE_MODE;
                                try {
                                    showDataByMode(choosing,humid_type,VALUE_MODE,humid_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_humid2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                humidity_last_choosing = second_choosing;
                                humidity_last_mode = HOURLY_MODE;
                                try {
                                    showDataByMode(second_choosing,humid_type,HOURLY_MODE,humid_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_humid3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                humidity_last_choosing = third_choosing;
                                humidity_last_mode = DAILY_MODE;
                                try {
                                    showDataByMode(third_choosing,humid_type,DAILY_MODE,humid_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_humid4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                humidity_last_choosing = fourth_choosing;
                                humidity_last_mode = MONTHLY_MODE;
                                try {
                                    showDataByMode(fourth_choosing,humid_type,MONTHLY_MODE,humid_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_humid5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                humidity_last_choosing = fifth_choosing;
                                humidity_last_mode = CUSTOM_MODE;
                                try {
                                    showDataByMode(fifth_choosing,humid_type,CUSTOM_MODE,humid_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                            break;
                        }
                    }
                });
                String AI_choosing = dropdown.getSelectedItem().toString();
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
        final String light_type = LIGHT;
        final String light_unit = LIGHT_UNIT;
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
                Button bt = findViewById(R.id.button_temp);
                if(rad.isChecked()){
                    String choosing = dropdown.getSelectedItem().toString();
                    light_last_choosing = choosing;
                    light_last_mode = VALUE_MODE;
                    try {
                        showDataByMode(choosing,light_type,VALUE_MODE,light_unit);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case R.id.radio_light1:
                                String choosing = dropdown.getSelectedItem().toString();
                                light_last_choosing = choosing;
                                light_last_mode= VALUE_MODE;
                                try {
                                    showDataByMode(choosing,light_type,VALUE_MODE,light_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_light2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                light_last_choosing = second_choosing;
                                light_last_mode = HOURLY_MODE;
                                try {
                                    showDataByMode(second_choosing,light_type,HOURLY_MODE,light_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_light3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                light_last_choosing = third_choosing;
                                light_last_mode = DAILY_MODE;
                                try {
                                    showDataByMode(third_choosing,light_type,DAILY_MODE,light_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_light4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                light_last_choosing = fourth_choosing;
                                light_last_mode = MONTHLY_MODE;
                                try {
                                    showDataByMode(fourth_choosing,light_type,MONTHLY_MODE,light_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_light5:
                            {
                                final String fifth_choosing = dropdown.getSelectedItem().toString();
                                light_last_choosing = fifth_choosing;
                                light_last_mode = CUSTOM_MODE;
                                try {
                                    showDataByMode(fifth_choosing,light_type,CUSTOM_MODE,light_unit);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                    }
                });
               String AI_choosing = dropdown.getSelectedItem().toString();
               sendDatatoAI(AI_choosing,LIGHT);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDataByMode(final String choosing, final String type, String mode, final String unit) throws ParseException {
        final TextView tv ;
        Calendar c;
        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) ;
        int year = c.get(Calendar.YEAR);
        switch(type)
        {
            case TEMP:
                tv = findViewById(R.id.mode_temp_view);
                break;
            case HUMIDITY:
                tv = findViewById(R.id.mode_humid_view);
                break;
            case LIGHT:
                tv = findViewById(R.id.mode_light_view);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        if(mode.equals(VALUE_MODE))
        {
            getMeasurementFromDatabase(choosing,type);
            tv.setText("Showing some nearest measurements (unit : " + unit +"):");
        }
        else if(mode.equals(HOURLY_MODE))
        {
            getValueToday(choosing,type);
            tv.setText("Measurement by hour (unit : " + unit +") in day : "+ day + "-"+ (month + 1) + "-" + year);
        }
        else if(mode.equals(DAILY_MODE))
        {
            getValueThisMonth(choosing,type);
            tv.setText("Measurement by day (unit : " + unit +") in month : " + (month + 1) + "-" + year);
        }
        else if(mode.equals(MONTHLY_MODE))
        {
            getValueThisYear(choosing,type);
            tv.setText("Measurement by month (unit : " + unit +") in year : "  + year);
        }
        else
        {

            DatePickerDialog dpd;
            dpd = new DatePickerDialog(ViewReport.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int Dyear, int Dmonth, int DdayOfMonth) {
                    tv.setText("Measurement by hour (unit : " + unit +") in day : " + DdayOfMonth + "-"+ (Dmonth + 1) + "-" + Dyear );
                    try {
                        getValueInCustomDate(choosing,type,DdayOfMonth,(Dmonth+1),Dyear);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }, year, month, day);
            dpd.show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void temp_refresh(View v) throws ParseException {
        showDataByMode(temp_last_choosing,TEMP,temp_last_mode,TEMP_UNIT);
        sendDatatoAI(temp_last_choosing,TEMP);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void humidity_refresh(View v) throws ParseException {
        showDataByMode(humidity_last_choosing,HUMIDITY,humidity_last_mode,HUMIDITY_UNIT);
        sendDatatoAI(humidity_last_choosing,HUMIDITY);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void light_refresh(View v) throws ParseException {
        showDataByMode(light_last_choosing,LIGHT,light_last_mode,LIGHT_UNIT);
        sendDatatoAI(light_last_choosing,LIGHT);
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


        configGraph(gv,results.size(),seriesTemp,mode);
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

    private void configGraph(GraphView gv, int num, LineGraphSeries<DataPoint> seriesTemp, String mode){
            int max_y ;
            if(gv == graphTemperature)
            {
                max_y = MAX_TEMP;
                seriesTemp.setColor(Color.rgb(226,91,34));
                gv.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.GREEN);
            }
            else if(gv == graphHumidity)
            {
                max_y = MAX_HUMIDITY;
                seriesTemp.setColor(Color.rgb(56,149,164));
                gv.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLUE);

            }
            else {
                max_y = MAX_LIGHT;
                seriesTemp.setColor(Color.rgb(123,135,13));
                gv.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.YELLOW);
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
            if(mode.equals(VALUE_MODE)) {
                if (num <= 3) {
                    gv.getGridLabelRenderer().setTextSize(45f);
                } else if (num == 4) {
                    gv.getGridLabelRenderer().setTextSize(30f);
                } else {
                    gv.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                }
                gv.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
            }
            else if(mode.equals(HOURLY_MODE))
            {
                gv.getGridLabelRenderer().setHorizontalAxisTitle("Hour");
                gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
            }
            else if(mode.equals(DAILY_MODE))
            {
            gv.getGridLabelRenderer().setHorizontalAxisTitle("Day");
            gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
            }
            else if(mode.equals(MONTHLY_MODE))            {
            gv.getGridLabelRenderer().setHorizontalAxisTitle("Month");
            gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(TITLE_SIZE);
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
            textTemperature.setText("Recommended temperature threshold " + String.valueOf(AI_result));
        else if(type.equals(HUMIDITY))
            textHumidity.setText("Recommended humidity threshold " + String.valueOf(AI_result));
        else
            textLightLevel.setText("Recommended light density threshold " + String.valueOf(AI_result));


    }

}
