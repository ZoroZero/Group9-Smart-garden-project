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
import android.widget.ImageView;
import android.widget.LinearLayout;
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


        getTempMeasurementFromDatabase("ID119");
        getLightMeasurementFromDatabase("ID113");
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
