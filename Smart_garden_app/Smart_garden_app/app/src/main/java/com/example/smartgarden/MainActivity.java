package com.example.smartgarden;




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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

    GraphView graphTemperature,graphLightLevel;
    MQTTHelper mqttHelper;
    boolean flag = true;
    double preTemperature = 0;
    double preLightLevel = 0;
    int numOfTemp = 0;
    int numOfLight = 0;
    double []temp = {0,0,0,0,0};
    double []light = {0,0,0,0,0};



    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphLightLevel = findViewById(R.id.graphLightLevel);
        graphTemperature = findViewById(R.id.graphTemperature);


        makeTempDeviceSpinner("TH");
        makeLightDeviceSpinner("S");
        getthisYearValue("ID222");
        //getValueToday("ID119");
        //getThisMonthValue("ID222");
//        Vector<Double>  test = new Vector<>();
//        test.add(36.5);
//        test.add(37.7);
//        test.add(39.0);
//        sendDatatoAI(test);
    }


    private void getthisYearValue(String deviceID){
        GetValueThisYear getValueThisYear = new GetValueThisYear(deviceID);
        Thread thread = new Thread(getValueThisYear);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Double> results = getValueThisYear.results;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataPoint[] dataPoints = new DataPoint[results.size()]; // declare an array of DataPoint objects with the same size as your list
        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            dataPoints[i] = new DataPoint(i, results.get(i)); // not sure but I think the second argument should be of type double
        }
        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<DataPoint>(dataPoints);

        graphTemperature.getViewport().setMinY(0);
        graphTemperature.getViewport().setMaxY(900);
        graphTemperature.getViewport().setMinX(0);
        graphTemperature.getViewport().setMaxX(results.size() - 1);
        graphTemperature.getGridLabelRenderer().setNumHorizontalLabels(results.size());
        graphTemperature.getViewport().setYAxisBoundsManual(true);
        graphTemperature.getViewport().setXAxisBoundsManual(true);
        showDataOnGraph(seriesTemp, graphTemperature);
    }

    private void getValueToday(String deviceID){
        GetValueToday getValueToday = new GetValueToday(deviceID);
        Thread thread = new Thread(getValueToday);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Double> results = getValueToday.results;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataPoint[] dataPoints = new DataPoint[results.size()]; // declare an array of DataPoint objects with the same size as your list
        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            dataPoints[i] = new DataPoint(i, results.get(i)); // not sure but I think the second argument should be of type double
        }
        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<DataPoint>(dataPoints);

        graphTemperature.getViewport().setMinY(0);
        graphTemperature.getViewport().setMaxY(900);
        graphTemperature.getViewport().setMinX(0);
        graphTemperature.getViewport().setMaxX(results.size() - 1);
        graphTemperature.getGridLabelRenderer().setNumHorizontalLabels(results.size());
        graphTemperature.getViewport().setYAxisBoundsManual(true);
        graphTemperature.getViewport().setXAxisBoundsManual(true);
        showDataOnGraph(seriesTemp, graphTemperature);
    }
    private void getThisMonthValue(String deviceID){
        GetThisMonthValue getThisMonthValue = new GetThisMonthValue(deviceID);
        Thread thread = new Thread(getThisMonthValue);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Double> results = getThisMonthValue.results;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataPoint[] dataPoints = new DataPoint[results.size()]; // declare an array of DataPoint objects with the same size as your list
        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            dataPoints[i] = new DataPoint(i, results.get(i)); // not sure but I think the second argument should be of type double
        }
        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<DataPoint>(dataPoints);

        graphTemperature.getViewport().setMinY(0);
        graphTemperature.getViewport().setMaxY(900);
        graphTemperature.getViewport().setMinX(0);
        graphTemperature.getViewport().setMaxX(results.size() - 1);
        graphTemperature.getGridLabelRenderer().setNumHorizontalLabels(results.size());
        graphTemperature.getViewport().setYAxisBoundsManual(true);
        graphTemperature.getViewport().setXAxisBoundsManual(true);
        showDataOnGraph(seriesTemp, graphTemperature);
    }

    private void makeTempDeviceSpinner(String type)
    {
        GetDeviceByType getDeviceByType = new GetDeviceByType(type);
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
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                final RadioGroup radioGroup = findViewById(R.id.radio_temp);
                RadioButton rad = (RadioButton) findViewById(R.id.radio_temp1);
                if(rad.isChecked()){
                String choosing = dropdown.getSelectedItem().toString();
                getTempMeasurementFromDatabase(choosing);}
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case R.id.radio_temp1:
                                String choosing = dropdown.getSelectedItem().toString();
                                getTempMeasurementFromDatabase(choosing);
                                break;
                            case R.id.radio_temp2:
                                String second_choosing = dropdown.getSelectedItem().toString();
                                getthisYearValue("ID222");
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

    private void sendDatatoAI(Vector<Double> test){
        SendDataToAI sendDataToAI = new SendDataToAI(test);
        Thread thread = new Thread(sendDataToAI);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void makeLightDeviceSpinner(String type)
    {
        GetDeviceByType getDeviceByType = new GetDeviceByType(type);
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
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String choosing = dropdown.getSelectedItem().toString();
                getLightMeasurementFromDatabase(choosing);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    private void getTempMeasurementFromDatabase(String temp_device_id){
        GetDataFromURL getDataFromURL = new GetDataFromURL(temp_device_id);
        Thread thread = new Thread(getDataFromURL);
        thread.start();
        Vector<Double> results = getDataFromURL.results;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataPoint[] dataPoints = new DataPoint[results.size()]; // declare an array of DataPoint objects with the same size as your list
        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            dataPoints[i] = new DataPoint(i, results.get(i)); // not sure but I think the second argument should be of type double
        }
        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<DataPoint>(dataPoints);

        graphTemperature.getViewport().setMinY(0);
        graphTemperature.getViewport().setMaxY(900);
        graphTemperature.getViewport().setMinX(0);
        graphTemperature.getViewport().setMaxX(results.size() - 1);
        graphTemperature.getGridLabelRenderer().setNumHorizontalLabels(results.size());
        graphTemperature.getViewport().setYAxisBoundsManual(true);
        graphTemperature.getViewport().setXAxisBoundsManual(true);
        showDataOnGraph(seriesTemp, graphTemperature);
    }
    private void getLightMeasurementFromDatabase(String light_device_id){
        GetDataFromURL getDataFromURL = new GetDataFromURL(light_device_id);
        Thread thread = new Thread(getDataFromURL);
        thread.start();
        Vector<Double> results = getDataFromURL.results;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataPoint[] dataPoints = new DataPoint[results.size()]; // declare an array of DataPoint objects with the same size as your list
        for (int i = 0; i < results.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            dataPoints[i] = new DataPoint(i, results.get(i)); // not sure but I think the second argument should be of type double
        }
        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<DataPoint>(dataPoints);

        graphLightLevel.getViewport().setMinY(0);
        graphLightLevel.getViewport().setMaxY(900);
        graphLightLevel.getViewport().setMinX(0);
        graphLightLevel.getViewport().setMaxX(results.size() - 1);
        graphLightLevel.getGridLabelRenderer().setNumHorizontalLabels(results.size());
        graphLightLevel.getViewport().setYAxisBoundsManual(true);
        graphLightLevel.getViewport().setXAxisBoundsManual(true);
        showDataOnGraph(seriesTemp, graphLightLevel);
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
