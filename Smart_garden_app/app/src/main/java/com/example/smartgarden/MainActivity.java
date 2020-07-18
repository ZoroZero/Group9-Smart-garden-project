package com.example.smartgarden;




import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.Base64;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.security.auth.login.LoginException;


public class MainActivity extends AppCompatActivity {
    GraphView graphTemperature,graphHumidity,graphLightLevel;
    private final static String TEMP_HUMIDITY =  "TH";
    private final static String TEMP = "T";
    private final static String HUMIDITY = "H";
    private final static String LIGHT = "L";
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


        makeTempDeviceSpinner(TEMP_HUMIDITY);
        makeHumidDeviceSpinner(TEMP_HUMIDITY);
        makeLightDeviceSpinner(LIGHT);



    }






    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTempMeasurementFromDatabase(String temp_device_id, String type) throws ParseException {
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
        drawGraphTemperature(results,dates,"VALUE");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getHumidMeasurementFromDatabase(String temp_device_id, String type) throws ParseException {
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
        drawHumidTemperature(results,dates,"VALUE");
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
        if(types.equals(TEMP))
        {
            drawGraphTemperature(results,hours,"DAY");
        }
        else if(types.equals(HUMIDITY))
        {
            drawHumidTemperature(results,hours,"DAY");
        }
        else
        {
            drawLightTemperature(results,hours,"DAY");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getThisMonthValue(String deviceID, String types) throws ParseException {
        GetThisMonthValue getThisMonthValue = new GetThisMonthValue(deviceID,types);
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

        if(types.equals(TEMP))
        {
            drawGraphTemperature(results,days,"DAY");
        }
        else if(types.equals(HUMIDITY))
        {
            drawHumidTemperature(results,days,"DAY");
        }
        else
        {
            drawLightTemperature(results,days,"DAY");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getthisYearValue(String deviceID, String types) throws ParseException {
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

        if(types.equals(TEMP))
        {
            drawGraphTemperature(results,months,"DAY");
        }
        else if(types.equals(HUMIDITY))
        {
            drawHumidTemperature(results,months,"DAY");
        }
        else
        {
            drawLightTemperature(results,months,"DAY");
        }
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
                        getTempMeasurementFromDatabase(choosing,TEMP);
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
                                try {
                                    getTempMeasurementFromDatabase(choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_temp2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_temp3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getThisMonthValue(third_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_temp4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getthisYearValue(fourth_choosing,TEMP);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });

//                String AI_choosing = dropdown.getSelectedItem().toString();
//                sendDatatoAI(AI_choosing);
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
                        getHumidMeasurementFromDatabase(choosing,HUMIDITY);
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
                                try {
                                    getHumidMeasurementFromDatabase(choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_humid2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_humid3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getThisMonthValue(third_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_humid4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getthisYearValue(fourth_choosing,HUMIDITY);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });

//                String AI_choosing = dropdown.getSelectedItem().toString();
//                sendDatatoAI(AI_choosing);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }
    private void sendDatatoAI(String temp_device_id){
        GetDataFromURLWithType getDataFromURLWithType = new GetDataFromURLWithType(temp_device_id,TEMP);
        Thread thread = new Thread(getDataFromURLWithType);
        thread.start();
        Vector<Double> results = getDataFromURLWithType.results;
        Vector<String> dates = getDataFromURLWithType.date;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        SendDataToAI sendDataToAI = new SendDataToAI(results,dates);
        Thread second_thread = new Thread(sendDataToAI);
        second_thread.start();

        try {
            second_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double AI_result = sendDataToAI.AI_result;
        TextView mytextview;
        mytextview = findViewById(R.id.temp_view);
        mytextview.setText("Recommendation value " + String.valueOf(AI_result));

    }
    private void makeLightDeviceSpinner(String type)
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
                        getLightMeasurementFromDatabase(choosing);
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
                                try {
                                    getLightMeasurementFromDatabase(choosing);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_light2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getValueToday(second_choosing,"L");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_light3:
                                String third_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getThisMonthValue(third_choosing,"L");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio_light4:
                                String fourth_choosing = dropdown.getSelectedItem().toString();
                                try {
                                    getthisYearValue(fourth_choosing,"L");
                                } catch (ParseException e) {
                                    e.printStackTrace();
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
    private void getLightMeasurementFromDatabase(String light_device_id) throws ParseException {
        GetDataFromURL getDataFromURL = new GetDataFromURL(light_device_id, LIGHT);
        Thread thread = new Thread(getDataFromURL);
        thread.start();
        Vector<Double> results = getDataFromURL.results;
        Vector<String> dates = getDataFromURL.date;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        drawLightTemperature(results,dates,"VALUE");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawGraphTemperature(Vector<Double> results, Vector<String> date, String mode) throws ParseException {

        DataPoint[] dataPoints = new DataPoint[results.size()];
        final HashMap<Integer, Long> mappoint = new HashMap<>();// declare an array of DataPoint objects with the same size as your list
        final HashMap<Integer, String> daily_mappoint = new HashMap<>();

        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            if(mode == "VALUE") {
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

        seriesTemp.setColor(Color.rgb(226,91,34));
        seriesTemp.setDataPointsRadius(15);

        configGraphTemperature(graphTemperature,results.size());
        showDataOnGraph(seriesTemp, graphTemperature);
        if (mode == "VALUE") {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            graphTemperature.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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
            graphTemperature.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawHumidTemperature(Vector<Double> results, Vector<String> date, String mode) throws ParseException {

        DataPoint[] dataPoints = new DataPoint[results.size()];
        final HashMap<Integer, Long> mappoint = new HashMap<>();// declare an array of DataPoint objects with the same size as your list
        final HashMap<Integer, String> daily_mappoint = new HashMap<>();

        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            if(mode == "VALUE") {
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

        seriesTemp.setColor(Color.rgb(226,91,34));
        seriesTemp.setDataPointsRadius(15);

        configGraphTemperature(graphHumidity,results.size());
        showDataOnGraph(seriesTemp, graphHumidity);
        if (mode == "VALUE") {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            graphHumidity.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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
            graphHumidity.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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

    private void configGraphTemperature(GraphView gv, int num){
            int max_y ;
            if(gv == graphTemperature)
                max_y = 50;
            else if(gv == graphHumidity)
                max_y = 100;
            else
                max_y = 500;
            Log.e("met", String.valueOf(num));
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




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawLightTemperature(Vector<Double> results, Vector<String> date, String mode) throws ParseException {

        DataPoint[] dataPoints = new DataPoint[results.size()];
        final HashMap<Integer, Long> mappoint = new HashMap<>();// declare an array of DataPoint objects with the same size as your list
        final HashMap<Integer, String> daily_mappoint = new HashMap<>();

        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            if(mode == "VALUE") {
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

        seriesTemp.setColor(Color.rgb(226,91,34));
        seriesTemp.setDataPointsRadius(15);

        configGraphTemperature(graphLightLevel,results.size());
        showDataOnGraph(seriesTemp, graphLightLevel);
        if (mode == "VALUE") {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            graphLightLevel.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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
            graphLightLevel.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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
    private void showDataOnGraph(LineGraphSeries<DataPoint> series, GraphView graph){
        if(graph.getSeries().size() > 0){
            graph.getSeries().remove(0);
        }
        graph.addSeries(series);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);

    }


}
