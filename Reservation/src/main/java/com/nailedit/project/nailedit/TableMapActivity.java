package com.nailedit.project.nailedit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nailedit.project.nailedit.Adapter.ListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by gotic_000 on 4/16/2018.
 */

public class TableMapActivity extends Activity {

    ArrayList<String> listItems;

    ListAdapter adapter;
    GridView gridview;

    String idCustomer;

    boolean stop = false;
    boolean stop2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_map);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            idCustomer = "";
        }else {
            idCustomer = extras.getString("id");
            Toast.makeText(TableMapActivity.this, "Customer: " + idCustomer, Toast.LENGTH_SHORT).show();
        }

        SharedPreferences prefss = getSharedPreferences("TIME", MODE_PRIVATE);
        String restoredValue = prefss.getString("date_time", null);

        long valueCompared = 0;
        try{
            valueCompared = System.currentTimeMillis() - Long.valueOf(restoredValue);
        }catch (Exception e){
            e.printStackTrace();
        }

        gridview = (GridView) findViewById(R.id.gridview);

        if(valueCompared < 600000){
            // less than 10 minutes = keep reservations
            //Retrieve the values
            SharedPreferences pref = getSharedPreferences("ITEMS", 0);
            String str = pref.getString("items","");

            Type type = new TypeToken<ArrayList<String>>() { }.getType();
            ArrayList<String> restoreData = new Gson().fromJson(str, type);
            listItems = restoreData;

            adapter = new ListAdapter(this,R.layout.list_table_map_layout,R.id.txtid, listItems);
            gridview.setAdapter(adapter);

            // and reset the reservations after minutes left
            final Handler handler = new Handler();
            Runnable runnable = new Runnable(){
                //handler.postDelayed( new Runnable() {

                @Override
                public void run() {
                    if(stop2){
                        JSONArray jsonArray = getJSonData("table_map.json");
                        listItems = getArrayListFromJSONArray(jsonArray);
                        adapter = new ListAdapter(TableMapActivity.this,R.layout.list_table_map_layout,R.id.txtid, listItems);
                        adapter.notifyDataSetChanged();
                        gridview.setAdapter(adapter);
                        stop2 = false;
                    }
                }
            };
            if(stop2){
                handler.postDelayed(runnable, valueCompared);
            }

        }else {
            // more than 10 minutes = reset reservations
            JSONArray jsonArray = getJSonData("table_map.json");
            listItems = getArrayListFromJSONArray(jsonArray);
            adapter = new ListAdapter(this,R.layout.list_table_map_layout,R.id.txtid, listItems);
            gridview.setAdapter(adapter);
        }



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedItem = (int) (adapter.getItemId(i));
                String selectedItemValue = (String) (adapter.getItem(i));
                Toast.makeText(TableMapActivity.this, "Table " + selectedItem + " " + selectedItemValue, Toast.LENGTH_SHORT).show();

                if(selectedItemValue.equals("true")){
                    stop = true;
                    alertDialog(listItems, selectedItem, adapter);
                }
            }
        });

    }

    public void alertDialog(final ArrayList<String> listItems, final int selectedItem, final ListAdapter adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(TableMapActivity.this);

        builder.setTitle("Table choosing screen")
                .setMessage("Are you sure you want to select this table?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with selecting
                        listItems.set(selectedItem, "false");
                        adapter.notifyDataSetChanged();
                        gridview.setAdapter(adapter);

                        resetListAfter10Minutes(TableMapActivity.this);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public void resetListAfter10Minutes(Context context) {

        SharedPreferences.Editor editor = getSharedPreferences("TIME", MODE_PRIVATE).edit();
        editor.putString("date_time", String.valueOf(System.currentTimeMillis()));
        editor.apply();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable(){
        //handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                JSONArray jsonArray = getJSonData("table_map.json");
                listItems = getArrayListFromJSONArray(jsonArray);
                adapter = new ListAdapter(TableMapActivity.this,R.layout.list_table_map_layout,R.id.txtid, listItems);
                adapter.notifyDataSetChanged();
                gridview.setAdapter(adapter);
                stop = false;
            }
        };
        if(stop){
            stop2 = false;
            handler.postDelayed(runnable,600000);
        }

    }

    private JSONArray getJSonData(String fileName){

        JSONArray jsonArray=null;

        try {

            InputStream is = getResources().getAssets().open(fileName);
            int size = is.available();
            byte[] data = new byte[size];

            is.read(data);
            is.close();

            String json = new String(data, "UTF-8");

            jsonArray=new JSONArray(json);

        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException je){
            je.printStackTrace();
        }

        return jsonArray;
    }

    private ArrayList<String> getArrayListFromJSONArray(JSONArray jsonArray){

        ArrayList<String> aList=new ArrayList<>();

        try {
            if (jsonArray != null) {
                int lenght = jsonArray.length();
                for (int i = 0; i < lenght; i++) {
                    String a = jsonArray.getString(i);

                    aList.add(a);
                    //aList.add(jsonArray.getJSONObject(i));
                }
            }

        }catch (JSONException je){
            je.printStackTrace();
        }

        return  aList;
    }

    @Override
    protected void onStop() {
        super.onDestroy();

        String dataStr = new Gson().toJson(listItems);

        SharedPreferences.Editor editor = getSharedPreferences("ITEMS", 0).edit();
        editor.putString("items", String.valueOf(dataStr));
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        String dataStr = new Gson().toJson(listItems);

        SharedPreferences.Editor editor = getSharedPreferences("ITEMS", 0).edit();
        editor.putString("items", String.valueOf(dataStr));
        editor.commit();

    }

}


