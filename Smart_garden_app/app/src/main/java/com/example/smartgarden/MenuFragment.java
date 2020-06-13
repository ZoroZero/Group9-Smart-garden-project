package app.advance.hcmut.cse.smartgardensystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MenuFragment extends Fragment implements View.OnClickListener{
    ImageButton viewDevice;
    TextView txtViewDevice;
    ImageButton searchDevice;
    TextView txtSearchDevice;
    ImageButton addDevice;
    TextView txtAddDevice;
    ImageButton viewReport;
    TextView txtViewReport;
    ImageButton notification;
    TextView txtNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);

        viewDevice = view.findViewById(R.id.btn_menu_view_device);
        viewDevice.setOnClickListener(this);
        txtViewDevice = view.findViewById(R.id.txt_menu_view_device);
        txtViewDevice.setOnClickListener(this);
        searchDevice = view.findViewById(R.id.btn_menu_search_device);
        searchDevice.setOnClickListener(this);
        txtSearchDevice = view.findViewById(R.id.txt_menu_search_device);
        txtSearchDevice.setOnClickListener(this);
        addDevice = view.findViewById(R.id.btn_menu_add_device);
        addDevice.setOnClickListener(this);
        txtAddDevice = view.findViewById(R.id.txt_menu_add_new_device);
        txtAddDevice.setOnClickListener(this);
        viewReport = view.findViewById(R.id.btn_menu_view_report);
        viewReport.setOnClickListener(this);
        txtViewReport = view.findViewById(R.id.txt_menu_view_report);
        txtViewReport.setOnClickListener(this);
        notification = view.findViewById(R.id.btn_menu_notification);
        notification.setOnClickListener(this);
        txtNotification = view.findViewById(R.id.txt_menu_notification);
        txtNotification.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if ((view.getId() == R.id.btn_menu_view_device) ||
                (view.getId() == R.id.txt_menu_view_device))
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(), DeviceTab.class);
            getActivity().startActivity(intent);
        }

        if ((view.getId() == R.id.btn_menu_search_device) ||
                (view.getId() == R.id.txt_menu_search_device))
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(), DeviceSearch.class);
            getActivity().startActivity(intent);
        }

        if ((view.getId() == R.id.btn_menu_add_device) ||
                (view.getId() == R.id.txt_menu_add_new_device))
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(), RegisterDeviceSetting.class);
            getActivity().startActivity(intent);
        }

        if ((view.getId() == R.id.btn_menu_view_report) ||
                (view.getId() == R.id.txt_menu_view_report))
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ViewReport.class);
            getActivity().startActivity(intent);
        }

        if ((view.getId() == R.id.btn_menu_notification) ||
                (view.getId() == R.id.txt_menu_notification))
        {
            Intent intent = new Intent();
            intent.setClass(getActivity(), Notification.class);
            getActivity().startActivity(intent);
        }
    }
}