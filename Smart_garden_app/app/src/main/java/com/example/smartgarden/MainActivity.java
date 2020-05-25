package com.example.smartgarden;

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphLightLevel = findViewById(R.id.graphLightLevel);
        graphTemperature = findViewById(R.id.graphTemperature);

        if (flag) {
            flag = false;
            getDataFromThingSpeak();
        }
        startMQTT();


        graphTemperature.getViewport().setMinY(0);
        graphTemperature.getViewport().setMaxY(60);
        graphTemperature.getViewport().setYAxisBoundsManual(true);

       setupBlinkyTimer();




    }

    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext());
        Log.d("MQTT", "Step 1");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.d("MQTT", topic);
                System.out.print(mqttMessage.toString());
                sendDataToThingSpeak(topic.toString(), mqttMessage.toString());
                getDataFromThingSpeak();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private void showDataOnGraph(LineGraphSeries<DataPoint> series, GraphView graph){
        if(graph.getSeries().size() > 0){
            graph.getSeries().remove(0);
        }
        graph.addSeries(series);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);

    }


    private boolean checkNull(double n){
        String s = n + "";
        if (s == null) return true;
        return false;


    }
    private void getDataFromThingSpeak(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        String apiURL = "https://api.thingspeak.com/channels/1005910/feeds.json?results=5";
        Request request = builder.url(apiURL).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonString = response.body().string();
                Log.e("new test", "DUY");
                try{
                    JSONObject jsonData = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonData.getJSONArray("feeds");
                    double temp0 = jsonArray.getJSONObject(0).getDouble("field1");
                    preTemperature = temp0;
                    double temp1 = jsonArray.getJSONObject(1).getDouble("field1");
                    preTemperature = temp1;
                    double temp2 = jsonArray.getJSONObject(2).getDouble("field1");
                    preTemperature = temp2;
                    double temp3 = jsonArray.getJSONObject(3).getDouble("field1");
                    preTemperature = temp3;
                    double temp4 = jsonArray.getJSONObject(4).getDouble("field1");
                    preTemperature = temp4;
                    LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<>(new DataPoint[]
                            {   new DataPoint(0, temp0),
                                    new DataPoint(1, temp1),
                                    new DataPoint(2, temp2),
                                    new DataPoint(3, temp3),
                                    new DataPoint(4, temp4)
                            });

                    showDataOnGraph(seriesTemp, graphTemperature);
                }catch (Exception e){

                }
                try{
                    JSONObject jsonData = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonData.getJSONArray("feeds");
                    double light0 = jsonArray.getJSONObject(0).getDouble("field2");
                    preLightLevel = light0;
                    double light1 = jsonArray.getJSONObject(1).getDouble("field2");
                    preLightLevel = light1;
                    double light2 = jsonArray.getJSONObject(2).getDouble("field2");
                    preLightLevel = light2;
                    double light3 = jsonArray.getJSONObject(3).getDouble("field2");
                    preLightLevel = light3;
                    double light4 = jsonArray.getJSONObject(4).getDouble("field2");
                    preLightLevel = light4;
                    LineGraphSeries<DataPoint> seriesLight = new LineGraphSeries<>(new DataPoint[]
                            {   new DataPoint(0, light0),
                                    new DataPoint(1, light1),
                                    new DataPoint(2, light2),
                                    new DataPoint(3, light3),
                                    new DataPoint(4, light4)
                            });

                    showDataOnGraph(seriesLight, graphLightLevel);
                }catch (Exception e){

                }
            }
        });
    }
    void sendDataToThingSpeak(String nfield, String content){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        String apiURL = "https://api.thingspeak.com/update?api_key=OF5A5H8ATEQAMM3W&field1=0&field2=0";
        if (nfield.equals("sensor/Temperature")) {
            apiURL = "https://api.thingspeak.com/update?api_key=OF5A5H8ATEQAMM3W&field1=" + content + "&field2=" + preLightLevel;
        } else if (nfield.equals("sensor/Light"))
            apiURL = "https://api.thingspeak.com/update?api_key=OF5A5H8ATEQAMM3W&field1=" + preTemperature + "&field2=" + content;
        Request request = builder.url(apiURL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }
    private void setupBlinkyTimer(){
        Timer mTimer = new Timer();
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
               getDataFromThingSpeak();
            }
        };
        mTimer.schedule(mTask, 2000, 5000);

    }




}
