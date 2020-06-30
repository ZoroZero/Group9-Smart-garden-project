package com.example.smartgarden;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
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



    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable =   false;
            mExternalStorageWriteable = false;
        }
        if(!mExternalStorageWriteable)
        {

        }




        View v1 = graphTemperature.getRootView();
        v1.setDrawingCacheEnabled(true);
        v1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());

        v1.buildDrawingCache(true);
        Bitmap screen = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);



        /*File pdfDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "MyApp");
        if (!pdfDir.exists()){
            pdfDir.mkdirs();
        }
        File pdfFile = new File(pdfDir, "myPdfFile.pdf");*/

        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/Gym");
        if(!dir.exists()) {
            if (dir.mkdir()) {
                Log.e("gym", "Dir made");
            } else {
                Log.e("gym", "Error: Dir not made");
            }
        }

        File file = new File(dir, "myPdfFile.pdf");

        try {
                Document  document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                addImage(document,byteArray);
                document.close();

        }

        catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);


    }



    private static void addImage(Document document,byte[] byteArray)
    {
        Image image = null;
        try
        {
            image = Image.getInstance(byteArray);
        }
        catch (BadElementException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try
        {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
