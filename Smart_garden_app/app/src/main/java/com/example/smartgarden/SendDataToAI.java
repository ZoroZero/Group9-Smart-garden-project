package com.example.smartgarden;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
public class SendDataToAI  implements Runnable{
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://169.254.20.224:5000/api/post_some_data";
    protected Vector<Double> results = new Vector<>();
    protected Vector<String> dates = new Vector<>();
    protected Double AI_result ;
    public SendDataToAI(Vector<Double> results, Vector<String> dates){
        this.results = results;
        this.dates = dates;
    }

    @Override
    public void run() {
          JSONObject json_test = new JSONObject();
        try {
            int len = this.results.size();
            for(int i = 0 ; i < len; i++)
        {
            json_test.put(this.dates.get(i), this.results.get(i));
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, String.valueOf(json_test));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response responses = null;

            int responsesCode = 0;



            responses = client.newCall(request).execute();



            if ((responsesCode = responses.code()) == 200){

                String jsonData = responses.body().string();

                JSONObject json = new JSONObject(jsonData);

                this.AI_result = json.getDouble("result");


            }

        } catch (IOException | JSONException e) {
            Log.e("errors",e.toString());
            e.printStackTrace();
        }
    }
}
