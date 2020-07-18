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
    private String url = "http://169.254.20.224/duyapi/v1/getInputDevicesWithType.php";
    private String type;
    private String user_id;
    protected Vector<String> results = new Vector<>();
    public GetDeviceByType(String user_id, String type){
        this.user_id = user_id;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("user_id", user_id)
                    .add("type",type)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response responses = null;

            int responsesCode = 0;

            Log.e("test1", String.valueOf(user_id));
            Log.e("test_2",type);

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
            Log.e("test_list", String.valueOf(this.results));

        } catch (IOException | JSONException e) {
            Log.e("errors",e.toString());
            e.printStackTrace();
        }
    }
}