package app.advance.hcmut.cse.smartgardensystem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private final Context context;

    private List<DeviceModel> elements;
    FragmentManager fragmentManager;

    public ListAdapter(Context c, List<DeviceModel> list, FragmentManager fragmentManager) {
        this.context = c;
        this.elements = list;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.layout_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.textName.setText(elements.get(position).getDeviceName());
        holder.textDeviceID.setText(elements.get(position).getDeviceId());
        holder.textDeviceType.setText(elements.get(position).getDeviceType());
        holder.textUserID.setText(elements.get(position).getUserId());

//        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//            @Override
//            public void onFocusChange(View v, boolean b){
//                holder.itemView.setSelected(b);
//                if(b){
//                    holder.itemView.setBackgroundColor(Color.CYAN);
//                } else {
//                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//                }
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println(position);
//                if (position != -1) {
//                    Fragment fragment = null;
//                    Class fragmentClass = DeviceItemFragment.class;
//                    try {
//                        fragment = (Fragment) fragmentClass.newInstance();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("link", elements.get(position).getPath());
//                        fragment.setArguments(bundle);
//
//                        fragmentManager.beginTransaction().replace(R.id.device_tab, fragment).commitAllowingStateLoss();
//                    } catch (InstantiationException e){
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
                goToDeviceItem();
            }
        });
    }

    public void goToDeviceItem(){
        Intent intent = new Intent(context, DeviceItem.class);
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;
        TextView textDeviceID;
        TextView textDeviceType;
        TextView textUserID;

        public ViewHolder(View view) {
            super(view);

            textName = view.findViewById(R.id.txt_device_title);
            textDeviceID = view.findViewById(R.id.txt_device_id);
            textDeviceType = view.findViewById(R.id.txt_device_type);
            textUserID = view.findViewById(R.id.txt_user_id);
        }
    }

}
