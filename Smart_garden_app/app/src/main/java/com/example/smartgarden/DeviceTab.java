package app.advance.hcmut.cse.smartgardensystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class DeviceTab extends AppCompatActivity implements View.OnClickListener {
    Button device_list;
    ImageButton menu0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_tab);

        device_list = findViewById(R.id.textView2);
        device_list.setOnClickListener(this);

        menu0 = findViewById(R.id.btn_menu0);
        menu0.setOnClickListener(this);

        selectFragment();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textView2){
            selectFragment();
        }
        if (view.getId() == R.id.btn_menu0){
            menu();
        }
    }

    Fragment fragment = null;
    String fragmentTag = "";
    Class fragmentClass = null;
    int menuState;

    public void selectFragment() {
        fragmentClass = DeviceFragment.class;
        fragmentTag = "DeviceFragment";

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            Bundle bundle = new Bundle();

            bundle.putString("fragmentTag", fragmentTag);

            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_content, fragment).commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            fragmentManager.beginTransaction().replace(R.id.device_tab, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
