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

public class GetValueThisYear implements Runnable {
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://169.254.20.224/duyapi/v1/getValueThisYear.php";
    private String device_id;
    private String type;
    private String query_type  = "";
    protected Vector<Double> results = new Vector<>();
    protected Vector<String> months = new Vector<>();
    public GetValueThisYear(String device_id, String type){
        this.device_id = device_id;
        this.type = type;
    }

    @Override
    public void run() {
        if(this.type == "T" || this.type == "H")
        {
            query_type ="TH";
        }
        else
            query_type = this.type;
        try {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("device_id",device_id)
                    .add("type",query_type)
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
                String first_measurement = jsonArray.getJSONObject(0).getString("measurement");
                double sum;
                if (this.type == "L")
                {
                    double measure = Double.parseDouble(first_measurement);
                    sum = measure;
                }
                else
                {
                    String[] temp_humi = first_measurement.split(":");
                    double temperature = Double.parseDouble(temp_humi[0]);
                    double humidity = Double.parseDouble(temp_humi[1]);
                    if(this.type == "T")
                        sum = temperature;
                    else
                        sum = humidity;
                }

                String first_date = jsonArray.getJSONObject(0).getString("date");
                SimpleDateFormat first_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date firstDate = first_date_format.parse(first_date);
                Calendar first_cal = Calendar.getInstance();
                first_cal.setTime(firstDate);
                int thisMonth = first_cal.get(Calendar.MONTH) + 1;

                double count = 1.0 ;
                for(int i = 0 ; i < length ; i ++)
                {
                    if(i == length - 1) {
                        this.results.add(sum/count);
                        this.months.add(String.valueOf(thisMonth));
                        break;
                    }
                    String temp = jsonArray.getJSONObject(i + 1).getString("measurement");
                    double temp_value;
                    if (this.type == "L")
                    {
                        double measure = Double.parseDouble(temp);
                        temp_value = measure;
                    }
                    else
                    {
                        String[] temp_humi = temp.split(":");
                        double temperature = Double.parseDouble(temp_humi[0]);
                        double humidity = Double.parseDouble(temp_humi[1]);
                        if(this.type == "T")
                            temp_value = temperature;
                        else
                            temp_value = humidity;
                    }
                    String this_date = jsonArray.getJSONObject(i + 1).getString("date");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date parsedDate = dateFormat.parse(this_date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    int nextMonth = cal.get(Calendar.MONTH) + 1;

                    if(nextMonth != thisMonth) {
                        this.results.add(sum/count);
                        this.months.add(String.valueOf(thisMonth));
                        sum = temp_value;
                        count = 1 ;
                    }
                    else {
                        sum += temp_value;
                        count += 1;
                    }
                    thisMonth = nextMonth;
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
