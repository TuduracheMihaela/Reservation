package com.nailedit.project.nailedit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nailedit.project.nailedit.Model.Customer;
import com.nailedit.project.nailedit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gotic_000 on 4/16/2018.
 */

public class ListAdapter extends ArrayAdapter<String> {

    int vg;
    ArrayList<String> list;
    Context context;

    public ListAdapter(Context context, int vg, int id, ArrayList<String> list){
        super(context,vg, id,list);

        this.context=context;
        this.vg=vg;
        this.list=list;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(vg, parent, false);

        LinearLayout layoutTable = (LinearLayout)itemView.findViewById(R.id.layoutTable);
        TextView txtId = (TextView)itemView.findViewById(R.id.txtid);

        for(int i = 0; i < list.size(); i++){
            //txtId.setText(list.get(position));
            txtId.setText("Table 0" + position);

            //if(txtId.getText().equals("false")){
            if(list.get(position).equals("false")){
                layoutTable.setBackgroundColor(context.getResources().getColor(R.color.red_transparent80));
            }else {
                layoutTable.setBackgroundColor(context.getResources().getColor(R.color.green_transparent80));
            }
        }

        return itemView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
