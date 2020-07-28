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
import java.util.Vector;
import Helper.Constants;

public class GetDataFromURL implements Runnable {
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://" + Constants.DATABASE_IP + Constants.GET_MEASUREMENT;
    private String device_id;
    private String type;
    public Vector<String> date = new Vector<>();
    public Vector<Double> results = new Vector<>();
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

            Response responses;

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

                    String LIGHT = ViewReport.LIGHT;
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
                        String TEMP = ViewReport.TEMP;
                        if (this.type.equals(TEMP))
                            this.results.add(temperature);
                        else
                            this.results.add(humidity);
                    }
                    String this_date = jsonArray.getJSONObject(i).getString("date");
                    this.date.add(this_date);
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}