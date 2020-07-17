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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class GetThisMonthValue implements Runnable {
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://169.254.20.224/duyapi/v1/getValueThisMonth.php";
    private String device_id;
    private String type;
    protected Vector<Double> results = new Vector<>();
    public GetThisMonthValue(String device_id, String type){
        this.device_id = device_id;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("device_id",device_id)
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



                JSONArray jsonArray = json.getJSONArray("date");

                int length = jsonArray.length();
                boolean finalMeasure = false ;

                double sum = jsonArray.getJSONObject(0).getDouble("measurement");
                String first_date = jsonArray.getJSONObject(0).getString("date");
                SimpleDateFormat first_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date firstDate = first_date_format.parse(first_date);
                Calendar first_cal = Calendar.getInstance();
                first_cal.setTime(firstDate);
                int today = first_cal.get(Calendar.DAY_OF_MONTH);
                for(int i = 0 ; i < length ; i ++)
                {
                    if(i == length - 1) {
                        this.results.add(sum);
                        break;
                    }
                    double temp = jsonArray.getJSONObject(i + 1).getDouble("measurement");
                    String this_date = jsonArray.getJSONObject(i + 1).getString("date");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date parsedDate = dateFormat.parse(this_date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    int nextday = cal.get(Calendar.DAY_OF_MONTH);

                    if(nextday != today) {
                        this.results.add(sum);
                        sum = temp;
                    }
                    else {
                        sum += temp;
                    }
                    today = nextday;
                }
                }

            } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

}
