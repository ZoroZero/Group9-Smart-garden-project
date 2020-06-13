package app.advance.hcmut.cse.smartgardensystem;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class DeviceItem extends AppCompatActivity implements View.OnClickListener{
    Button btn_return;
    ImageButton menu1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_item);

        btn_return = findViewById(R.id.item_returnButton);
        btn_return.setOnClickListener(this);

        menu1 = findViewById(R.id.btn_menu2);
        menu1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.item_returnButton){
            deviceTab_return();
        }
        if (view.getId() == R.id.btn_menu2){
            menu();
        }
    }

    public void deviceTab_return() {
        Intent intent = new Intent(this, DeviceTab.class);
        startActivity(intent);
    }

    Fragment fragment = null;
    String fragmentTag = "";
    Class fragmentClass = null;
    int menuState;

    public void menu() {
        menuState += 1;
        if (menuState == 1) {
            fragmentClass = MenuFragment.class;
            fragmentTag = "MenuFragment";
        } else {
            menuState = 0;
            fragmentClass = FragmentNothing.class;
            fragmentTag = "NothingFragment";
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            Bundle bundle = new Bundle();

            bundle.putString("fragmentTag", fragmentTag);

            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.device_item, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
