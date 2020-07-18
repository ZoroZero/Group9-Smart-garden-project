package com.example.smartgarden;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

public class GetDataFromURL implements Runnable {
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://169.254.20.224/duyapi/v1/getDeviceMeasurement.php";
    private String device_id;
    private String type;
    private final String TEMP_HUMIDITY = MainActivity.TEMP_HUMIDITY;
    private final String TEMP = MainActivity.TEMP;
    private final String HUMIDITY = MainActivity.HUMIDITY;
    private final String LIGHT = MainActivity.LIGHT;
    protected Vector<String> date = new Vector<>();
    protected Vector<Double> results = new Vector<>();
    public GetDataFromURL(String device_id, String type){
        this.device_id = device_id;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("device_id",device_id)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response responses = null;

            int responsesCode = 0;



            responses = client.newCall(request).execute();



            if ((responsesCode = responses.code()) == 200){

                String jsonData = responses.body().string();

                JSONObject json = new JSONObject(jsonData);



                JSONArray jsonArray = json.getJSONArray("reading");
                int length = jsonArray.length();
                for(int i = 0 ; i < length ; i ++)
                {
                    String temp = jsonArray.getJSONObject(i).getString("measurement");

                    if (this.type.equals(LIGHT))
                    {
                        double measure = Double.parseDouble(temp);
                        this.results.add(measure);
                    }
                    else
                    {
                        String[] temp_humi = temp.split(":");
                        double temperature = Double.parseDouble(temp_humi[0]);
                        double humidity = Double.parseDouble(temp_humi[1]);
                        if(this.type.equals(TEMP))
                            this.results.add(temperature);
                        else
                            this.results.add(humidity);
                    }
                    String this_date = jsonArray.getJSONObject(i).getString("date");
                    this.date.add(this_date);
                }
                Log.e("fix", String.valueOf(this.results));
            }

        } catch (IOException | JSONException e) {
            Log.e("errors",e.toString());
            e.printStackTrace();
        }
    }
}