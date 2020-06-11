package Userprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class PlantListView extends AppCompatActivity implements VolleyCallBack {
    private ListView plantListView;
    private String[] plant_name;
    private String[] plant_buy_date ;
    private String[] plant_buy_location ;
    private String[] plant_amount ;
    private String[] linked_sensor_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list_view);

        plantListView = findViewById(R.id.PlantList_PLantListView);
        Garden_Database_Control.FetchPlantsInfo(this, this);
    }

    @Override
    public void onSuccessResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("error")) {
                JSONArray jsonArray = jsonObject.getJSONArray("plant_list");
                Log.i("JSON Array", String.valueOf(jsonArray));
                //String[] plant_name = new String[jsonArray.length()];
                plant_name = new String[jsonArray.length()];
                plant_buy_date = new String[jsonArray.length()];
                plant_buy_location = new String[jsonArray.length()];
                plant_amount = new String[jsonArray.length()];
                linked_sensor_id = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Log.i("JSON Object", String.valueOf(obj));
                    plant_name[i] = obj.getString("Plant_name");
                    plant_buy_date[i] = obj.getString("Buy_date");
                    plant_buy_location[i] = obj.getString("Buy_location");
                    plant_amount[i] = obj.getInt("Amount") +"";
                    linked_sensor_id[i] = obj.getString("linked_sensor_id");
                }
                PlantDetailAdapter itemAdapter = new PlantDetailAdapter(getApplicationContext(), plant_name, plant_buy_date, plant_amount);
                //UserLoginManagement.getInstance(this).storeUserDevices(get_device_id, get_device_name, linked_device_id, linked_device_name, device_type);
                plantListView.setAdapter(itemAdapter);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
