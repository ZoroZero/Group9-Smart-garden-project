package GardenManagement.PlantManagement;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import Report.ViewReport;
import com.example.smartgarden.R;

import org.json.JSONArray;
import org.json.JSONObject;

import Database.Garden_Database_Control;
import Helper.VolleyCallBack;
import Login_RegisterUser.HomeActivity;
import Login_RegisterUser.LoginActivity;
import Login_RegisterUser.UserLoginManagement;
import Registeration.RegisterDeviceSearchActivity;
import Registeration.RegisterPlant;

public class PlantListView extends AppCompatActivity implements VolleyCallBack {
    private GridView plantListView;
    private String[] plant_name;
    private String[] plant_buy_date;
    private String[] plant_buy_location;
    private String[] plant_amount;
    private TextView totalPlantTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list_view);

        totalPlantTV = findViewById(R.id.PlantDetail_TotalPlant_TV);
        plantListView = findViewById(R.id.PlantList_PLantListView);
        Garden_Database_Control.FetchPlantsInfo(this, this);

        // Setup menu
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.normal_menu, menu);
        menu.add(0, 1, 1,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_home_black_24dp),
                        getResources().getString(R.string.home)));

        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_list_black_24dp),
                        getResources().getString(R.string.view_plant_list)));

        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_add_black_24dp),
                        getResources().getString(R.string.register_device)));

        menu.add(0, 4, 4,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_local_florist_black_24dp),
                        getResources().getString(R.string.register_plant)));

        menu.add(0, 5, 5,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_view_report_black_24dp),
                        getResources().getString(R.string.view_report)));

        menu.add(0, 6, 6,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_settings_black_24dp),
                        getResources().getString(R.string.setting)));

        menu.add(0, 7, 7, menuIconWithText(getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp),
                getResources().getString(R.string.log_out)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
            case android.R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            case 2:
                startActivity(new Intent(this, PlantListView.class));
                finish();
                return true;
            case 3:
                startActivity(new Intent(getApplicationContext(), RegisterDeviceSearchActivity.class));
                finish();
                return true;
            case 4:
                startActivity(new Intent(getApplicationContext(), RegisterPlant.class));
                finish();
                return true;
            case 5:
                startActivity(new Intent(getApplicationContext(), ViewReport.class));
                finish();
                return true;
            case 6:
                return false;
            case 7:
                UserLoginManagement.getInstance(this).logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("error")) {
                JSONArray jsonArray = jsonObject.getJSONArray("plant_list");
                Log.i("JSON Array", String.valueOf(jsonArray));
                plant_name = new String[jsonArray.length()];
                plant_buy_date = new String[jsonArray.length()];
                plant_buy_location = new String[jsonArray.length()];
                plant_amount = new String[jsonArray.length()];
                int total_plant = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Log.i("JSON Object", String.valueOf(obj));
                    plant_name[i] = obj.getString("Plant_name");
                    plant_buy_date[i] = obj.getString("Buy_date");
                    plant_buy_location[i] = obj.getString("Buy_location");
                    plant_amount[i] = obj.getInt("Amount") +"";
                    total_plant += obj.getInt("Amount");
                }
                PlantDetailAdapter itemAdapter = new PlantDetailAdapter(getApplicationContext(), plant_name,
                        plant_buy_date, plant_amount);
                plantListView.setAdapter(itemAdapter);
                totalPlantTV.setText(total_plant + " plants");

                plantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent showPlantDetail = new Intent(getApplicationContext(), PlantDetailActivity.class);

                        showPlantDetail.putExtra("plant_detail.plant_name",
                                plant_name[position]);
                        showPlantDetail.putExtra("plant_detail.buy_date",
                                plant_buy_date[position]);
                        showPlantDetail.putExtra("plant_detail.buy_location",
                                plant_buy_location[position]);
                        showPlantDetail.putExtra("plant_detail.amount",
                                plant_amount[position]);

                        startActivity(showPlantDetail);
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
