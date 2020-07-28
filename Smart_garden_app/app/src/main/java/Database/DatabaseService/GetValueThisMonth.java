package Database.DatabaseService;

import Report.ViewReport;
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

import Helper.Constants;

public class GetValueThisMonth implements Runnable{
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://" + Constants.DATABASE_IP + Constants.GET_VALUE_BY_MONTH;
    private String device_id;
    private String type;
    private String query_type  = "";
    private final String TEMP_HUMIDITY = ViewReport.TEMP_HUMIDITY;
    private final String TEMP = ViewReport.TEMP;
    private final String HUMIDITY = ViewReport.HUMIDITY;
    private final String LIGHT = ViewReport.LIGHT;
    public Vector<Double> results = new Vector<>();
    public Vector<String> days = new Vector<>();
    public GetValueThisMonth(String device_id, String type){
        this.device_id = device_id;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            if(this.type.equals(TEMP) || this.type.equals(HUMIDITY))
            {
                query_type = TEMP_HUMIDITY;
            }
            else
                query_type = this.type;
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
                String first_measurement = jsonArray.getJSONObject(0).getString("measurement");
                double sum;
                if (this.type.equals(LIGHT))
                {
                    double measure = Double.parseDouble(first_measurement);
                    sum = measure;
                }
                else
                {
                    String[] temp_humi = first_measurement.split(":");
                    double temperature = Double.parseDouble(temp_humi[0]);
                    double humidity = Double.parseDouble(temp_humi[1]);
                    if(this.type.equals(TEMP))
                        sum = temperature;
                    else
                        sum = humidity;
                }
                String first_date = jsonArray.getJSONObject(0).getString("date");
                SimpleDateFormat first_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date firstDate = first_date_format.parse(first_date);
                Calendar first_cal = Calendar.getInstance();
                first_cal.setTime(firstDate);
                int today = first_cal.get(Calendar.DAY_OF_MONTH);
                double count = 1.0 ;
                for(int i = 0 ; i < length ; i ++)
                {
                    if(i == length - 1) {
                        this.results.add(sum/count);
                        this.days.add(String.valueOf(today));
                        break;
                    }
                    String temp = jsonArray.getJSONObject(i + 1).getString("measurement");
                    double temp_value;
                    if (this.type.equals(LIGHT))
                    {
                        double measure = Double.parseDouble(temp);
                        temp_value = measure;
                    }
                    else
                    {
                        String[] temp_humi = temp.split(":");
                        double temperature = Double.parseDouble(temp_humi[0]);
                        double humidity = Double.parseDouble(temp_humi[1]);
                        if(this.type.equals(TEMP))
                            temp_value = temperature;
                        else
                            temp_value = humidity;
                    }
                    String this_date = jsonArray.getJSONObject(i + 1).getString("date");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date parsedDate = dateFormat.parse(this_date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    int nextday = cal.get(Calendar.DAY_OF_MONTH);

                    if(nextday != today) {
                        this.results.add(sum/count);
                        this.days.add(String.valueOf(today));
                        sum = temp_value;
                        count = 1 ;
                    }
                    else {
                        sum += temp_value;
                        count += 1;
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
