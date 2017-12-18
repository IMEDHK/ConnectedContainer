package com.hakimi.ConnexionInscription.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.hakimi.ConnexionInscription.R;
import com.hakimi.ConnexionInscription.adapters.ContainerAdapter;
import com.hakimi.ConnexionInscription.model.Contenair;


import java.util.ArrayList;
import java.util.List;


public class ContainerActivity extends AppCompatActivity {

    List<Contenair> contenairList;
    SQLiteDatabase mDatabase;
    ListView listViewEmployees;
    ContainerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);




        listViewEmployees = (ListView) findViewById(R.id.listViewEmployees);
        contenairList = new ArrayList<>();

        //opening the database
        mDatabase = openOrCreateDatabase(HomeActivity.DATABASE_NAME, MODE_PRIVATE, null);

        //this method will display the employees in the list
        showEmployeesFromDatabase();
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);


                    }
                },3000);
            }
        });



    }

    private void showEmployeesFromDatabase() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor cursorContenairs = mDatabase.rawQuery("SELECT * FROM containers", null);

        //if the cursor has some data
        if (cursorContenairs.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                contenairList.add(new Contenair(
                        cursorContenairs.getInt(0),
                        cursorContenairs.getString(1),
                        cursorContenairs.getString(2),
                        cursorContenairs.getString(3),
                        cursorContenairs.getString(4)
                ));
            } while (cursorContenairs.moveToNext());
        }
        //closing the cursor
        cursorContenairs.close();

        //creating the adapter object
        adapter = new ContainerAdapter(this, R.layout.list_layout_container, contenairList, mDatabase);

        //adding the adapter to listview
        listViewEmployees.setAdapter(adapter);
    }

}
