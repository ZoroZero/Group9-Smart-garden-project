package app.advance.hcmut.cse.smartgardensystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.List;

public class DeviceFragment extends Fragment {
    VerticalGridView gridViewDevice;
    FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        gridViewDevice = view.findViewById(R.id.gridViewDevice);

        List<DeviceModel> aList = new ArrayList<>();
        DeviceModel Dlist = new DeviceModel();
        Dlist.setDeviceName("Device name 1");
        Dlist.setDeviceId("Device Id 1");
        Dlist.setDeviceType("Device Type 1");
        Dlist.setUserId("User ID 1");
        aList.add(Dlist);

        Dlist = new DeviceModel();
        Dlist.setDeviceName("Device name 2");
        Dlist.setDeviceId("Device Id 2");
        Dlist.setDeviceType("Device Type 2");
        Dlist.setUserId("User ID 2");
        aList.add(Dlist);

        Dlist = new DeviceModel();
        Dlist.setDeviceName("Device name 3");
        Dlist.setDeviceId("Device Id 3");
        Dlist.setDeviceType("Device Type 3");
        Dlist.setUserId("User ID 3");
        aList.add(Dlist);

        Dlist = new DeviceModel();
        Dlist.setDeviceName("Device name 4");
        Dlist.setDeviceId("Device Id 4");
        Dlist.setDeviceType("Device Type 4");
        Dlist.setUserId("User ID 4");
        aList.add(Dlist);

        Dlist = new DeviceModel();
        Dlist.setDeviceName("Device name 5");
        Dlist.setDeviceId("Device Id 5");
        Dlist.setDeviceType("Device Type 5");
        Dlist.setUserId("User ID 5");
        aList.add(Dlist);

        fragmentManager = getFragmentManager();
        //Create Adapter
        ListAdapter adapter = new ListAdapter(getActivity(), aList, fragmentManager);
        gridViewDevice.setAdapter(adapter);

        return view;
    }
}