
package com.example.smartgarden;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReportDateFragment extends Fragment {
    ImageButton date_exit;
    EditText date_write;
    Button date_enter;
    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_date_setting, container, false);

        date_exit = view.findViewById(R.id.date_exiting);
        date_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.date_exiting){
                    returnViewReport();
                }
            }
        });
        date_write = view.findViewById(R.id.date_writing);

        date_enter = view.findViewById(R.id.date_entering);

        date_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.date_entering){
                    date = date_write.getText().toString();
                    returnViewReport();
                }
            }
        });

        return view;
    }

    public void returnViewReport(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), MainActivity.class);
        intent.putExtra("set_date", date);
        getActivity().startActivity(intent);
    }
}
