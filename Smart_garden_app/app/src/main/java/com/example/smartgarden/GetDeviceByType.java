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

public class GetDeviceByType implements Runnable {
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://169.254.20.224/duyapi/v1/getDeviceByType.php";
    private String type;
    protected Vector<String> results = new Vector<>();
    public GetDeviceByType(String type){
        this.type = type;
    }

    @Override
    public void run() {
        try {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("type",type)
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
                    String temp = jsonArray.getJSONObject(i).getString("input_device_id");
                    this.results.add(temp);
                }
            }

        } catch (IOException | JSONException e) {
            Log.e("errors",e.toString());
            e.printStackTrace();
        }
    }
}