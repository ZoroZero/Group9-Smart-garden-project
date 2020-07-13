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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class GetValueToday implements Runnable {
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://169.254.20.224/duyapi/v1/getValueToday.php";
    private String device_id;
    protected Vector<Double> results = new Vector<>();
    public GetValueToday(String device_id){
        this.device_id = device_id;
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



                JSONArray jsonArray = json.getJSONArray("date");

                int length = jsonArray.length();
                boolean finalMeasure = false ;

                double sum = jsonArray.getJSONObject(0).getDouble("measurement");
                String first_date = jsonArray.getJSONObject(0).getString("date");
                SimpleDateFormat first_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date firstDate = first_date_format.parse(first_date);
                Calendar first_cal = Calendar.getInstance();
                first_cal.setTime(firstDate);
                int thisHour = first_cal.get(Calendar.HOUR_OF_DAY);
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
                    int nextHour = cal.get(Calendar.HOUR_OF_DAY);

                    if(nextHour != thisHour) {
                        this.results.add(sum);
                        sum = temp;
                    }
                    else {
                        sum += temp;
                    }
                    thisHour = nextHour;
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
