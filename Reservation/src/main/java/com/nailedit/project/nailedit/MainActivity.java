package com.nailedit.project.nailedit;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nailedit.project.nailedit.Adapter.ListAdapter;
import com.nailedit.project.nailedit.Model.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    SearchManager searchManager;

    SimpleAdapter adapter = null;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSONArray jsonArray = getJSonData("customer.json");

        ArrayList<HashMap<String, String>> listItems = getArrayListFromJSONArray(jsonArray);

        lv = (ListView) findViewById(R.id.listView);
        adapter = new SimpleAdapter(
                MainActivity.this
                , listItems
                , R.layout.list_layout, new String[] { "id", "firstName", "lastName" },
                new int[] { R.id.txtid, R.id.txtfirstname, R.id.txtlastname });
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String cid = ((TextView) view.findViewById(R.id.txtid)).getText().toString();

                // start detail activity
                Intent intent = new Intent(getApplicationContext(), TableMapActivity.class);
                intent.putExtra("id",cid);
                startActivity(intent);
            }
        });
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

    private ArrayList<HashMap<String, String>> getArrayListFromJSONArray(JSONArray jsonArray){
        ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    String id = c.getString("id");
                    String first_name = c.getString("customerFirstName");
                    String last_name = c.getString("customerLastName");

                    HashMap<String, String> item = new HashMap<>();
                    item.put("id", id);
                    item.put("firstName", first_name);
                    item.put("lastName", last_name);

                    itemList.add(item);
                }
            }

        }catch (JSONException je){je.printStackTrace();}

        return  itemList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        ComponentName cn = new ComponentName(this, MainActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

        searchView.setIconifiedByDefault(false);

        return true;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            searchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        MainActivity.this.adapter.getFilter().filter(query.trim());
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MainActivity.this.adapter.getFilter().filter(newText.trim());
        return false;
    }
}
